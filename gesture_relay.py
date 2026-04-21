import cv2
import mediapipe as mp
import requests
import time

ESP32_IP = "http://172.20.10.6"

mp_hands = mp.solutions.hands
mp_draw = mp.solutions.drawing_utils
hands = mp_hands.Hands(
    static_image_mode=False,
    max_num_hands=1,
    min_detection_confidence=0.7,
    min_tracking_confidence=0.5
)

cap = cv2.VideoCapture(0)

relay_state = False
last_sent = 0
cooldown = 2

def count_fingers(hand_landmarks):
    tips = [8, 12, 16, 20]
    fingers_up = 0
    if hand_landmarks.landmark[4].x < hand_landmarks.landmark[3].x:
        fingers_up += 1
    for tip in tips:
        if hand_landmarks.landmark[tip].y < hand_landmarks.landmark[tip - 2].y:
            fingers_up += 1
    return fingers_up

def send_command(state):
    try:
        url = f"{ESP32_IP}/relay/{'on' if state else 'off'}"
        requests.get(url, timeout=2)
        print(f"Relay {'ON' if state else 'OFF'} sent")
    except Exception as e:
        print(f"Connection Error: {e}")

print("Starting gesture control...")
print("Open Hand = Relay ON")
print("Closed Fist = Relay OFF")
print("Press Q to quit")

while True:
    ret, frame = cap.read()
    if not ret:
        print("Camera not found!")
        break

    frame = cv2.flip(frame, 1)
    rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    result = hands.process(rgb)

    finger_count = -1

    if result.multi_hand_landmarks:
        for handLms in result.multi_hand_landmarks:
            mp_draw.draw_landmarks(frame, handLms, mp_hands.HAND_CONNECTIONS)
            finger_count = count_fingers(handLms)

        now = time.time()
        if now - last_sent > cooldown:
            if finger_count == 5 and not relay_state:
                relay_state = True
                send_command(True)
                last_sent = now
            elif finger_count == 0 and relay_state:
                relay_state = False
                send_command(False)
                last_sent = now

    status = "ON" if relay_state else "OFF"
    color = (0, 255, 0) if relay_state else (0, 0, 255)
    cv2.putText(frame, f"Relay: {status}", (10, 50),
                cv2.FONT_HERSHEY_SIMPLEX, 1.2, color, 3)
    cv2.putText(frame, f"Fingers: {finger_count if finger_count >= 0 else '--'}",
                (10, 100), cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 255, 0), 2)
    cv2.putText(frame, "Open Hand=ON | Fist=OFF", (10, 140),
                cv2.FONT_HERSHEY_SIMPLEX, 0.6, (200, 200, 200), 1)

    cv2.imshow("Gesture Relay Control", frame)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()