package com.example.gestureassist

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class GestureGuideActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scroll = ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0A1628"))
            setPadding(28, 52, 28, 52)
        }

        // ── "SYSTEM ONLINE" pill ─────────────────────────────────────────────
        val pill = TextView(this).apply {
            text = "● GESTURE GUIDE"
            textSize = 10f
            setTextColor(Color.parseColor("#7AB8FF"))
            typeface = Typeface.MONOSPACE
            letterSpacing = 0.18f
            setPadding(28, 8, 28, 8)
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(Color.parseColor("#1A1E3F80"))
                setStroke(1, Color.parseColor("#334DA3FF"))
                cornerRadius = 100f
            }
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.gravity = Gravity.CENTER_HORIZONTAL
            lp.setMargins(0, 0, 0, 20)
            layoutParams = lp
        }
        root.addView(pill)

        // ── Title ────────────────────────────────────────────────────────────
        root.addView(TextView(this).apply {
            text = "Gesture Guide"
            textSize = 26f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(0, 0, 0, 6)
            layoutParams = lp
        })

        root.addView(TextView(this).apply {
            text = "Point your hand at the camera clearly"
            textSize = 13f
            setTextColor(Color.parseColor("#607090"))
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(0, 0, 0, 36)
            layoutParams = lp
        })

        // ── Gesture cards ────────────────────────────────────────────────────
        val gestures = listOf(
            Triple("☝️", "1 Finger Up",       "Volume UP — hold to keep raising"),
            Triple("✌️", "2 Fingers Up",      "Volume DOWN — hold to keep lowering"),
            Triple("🤟", "3 Fingers Up",      "Brightness UP — hold to keep raising"),
            Triple("🖐️", "4 Fingers Up",      "Brightness DOWN — hold to keep lowering"),
            Triple("✋", "Open Hand",         "Toggle Flashlight ON / OFF"),
            Triple("✊", "Fist",              "Call next saved contact"),
            Triple("👍", "Thumbs Up",         "Skip to next music track"),
            Triple("🤙", "Pinky + Thumb",     "🚨 SOS — calls 112 immediately"),
        )

        gestures.forEach { (emoji, name, desc) ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                background = android.graphics.drawable.GradientDrawable().apply {
                    setColor(Color.parseColor("#CC102440"))
                    setStroke(1, Color.parseColor("#1A4DA3FF"))
                    cornerRadius = 18f
                }
                setPadding(20, 18, 20, 18)
                gravity = android.view.Gravity.CENTER_VERTICAL
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(0, 0, 0, 10)
                layoutParams = lp
            }

            // Left accent bar
            val accent = android.view.View(this).apply {
                setBackgroundColor(Color.parseColor("#4DA3FF"))
                val lp = LinearLayout.LayoutParams(3, LinearLayout.LayoutParams.MATCH_PARENT)
                lp.setMargins(0, 0, 16, 0)
                layoutParams = lp
            }

            val emojiView = TextView(this).apply {
                text = emoji
                textSize = 28f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(64, LinearLayout.LayoutParams.WRAP_CONTENT)
            }

            val textCol = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            textCol.addView(TextView(this).apply {
                text = name
                textSize = 15f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#C8DCF8"))
            })
            textCol.addView(TextView(this).apply {
                text = desc
                textSize = 12f
                setTextColor(Color.parseColor("#506070"))
                setPadding(0, 3, 0, 0)
            })

            card.addView(accent)
            card.addView(emojiView)
            card.addView(textCol)
            root.addView(card)
        }

        // ── Tips section ─────────────────────────────────────────────────────
        root.addView(TextView(this).apply {
            text = "TIPS"
            textSize = 10f
            setTextColor(Color.parseColor("#4DA3FF"))
            typeface = Typeface.MONOSPACE
            letterSpacing = 0.16f
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(0, 28, 0, 12)
            layoutParams = lp
        })

        val tips = listOf(
            "Use in good lighting for best detection",
            "Keep hand 20–50 cm from camera",
            "Fist gesture cycles through saved contacts",
            "Add quick-dial numbers in Settings ⚙️"
        )
        tips.forEach { tip ->
            root.addView(TextView(this).apply {
                text = "· $tip"
                textSize = 13f
                setTextColor(Color.parseColor("#506070"))
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(8, 0, 0, 10)
                layoutParams = lp
            })
        }

        scroll.addView(root)
        setContentView(scroll)
    }
}