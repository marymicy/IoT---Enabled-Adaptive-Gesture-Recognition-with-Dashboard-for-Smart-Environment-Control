package com.example.gestureassist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener, HandLandmarkerHelper.LandmarkerListener {

    private lateinit var previewView: PreviewView
    private lateinit var gestureText: TextView
    private lateinit var gestureEmoji: TextView
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var landmarkerHelper: HandLandmarkerHelper
    private lateinit var audioManager: AudioManager

    private var tts: TextToSpeech? = null
    private var lastSpokenText = ""
    private var lastGesture = ""
    private var isFrontCamera = true
    private var cameraProvider: ProcessCameraProvider? = null
    private var isFlashlightOn = false

    private val holdHandler = Handler(Looper.getMainLooper())
    private var holdRunnable: Runnable? = null
    private var lastGestureTime = 0L
    private val GESTURE_DEBOUNCE_MS = 1500L
    private val HOLD_REPEAT_MS = 400L
    private var contactIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewView  = findViewById(R.id.view_finder)
        gestureText  = findViewById(R.id.gesture_text)
        gestureEmoji = findViewById(R.id.gesture_emoji)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        cameraExecutor   = Executors.newSingleThreadExecutor()
        landmarkerHelper = HandLandmarkerHelper(this, this)
        tts = TextToSpeech(this, this)

        val permissions = listOf(Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE)
        if (!permissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 100)
        } else {
            startCamera()
        }

        // Flip button — now a LinearLayout pill
        findViewById<LinearLayout>(R.id.btn_flip).setOnClickListener {
            isFrontCamera = !isFrontCamera
            startCamera()
        }

        findViewById<android.widget.ImageButton>(R.id.btn_guide).setOnClickListener {
            startActivity(Intent(this, GestureGuideActivity::class.java))
        }

        findViewById<android.widget.ImageButton>(R.id.btn_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            val selector = if (isFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build().also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        landmarkerHelper.detectLiveStream(imageProxy.toBitmap(isFrontCamera))
                        imageProxy.close()
                    }
                }

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(this, selector, preview, imageAnalyzer)
            } catch (e: Exception) {
                Log.e("GestureAssist", "Camera binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onResults(result: HandLandmarkerResult) {
        runOnUiThread {
            if (result.landmarks().isEmpty()) {
                stopHold()
                gestureEmoji.text = "🖐️"
                gestureText.text  = "Show your hand"
                return@runOnUiThread
            }

            val lm = result.landmarks()[0]

            fun extended(tip: Int, pip: Int) = lm[tip].y() < lm[pip].y()
            fun thumbOut() = if (isFrontCamera) lm[4].x() > lm[3].x() else lm[4].x() < lm[3].x()

            val index  = extended(8, 6)
            val middle = extended(12, 10)
            val ring   = extended(16, 14)
            val pinky  = extended(20, 18)
            val thumb  = thumbOut()
            val fingerCount = listOf(index, middle, ring, pinky).count { it }

            val gesture = when {
                thumb && pinky && !index && !middle && !ring -> "SOS"
                thumb && !index && !middle && !ring && !pinky -> "THUMB_UP"
                !thumb && !index && !middle && !ring && !pinky -> "FIST"
                index && !middle && !ring && !pinky -> "ONE"
                index && middle && !ring && !pinky -> "TWO"
                index && middle && ring && !pinky -> "THREE"
                index && middle && ring && pinky && !thumb -> "FOUR"
                index && middle && ring && pinky && thumb -> "FIVE"
                else -> "UNKNOWN"
            }

            val now = System.currentTimeMillis()
            val isNew = gesture != lastGesture || (now - lastGestureTime) > GESTURE_DEBOUNCE_MS

            val (emoji, label) = when (gesture) {
                "ONE"      -> "☝️" to "Volume UP"
                "TWO"      -> "✌️" to "Volume DOWN"
                "THREE"    -> "🤟" to "Brightness UP"
                "FOUR"     -> "🖐️" to "Brightness DOWN"
                "FIVE"     -> "✋" to "Toggle Flashlight"
                "FIST"     -> "✊" to "Calling Contact"
                "THUMB_UP" -> "👍" to "Next Track"
                "SOS"      -> "🤙" to "SOS — Calling 112"
                else       -> "👋" to "Hand Detected"
            }

            gestureEmoji.text = emoji
            gestureText.text  = label

            if (isNew && gesture != "UNKNOWN") {
                stopHold()
                lastGesture     = gesture
                lastGestureTime = now
                speak(label)
                performGestureAction(gesture)
            }
        }
    }

    private fun performGestureAction(gesture: String) {
        when (gesture) {
            "ONE"      -> startHold { adjustVolume(AudioManager.ADJUST_RAISE) }
            "TWO"      -> startHold { adjustVolume(AudioManager.ADJUST_LOWER) }
            "THREE"    -> startHold { adjustBrightness(+10) }
            "FOUR"     -> startHold { adjustBrightness(-10) }
            "FIVE"     -> toggleFlashlight()
            "FIST"     -> callNextContact()
            "THUMB_UP" -> nextTrack()
            "SOS"      -> makeCall("112")
        }
    }

    private fun startHold(action: () -> Unit) {
        action()
        holdRunnable = object : Runnable {
            override fun run() { action(); holdHandler.postDelayed(this, HOLD_REPEAT_MS) }
        }
        holdHandler.postDelayed(holdRunnable!!, HOLD_REPEAT_MS * 2)
    }

    private fun stopHold() {
        holdRunnable?.let { holdHandler.removeCallbacks(it) }
        holdRunnable = null
    }

    private fun adjustVolume(direction: Int) {
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, AudioManager.FLAG_SHOW_UI)
    }

    private fun adjustBrightness(delta: Int) {
        val lp = window.attributes
        val current = if (lp.screenBrightness < 0) 0.5f else lp.screenBrightness
        lp.screenBrightness = (current + delta / 255f).coerceIn(0.01f, 1.0f)
        window.attributes = lp
    }

    private fun toggleFlashlight() {
        isFlashlightOn = !isFlashlightOn
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            try {
                cameraProvider?.unbindAll()
                val cam = cameraProvider?.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview)
                cam?.cameraControl?.enableTorch(isFlashlightOn)
            } catch (e: Exception) { Log.e("GestureAssist", "Torch error", e) }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun callNextContact() {
        val prefs    = getSharedPreferences("contacts", Context.MODE_PRIVATE)
        val contacts = (1..5).mapNotNull { prefs.getString("contact_$it", null) }.filter { it.isNotBlank() }
        if (contacts.isEmpty()) { speak("No contacts saved"); return }
        makeCall(contacts[contactIndex % contacts.size])
        contactIndex++
    }

    private fun makeCall(number: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$number")))
        }
    }

    private fun nextTrack() {
        val intent = Intent(Intent.ACTION_MEDIA_BUTTON)
        intent.putExtra(Intent.EXTRA_KEY_EVENT,
            android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_MEDIA_NEXT))
        sendOrderedBroadcast(intent, null)
    }

    private fun speak(text: String) {
        if (text != lastSpokenText) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            lastSpokenText = text
        }
    }

    override fun onError(error: String) { Log.e("GestureAssist", "MP Error: $error") }
    override fun onInit(status: Int) { if (status == TextToSpeech.SUCCESS) tts?.language = Locale.US }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopHold()
        cameraExecutor.shutdown()
        tts?.shutdown()
    }
}

fun ImageProxy.toBitmap(isFront: Boolean): Bitmap {
    val buffer = planes[0].buffer
    buffer.rewind()
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bmp.copyPixelsFromBuffer(java.nio.ByteBuffer.wrap(bytes))
    val matrix = Matrix().apply {
        postRotate(imageInfo.rotationDegrees.toFloat())
        if (isFront) postScale(-1f, 1f)
    }
    return Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true)
}