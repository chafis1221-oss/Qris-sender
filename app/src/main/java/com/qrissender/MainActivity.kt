package com.qrissender

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.Button
import android.widget.ImageView
import android.graphics.drawable.GradientDrawable
import android.graphics.Color
import android.view.View

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var toggleButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createLayout())
        updateStatus()
    }

    private fun createLayout(): LinearLayout {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            setBackgroundColor(Color.parseColor("#F8F9FA"))
        }

        // Title
        val titleCard = CardView(this).apply {
            radius = 24f
            cardElevation = 4f
            setCardBackgroundColor(Color.WHITE)
            setContentPadding(40, 40, 40, 40)
        }
        val titleLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }
        val icon = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_dialog_email)
            setColorFilter(Color.parseColor("#1A1A2E"))
        }
        val titleText = TextView(this).apply {
            text = "QRIS Sender"
            textSize = 22f
            setTextColor(Color.parseColor("#1A1A2E"))
            gravity = Gravity.CENTER
        }
        val subtitleText = TextView(this).apply {
            text = "Forward notifikasi DANA ke server"
            textSize = 13f
            setTextColor(Color.parseColor("#9E9E9E"))
            gravity = Gravity.CENTER
        }
        titleLayout.addView(icon)
        titleLayout.addView(titleText)
        titleLayout.addView(subtitleText)
        titleCard.addView(titleLayout)
        root.addView(titleCard)

        // Status Card
        val statusCard = CardView(this).apply {
            radius = 20f
            cardElevation = 4f
            setCardBackgroundColor(Color.WHITE)
            setContentPadding(32, 32, 32, 32)
        }
        val statusLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val statusDot = View(this).apply {
            setBackgroundResource(android.R.drawable.presence_offline)
            layoutParams = LinearLayout.LayoutParams(24, 24)
        }
        statusText = TextView(this).apply {
            textSize = 16f
            setTextColor(Color.parseColor("#1A1A2E"))
        }
        statusLayout.addView(statusDot)
        statusLayout.addView(statusText, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(16, 0, 0, 0) })
        statusCard.addView(statusLayout)
        root.addView(statusCard, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, 24, 0, 0)
        })

        // Toggle Button
        toggleButton = Button(this).apply {
            text = "Start Listener"
            textSize = 16f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#1A1A2E"))
            setOnClickListener { toggleService() }
        }
        root.addView(toggleButton, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, 32, 0, 0)
        })

        // Menu Buttons
        val menuLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        val devButton = createMenuButton("Dev Mode") { startActivity(Intent(this@MainActivity, DevModeActivity::class.java)) }
        val settingsButton = createMenuButton("Settings") { startActivity(Intent(this@MainActivity, SettingsActivity::class.java)) }
        val historyButton = createMenuButton("History") { startActivity(Intent(this@MainActivity, HistoryActivity::class.java)) }
        menuLayout.addView(devButton)
        menuLayout.addView(settingsButton)
        menuLayout.addView(historyButton)
        root.addView(menuLayout, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, 32, 0, 0)
        })

        return root
    }

    private fun createMenuButton(text: String, onClick: () -> Unit): Button {
        return Button(this).apply {
            this.text = text
            textSize = 13f
            setTextColor(Color.parseColor("#1A1A2E"))
            setBackgroundColor(Color.parseColor("#F0F0F0"))
            setOnClickListener { onClick() }
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(8, 0, 8, 0)
            }
        }
    }

    private fun toggleService() {
        if (isNotificationListenerEnabled()) {
            // Disable (buka settings untuk disable)
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        } else {
            // Enable
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            Toast.makeText(this, "Izinkan akses notifikasi", Toast.LENGTH_LONG).show()
        }
    }

    private fun isNotificationListenerEnabled(): Boolean {
        val listeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return listeners?.contains(packageName) == true
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun updateStatus() {
        val enabled = isNotificationListenerEnabled()
        statusText.text = if (enabled) "Listener Aktif" else "Listener Tidak Aktif"
        toggleButton.text = if (enabled) "Stop Listener" else "Start Listener"
        toggleButton.setBackgroundColor(if (enabled) Color.parseColor("#E74C3C") else Color.parseColor("#1A1A2E"))
    }
}
