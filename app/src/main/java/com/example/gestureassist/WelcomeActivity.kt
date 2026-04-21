package com.example.gestureassist

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Skip onboarding if already completed
        val prefs = getSharedPreferences("onboarding", MODE_PRIVATE)
        if (prefs.getBoolean("completed", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#0A1628"))
        }

        val center = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(40, 0, 40, 0)
        }

        // Animated logo area
        val logoWrap = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            val lp = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            layoutParams = lp
        }

        // App icon circle
        val iconCircle = FrameLayout(this).apply {
            background = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setColor(Color.parseColor("#1E3F80"))
                setStroke(2, Color.parseColor("#4DA3FF"))
            }
            val lp = LinearLayout.LayoutParams(100, 100)
            lp.gravity = Gravity.CENTER_HORIZONTAL
            lp.setMargins(0, 0, 0, 28)
            layoutParams = lp
        }
        val iconText = TextView(this).apply {
            text = "✋"
            textSize = 36f
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        iconCircle.addView(iconText)

        // App name
        val appName = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(0, 0, 0, 12)
            layoutParams = lp
        }
        appName.addView(TextView(this).apply {
            text = "Gesture"
            textSize = 32f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        })
        appName.addView(TextView(this).apply {
            text = "Assist"
            textSize = 32f
            setTextColor(Color.parseColor("#4DA3FF"))
            typeface = Typeface.DEFAULT_BOLD
        })

        // Subtitle
        val subtitle = TextView(this).apply {
            text = "Gesture-powered control center"
            textSize = 14f
            setTextColor(Color.parseColor("#607090"))
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(0, 0, 0, 56)
            layoutParams = lp
        }

        // Feature pills row
        val pillRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(0, 0, 0, 60)
            layoutParams = lp
        }

        listOf("📱 Hands-free", "🔊 Voice feedback", "🚨 SOS ready").forEach { label ->
            val pill = TextView(this).apply {
                text = label
                textSize = 11f
                setTextColor(Color.parseColor("#7AB8FF"))
                background = android.graphics.drawable.GradientDrawable().apply {
                    setColor(Color.parseColor("#1A1E3F80"))
                    setStroke(1, Color.parseColor("#334DA3FF"))
                    cornerRadius = 100f
                }
                setPadding(18, 8, 18, 8)
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(6, 0, 6, 0)
                layoutParams = lp
            }
            pillRow.addView(pill)
        }

        // Get Started button
        val getStarted = TextView(this).apply {
            text = "Get Started  →"
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(Color.parseColor("#1E5BBF"))
                setStroke(1, Color.parseColor("#4DA3FF"))
                cornerRadius = 100f
            }
            setPadding(0, 20, 0, 20)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(0, 0, 0, 16)
            layoutParams = lp
            setOnClickListener {
                startActivity(Intent(this@WelcomeActivity, OnboardingGestureActivity::class.java))
            }
        }

        // Version tag
        val version = TextView(this).apply {
            text = "GESTUREASSIST · v2.0"
            textSize = 9f
            setTextColor(Color.parseColor("#2A3A50"))
            typeface = Typeface.MONOSPACE
            gravity = Gravity.CENTER
            letterSpacing = 0.14f
        }

        logoWrap.addView(center)
        center.addView(iconCircle)
        center.addView(appName)
        center.addView(subtitle)
        center.addView(pillRow)
        center.addView(getStarted)
        center.addView(version)
        root.addView(logoWrap)
        setContentView(root)

        // Fade-in animation
        val fadeIn = AlphaAnimation(0f, 1f).apply { duration = 900 }
        val slideUp = TranslateAnimation(0f, 0f, 60f, 0f).apply { duration = 900 }
        val anim = AnimationSet(true).apply {
            addAnimation(fadeIn); addAnimation(slideUp)
        }
        center.startAnimation(anim)
    }
}