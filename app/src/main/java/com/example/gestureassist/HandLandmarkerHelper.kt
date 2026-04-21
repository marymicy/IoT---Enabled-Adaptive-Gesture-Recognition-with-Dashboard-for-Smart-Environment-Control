package com.example.gestureassist

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

class HandLandmarkerHelper(
    val context: Context,
    var listener: LandmarkerListener? = null
) {
    private var handLandmarker: HandLandmarker? = null

    init { setupHandLandmarker() }

    private fun setupHandLandmarker() {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("hand_landmarker.task")
            .build()

        val options = HandLandmarker.HandLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setMinHandDetectionConfidence(0.5f)
            .setMinTrackingConfidence(0.5f)
            .setMinHandPresenceConfidence(0.5f)
            .setNumHands(1)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener(this::returnResult)
            .setErrorListener(this::returnError)
            .build()

        try {
            handLandmarker = HandLandmarker.createFromOptions(context, options)
        } catch (e: Exception) {
            listener?.onError("HandLandmarker init failed: ${e.message}")
        }
    }

    fun detectLiveStream(bitmap: Bitmap) {
        val mpImage: MPImage = BitmapImageBuilder(bitmap).build()
        handLandmarker?.detectAsync(mpImage, SystemClock.uptimeMillis())
    }

    // underscore prefix tells Kotlin the parameter is intentionally unused
    private fun returnResult(result: HandLandmarkerResult, @Suppress("UNUSED_PARAMETER") input: MPImage) {
        listener?.onResults(result)
    }

    private fun returnError(error: RuntimeException) {
        listener?.onError(error.message ?: "Unknown error")
    }

    interface LandmarkerListener {
        fun onError(error: String)
        fun onResults(result: HandLandmarkerResult)
    }
}