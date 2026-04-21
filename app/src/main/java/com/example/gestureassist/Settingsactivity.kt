package com.example.gestureassist

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private val fields = mutableListOf<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scroll = ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0A1628"))
            setPadding(28, 52, 28, 52)
        }

        // ── Pill ─────────────────────────────────────────────────────────────
        val pill = TextView(this).apply {
            text = "● SETTINGS"
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
            text = "App Settings"
            textSize = 26f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0, 0, 0, 6)
            layoutParams = lp
        })

        root.addView(TextView(this).apply {
            text = "Configure your quick-dial contacts"
            textSize = 13f
            setTextColor(Color.parseColor("#607090"))
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0, 0, 0, 36)
            layoutParams = lp
        })

        // ── Contacts section label ────────────────────────────────────────────
        root.addView(TextView(this).apply {
            text = "QUICK-DIAL CONTACTS"
            textSize = 10f
            setTextColor(Color.parseColor("#4DA3FF"))
            typeface = Typeface.MONOSPACE
            letterSpacing = 0.16f
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0, 0, 0, 6)
            layoutParams = lp
        })

        root.addView(TextView(this).apply {
            text = "Fist gesture cycles through these. All fields optional (0–5)."
            textSize = 12f
            setTextColor(Color.parseColor("#506070"))
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0, 0, 0, 20)
            layoutParams = lp
        })

        val prefs = getSharedPreferences("contacts", Context.MODE_PRIVATE)

        // ── Contact fields ────────────────────────────────────────────────────
        for (i in 1..5) {
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                background = android.graphics.drawable.GradientDrawable().apply {
                    setColor(Color.parseColor("#CC102440"))
                    setStroke(1, Color.parseColor("#1A4DA3FF"))
                    cornerRadius = 14f
                }
                setPadding(16, 14, 16, 14)
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
                hint = "Optional — e.g. +91XXXXXXXXXX"
                setHintTextColor(Color.parseColor("#2A3A50"))
                setTextColor(Color.parseColor("#C8DCF8"))
                textSize = 14f
                inputType = android.text.InputType.TYPE_CLASS_PHONE
                imeOptions = EditorInfo.IME_ACTION_NEXT
                background = null
                setPadding(12, 0, 0, 0)
                setText(prefs.getString("contact_$i", ""))
                val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                layoutParams = lp
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

        // ── Save button ───────────────────────────────────────────────────────
        val saveBtn = TextView(this).apply {
            text = "💾  Save Contacts"
            textSize = 15f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(Color.parseColor("#1E5BBF"))
                setStroke(1, Color.parseColor("#4DA3FF"))
                cornerRadius = 100f
            }
            setPadding(0, 18, 0, 18)
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0, 16, 0, 40)
            layoutParams = lp
            setOnClickListener { saveContacts() }
        }
        root.addView(saveBtn)

        // ── Gesture reference ─────────────────────────────────────────────────
        root.addView(TextView(this).apply {
            text = "GESTURE MAP"
            textSize = 10f
            setTextColor(Color.parseColor("#4DA3FF"))
            typeface = Typeface.MONOSPACE
            letterSpacing = 0.16f
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0, 0, 0, 14)
            layoutParams = lp
        })

        val gestureMap = listOf(
            "☝️  1 Finger"     to "Volume UP (hold to repeat)",
            "✌️  2 Fingers"    to "Volume DOWN (hold to repeat)",
            "🤟  3 Fingers"    to "Brightness UP",
            "🖐️  4 Fingers"    to "Brightness DOWN",
            "✋  Open Hand"    to "Toggle Flashlight",
            "✊  Fist"         to "Call next contact",
            "👍  Thumbs Up"    to "Next Music Track",
            "🤙  Pinky+Thumb"  to "SOS — calls 112"
        )

        gestureMap.forEach { (gesture, action) ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                background = android.graphics.drawable.GradientDrawable().apply {
                    setColor(Color.parseColor("#0D102440"))
                    setStroke(1, Color.parseColor("#0F4DA3FF"))
                    cornerRadius = 10f
                }
                setPadding(16, 12, 16, 12)
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                lp.setMargins(0, 0, 0, 6)
                layoutParams = lp
            }
            row.addView(TextView(this).apply {
                text = gesture
                textSize = 13f
                setTextColor(Color.parseColor("#7AB8FF"))
                val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                layoutParams = lp
            })
            row.addView(TextView(this).apply {
                text = action
                textSize = 12f
                setTextColor(Color.parseColor("#506070"))
                val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.2f)
                layoutParams = lp
            })
            root.addView(row)
        }

        scroll.addView(root)
        setContentView(scroll)
    }

    private fun saveContacts() {
        val prefs = getSharedPreferences("contacts", Context.MODE_PRIVATE).edit()
        var saved = 0
        fields.forEachIndexed { i, et ->
            val value = et.text.toString().trim()
            prefs.putString("contact_${i + 1}", value)
            if (value.isNotBlank()) saved++
        }
        prefs.apply()
        val msg = if (saved == 0) "No contacts saved"
        else "✓ $saved contact${if (saved > 1) "s" else ""} saved"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}