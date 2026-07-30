package com.qrissender

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color

class SettingsActivity : AppCompatActivity() {

    private lateinit var urlInput: EditText
    private lateinit var apiKeyInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createLayout())
        loadSettings()
    }

    private fun createLayout(): LinearLayout {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            setBackgroundColor(Color.parseColor("#F8F9FA"))
        }

        // Title
        val title = TextView(this).apply {
            text = "Settings"
            textSize = 20f
            setTextColor(Color.parseColor("#1A1A2E"))
            gravity = Gravity.CENTER
        }
        root.addView(title)

        // URL Input
        urlInput = EditText(this).apply {
            hint = "Server URL (http://192.168.1.17:8080)"
            setTextColor(Color.parseColor("#1A1A2E"))
            setBackgroundColor(Color.WHITE)
        }
        root.addView(urlInput, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, 32, 0, 0)
        })

        // API Key Input
        apiKeyInput = EditText(this).apply {
            hint = "API Key"
            setTextColor(Color.parseColor("#1A1A2E"))
            setBackgroundColor(Color.WHITE)
        }
        root.addView(apiKeyInput, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, 16, 0, 0)
        })

        // Save Button
        val saveButton = Button(this).apply {
            text = "Simpan"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#1A1A2E"))
            setOnClickListener { saveSettings() }
        }
        root.addView(saveButton, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, 32, 0, 0)
        })

        return root
    }

    private fun loadSettings() {
        val prefs = getSharedPreferences("qris_sender_settings", Context.MODE_PRIVATE)
        urlInput.setText(prefs.getString("server_url", ""))
        apiKeyInput.setText(prefs.getString("api_key", ""))
    }

    private fun saveSettings() {
        val prefs = getSharedPreferences("qris_sender_settings", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("server_url", urlInput.text.toString().trim())
            putString("api_key", apiKeyInput.text.toString().trim())
            apply()
        }
        Toast.makeText(this, "Pengaturan disimpan", Toast.LENGTH_SHORT).show()
        finish()
    }
}
