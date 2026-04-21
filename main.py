import cv2
import os
import time
import sys
import subprocess
import requests
import pyautogui
import screen_brightness_control as sbc
from flask import Flask, Response, send_file, jsonify
from threading import Thread

if sys.path[0] == os.getcwd():
    sys.path.pop(0)

try:
    import mediapipe as mp
    mp_hands = mp.solutions.hands
    mp_draw = mp.solutions.drawing_utils
except Exception as e:
    print(f"\n[!] IMPORT ERROR: {e}")
    exit()

app = Flask(__name__)

pyautogui.PAUSE = 0
pyautogui.FAILSAFE = False
screen_width, screen_height = pyautogui.size()

# ── Mode & shared state ───────────────────────────────────────────────────────
current_mode = "mouse"
relay_state   = False          # shared: distant control reads this

# ── ESP32 config ──────────────────────────────────────────────────────────────
ESP32_IP      = "http://172.20.10.6"
esp_cooldown  = 2
last_esp_sent = 0

# ── MediaPipe ─────────────────────────────────────────────────────────────────
hands = mp_hands.Hands(
    static_image_mode=False,
    max_num_hands=1,
    min_detection_confidence=0.7,
    min_tracking_confidence=0.7
)

# ── Mouse smoothing ───────────────────────────────────────────────────────────
prev_x, prev_y   = 0, 0
smoothing_factor = 0.25
last_action_time = 0
action_cooldown  = 0.5

# ── Android app ───────────────────────────────────────────────────────────────
APP_PACKAGE  = "com.example.gestureassist"
APP_ACTIVITY = "com.example.gestureassist.WelcomeActivity"


# ── ADB helper ───────────────────────────────────────────────────────────────
def find_adb():
    return r"C:\Users\vmicy\AppData\Local\Android\Sdk\platform-tools\adb.exe"


# ── ESP32 relay command (fire-and-forget in background thread) ────────────────
def send_relay(state: bool):
    def _send():
        try:
            url = f"{ESP32_IP}/relay/{'on' if state else 'off'}"
            requests.get(url, timeout=2)
            print(f"[ESP32] Relay {'ON' if state else 'OFF'} sent")
        except Exception as e:
            print(f"[ESP32] Connection error: {e}")
    Thread(target=_send, daemon=True).start()


# ── Finger count helper (used by distant mode) ────────────────────────────────
def count_fingers(hand_landmarks):
    tips = [8, 12, 16, 20]
    count = 0
    if hand_landmarks.landmark[4].x < hand_landmarks.landmark[3].x:
        count += 1
    for tip in tips:
        if hand_landmarks.landmark[tip].y < hand_landmarks.landmark[tip - 2].y:
            count += 1
    return count


# ── Finger status helper (used by mouse mode) ─────────────────────────────────
def get_finger_status(landmark_list, hand_label):
    fingers = []
    if hand_label == "Right":
        fingers.append(landmark_list[4][0] < landmark_list[3][0])
    else:
        fingers.append(landmark_list[4][0] > landmark_list[3][0])
    for tip in [8, 12, 16, 20]:
        fingers.append(landmark_list[tip][1] < landmark_list[tip - 2][1])
    return fingers


