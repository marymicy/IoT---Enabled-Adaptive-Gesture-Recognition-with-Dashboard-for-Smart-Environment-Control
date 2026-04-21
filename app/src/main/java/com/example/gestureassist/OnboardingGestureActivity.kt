package com.example.gestureassist

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class OnboardingGestureActivity : AppCompatActivity() {

    private val gestures = listOf(
        listOf("☝️", "1 Finger Up", "Volume UP", "Hold to keep raising"),
        listOf("✌️", "2 Fingers Up", "Volume DOWN", "Hold to keep lowering"),
        listOf("🤟", "3 Fingers Up", "Brightness UP", "Hold to keep raising"),
        listOf("🖐️", "4 Fingers Up", "Brightness DOWN", "Hold to keep lowering"),
        listOf("✋", "Open Hand", "Toggle Flashlight", "Turns flashlight on or off"),
        listOf("✊", "Fist", "Call Contact", "Cycles through saved numbers"),
        listOf("👍", "Thumbs Up", "Next Track", "Skips to next music track"),
        listOf("🤙", "Pinky + Thumb", "SOS", "Calls 112 immediately")
    )

    private var currentPage = 0
    private lateinit var emojiView: TextView
    private lateinit var nameView: TextView
    private lateinit var actionView: TextView
    private lateinit var descView: TextView
    private lateinit var pageIndicator: LinearLayout
    private lateinit var nextBtn: TextView
    private lateinit var counterView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0A1628"))
            setPadding(32, 56, 32, 40)
        }

        // Header
        root.addView(TextView(this).apply {
            text = "● GESTURE GUIDE"
            textSize = 10f
            setTextColor(Color.parseColor("#7AB8FF"))
            typeface = Typeface.MONOSPACE
            letterSpacing = 0.16f
            gravity = Gravity.CENTER
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(Color.parseColor("#1A1E3F80"))
                setStroke(1, Color.parseColor("#334DA3FF"))
                cornerRadius = 100f
            }
            setPadding(24, 8, 24, 8)
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.gravity = Gravity.CENTER_HORIZONTAL
            lp.setMargins(0, 0, 0, 24)
            layoutParams = lp
        })

        root.addView(TextView(this).apply {
            text = "Learn the gestures"
            textSize = 26f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0, 0, 0, 6)
            layoutParams = lp
        })

        root.addView(TextView(this).apply {
            text = "Swipe through each gesture before you start"
            textSize = 13f
            setTextColor(Color.parseColor("#607090"))
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0, 0, 0, 40)
            layoutParams = lp
        })

        // Big gesture card
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(Color.parseColor("#CC102440"))
                setStroke(1, Color.parseColor("#334DA3FF"))
                cornerRadius = 24f
            }
            setPadding(32, 48, 32, 48)
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0, 0, 0, 32)
            layoutParams = lp
        }

        emojiView = TextView(this).apply {
            textSize = 72f
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0, 0, 0, 20)
            layoutParams = lp
        }

        nameView = TextView(this).apply {
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0, 0, 0, 8)
            layoutParams = lp
        }

        actionView = TextView(this).apply {
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#4DA3FF"))
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0, 0, 0, 8)
            layoutParams = lp
        }

        descView = TextView(this).apply {
            textSize = 13f
            setTextColor(Color.parseColor("#607090"))
            gravity = Gravity.CENTER
        }

        card.addView(emojiView)
        card.addView(nameView)
        card.addView(actionView)
        card.addView(descView)
        root.addView(card)

        // Page dots
        pageIndicator = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0, 0, 0, 32)
            layoutParams = lp
        }
        gestures.forEach { _ ->
            val dot = android.view.View(this).apply {
                background = android.graphics.drawable.GradientDrawable().apply {
                    shape = android.graphics.drawable.GradientDrawable.OVAL
                    setColor(Color.parseColor("#2A3A50"))
                }
                val lp = LinearLayout.LayoutParams(8, 8)
                lp.setMargins(5, 0, 5, 0)
                layoutParams = lp
            }
            pageIndicator.addView(dot)
        }
        root.addView(pageIndicator)

        // Counter + Next row
        val bottomRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        counterView = TextView(this).apply {
            textSize = 12f
            setTextColor(Color.parseColor("#2A3A50"))
            typeface = Typeface.MONOSPACE
            val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            layoutParams = lp
        }

        nextBtn = TextView(this).apply {
            textSize = 15f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(Color.parseColor("#1E5BBF"))
                setStroke(1, Color.parseColor("#4DA3FF"))
                cornerRadius = 100f
            }
            setPadding(40, 16, 40, 16)
            setOnClickListener { advancePage() }
        }

        bottomRow.addView(counterView)
        bottomRow.addView(nextBtn)
        root.addView(bottomRow)

        updatePage()
        setContentView(root)
    }

    private fun updatePage() {
        val g = gestures[currentPage]
        emojiView.text = g[0]
        nameView.text = g[1]
        actionView.text = g[2]
        descView.text = g[3]
        counterView.text = "${currentPage + 1} / ${gestures.size}"
        nextBtn.text = if (currentPage == gestures.size - 1) "Continue  →" else "Next  →"

        // Update dots
        for (i in 0 until pageIndicator.childCount) {
            val dot = pageIndicator.getChildAt(i) as android.view.View
            (dot.background as android.graphics.drawable.GradientDrawable).setColor(
                if (i == currentPage) Color.parseColor("#4DA3FF") else Color.parseColor("#2A3A50")
            )
        }
    }

    private fun advancePage() {
        if (currentPage < gestures.size - 1) {
            currentPage++
            updatePage()
        } else {
            startActivity(Intent(this, OnboardingContactsActivity::class.java))
        }
    }
}