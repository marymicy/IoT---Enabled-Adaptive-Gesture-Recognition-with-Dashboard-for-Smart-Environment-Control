package com.example.gestureassist

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class OnboardingContactsActivity : AppCompatActivity() {

    private val fields = mutableListOf<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scroll = ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0A1628"))
            setPadding(32, 56, 32, 40)
        }

        // Pill
        root.addView(TextView(this).apply {
            text = "● OPTIONAL STEP"
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

        // Title
        root.addView(TextView(this).apply {
            text = "Quick-dial Contacts"
            textSize = 26f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0, 0, 0, 8)
            layoutParams = lp
        })

        root.addView(TextView(this).apply {
            text = "The ✊ Fist gesture will call these numbers.\nAdd 0–5 contacts, or skip — you can add them later."
            textSize = 13f
            setTextColor(Color.parseColor("#607090"))
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0, 0, 0, 36)
            layoutParams = lp
        })

        val prefs = getSharedPreferences("contacts", Context.MODE_PRIVATE)

        // Contact fields
        for (i in 1..5) {
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                background = android.graphics.drawable.GradientDrawable().apply {
                    setColor(Color.parseColor("#CC102440"))
                    setStroke(1, Color.parseColor("#1A4DA3FF"))
                    cornerRadius = 14f
                }
                setPadding(16, 16, 16, 16)
                gravity = android.view.Gravity.CENTER_VERTICAL
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                lp.setMargins(0, 0, 0, 10)
                layoutParams = lp
            }

            card.addView(TextView(this).apply {
                text = "$i"
                textSize = 13f
                setTextColor(Color.parseColor("#4DA3FF"))
                typeface = Typeface.MONOSPACE
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(32, LinearLayout.LayoutParams.WRAP_CONTENT)
            })

            val et = EditText(this).apply {
                hint = "Optional — +91XXXXXXXXXX"
                setHintTextColor(Color.parseColor("#2A3A50"))
                setTextColor(Color.parseColor("#C8DCF8"))
                textSize = 14f
                inputType = android.text.InputType.TYPE_CLASS_PHONE
                imeOptions = EditorInfo.IME_ACTION_NEXT
                background = null
                setPadding(12, 0, 0, 0)
                setText(prefs.getString("contact_$i", ""))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val clearBtn = TextView(this).apply {
                text = "✕"
                textSize = 14f
                setTextColor(Color.parseColor("#2A3A50"))
                setPadding(12, 0, 4, 0)
                setOnClickListener { et.setText("") }
            }

            fields.add(et)
            card.addView(et)
            card.addView(clearBtn)
            root.addView(card)
        }

        // Spacer
        root.addView(android.view.View(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 32)
        })

        // Save & Continue button
        root.addView(TextView(this).apply {
            text = "Save & Continue  →"
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
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0, 0, 0, 12)
            layoutParams = lp
            setOnClickListener { saveAndContinue() }
        })

        // Skip button
        root.addView(TextView(this).apply {
            text = "Skip for now"
            textSize = 14f
            setTextColor(Color.parseColor("#2A3A50"))
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 12)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            setOnClickListener { goToMain() }
        })

        scroll.addView(root)
        setContentView(scroll)
    }

    private fun saveAndContinue() {
        val prefs = getSharedPreferences("contacts", Context.MODE_PRIVATE).edit()
        fields.forEachIndexed { i, et ->
            prefs.putString("contact_${i + 1}", et.text.toString().trim())
        }
        prefs.apply()
        goToMain()
    }

    private fun goToMain() {
        getSharedPreferences("onboarding", MODE_PRIVATE).edit()
            .putBoolean("completed", true).apply()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}