# ── Video feed (mode-aware) ───────────────────────────────────────────────────
def generate_frames():
    global prev_x, prev_y, last_action_time, relay_state, last_esp_sent

    cap = cv2.VideoCapture(0)
    cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
    cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)

    while True:
        success, frame = cap.read()
        if not success:
            break

        frame    = cv2.flip(frame, 1)
        h, w, _  = frame.shape
        rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        results  = hands.process(rgb_frame)

        if results.multi_hand_landmarks:
            for hand_landmarks, handedness in zip(
                results.multi_hand_landmarks, results.multi_handedness
            ):
                mp_draw.draw_landmarks(frame, hand_landmarks, mp_hands.HAND_CONNECTIONS)
                curr_t = time.time()

                # ── DISTANT CONTROL MODE ─────────────────────────────────────
                if current_mode == "distant":
                    fc = count_fingers(hand_landmarks)

                    if curr_t - last_esp_sent > esp_cooldown:
                        if fc == 5 and not relay_state:
                            relay_state   = True
                            last_esp_sent = curr_t
                            send_relay(True)
                        elif fc == 0 and relay_state:
                            relay_state   = False
                            last_esp_sent = curr_t
                            send_relay(False)

                    # On-frame overlay
                    status = "ON" if relay_state else "OFF"
                    color  = (0, 255, 0) if relay_state else (0, 0, 255)
                    cv2.putText(frame, f"Relay: {status}", (10, 50),
                                cv2.FONT_HERSHEY_SIMPLEX, 1.2, color, 3)
                    cv2.putText(frame, f"Fingers: {fc}", (10, 100),
                                cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 255, 0), 2)
                    cv2.putText(frame, "Open Hand=ON | Fist=OFF", (10, 140),
                                cv2.FONT_HERSHEY_SIMPLEX, 0.6, (200, 200, 200), 1)

                # ── MOUSE CONTROL MODE ───────────────────────────────────────
                else:
                    label  = handedness.classification[0].label
                    lm_list = [(int(lm.x * w), int(lm.y * h))
                               for lm in hand_landmarks.landmark]
                    up      = get_finger_status(lm_list, label)
                    up_count = sum(up)

                    if up[1] and not up[2] and not up[3]:
                        margin = 50
                        nx = screen_width  * (lm_list[8][0] - margin) / (w - 2 * margin)
                        ny = screen_height * (lm_list[8][1] - margin) / (h - 2 * margin)
                        curr_x = prev_x + (nx - prev_x) * smoothing_factor
                        curr_y = prev_y + (ny - prev_y) * smoothing_factor
                        pyautogui.moveTo(curr_x, curr_y, _pause=False)
                        prev_x, prev_y = curr_x, curr_y

                    elif up[1] and up[2] and not up[3]:
                        if curr_t - last_action_time > action_cooldown:
                            pyautogui.click()
                            last_action_time = curr_t

                    elif up[1] and up[2] and up[3] and not up[4]:
                        if curr_t - last_action_time > action_cooldown:
                            pyautogui.doubleClick()
                            last_action_time = curr_t

                    elif up_count == 5:
                        if curr_t - last_action_time > 1.0:
                            sbc.set_brightness(100)
                            last_action_time = curr_t
                    elif up_count == 0:
                        if curr_t - last_action_time > 1.0:
                            sbc.set_brightness(0)
                            last_action_time = curr_t

        ret, buffer = cv2.imencode('.jpg', frame)
        yield (b'--frame\r\n'
               b'Content-Type: image/jpeg\r\n\r\n'
               + buffer.tobytes()
               + b'\r\n')


# ── Routes ────────────────────────────────────────────────────────────────────
@app.route('/')
def index(): return send_file('index.html')

@app.route('/mouse_control')
def mouse_page(): return send_file('mouse_control.html')

@app.route('/distant_control')
def distant_page(): return send_file('distant_control.html')

@app.route('/video_feed')
def video_feed():
    return Response(generate_frames(),
                    mimetype='multipart/x-mixed-replace; boundary=frame')

@app.route('/set_mode/<mode>')
def set_mode(mode):
    global current_mode
    current_mode = mode
    print(f"[MODE] Switched to: {mode}")
    return jsonify({"status": "ok"})

@app.route('/relay_status')
def relay_status():
    """Polled by distant_control.html every second to update the UI."""
    return jsonify({"relay": relay_state})

@app.route('/open_app')
def open_app():
    adb = find_adb()
    if not adb:
        return jsonify({"status": "error",
                        "error": "ADB not found. Make sure Android Studio is installed."})
    try:
        check = subprocess.run([adb, 'devices'],
                               capture_output=True, text=True, timeout=5)
        lines   = [l.strip() for l in check.stdout.strip().splitlines()]
        devices = [l for l in lines[1:] if l and '\tdevice' in l and 'offline' not in l]

        if not devices:
            return jsonify({"status": "error",
                            "error": "No device connected. Enable USB Debugging and reconnect."})

        device_serial = devices[0].split('\t')[0].strip()
        result = subprocess.run(
            [adb, '-s', device_serial, 'shell', 'am', 'start',
             '-n', f'{APP_PACKAGE}/{APP_ACTIVITY}'],
            capture_output=True, text=True, timeout=10
        )

        # ADB prints "Starting: Intent {...}" on success — returncode 0 = success
        # Never show raw ADB output to the user
        if result.returncode != 0:
            return jsonify({"status": "error",
                            "error": "Could not launch app. Is the app installed on the device?"})

        return jsonify({"status": "ok"})

    except subprocess.TimeoutExpired:
        return jsonify({"status": "error",
                        "error": "ADB timed out. Try reconnecting the phone."})
    except Exception as e:
        return jsonify({"status": "error", "error": str(e)})


if __name__ == '__main__':
    print(f"[ADB] {find_adb()}")
    print(f"[ESP32] Target: {ESP32_IP}")
    app.run(host='0.0.0.0', port=5000, threaded=True)