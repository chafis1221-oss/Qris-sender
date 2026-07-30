package com.qrissender

import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.graphics.Color
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            setBackgroundColor(Color.parseColor("#F8F9FA"))
        }

        val title = TextView(this).apply {
            text = "Riwayat Notifikasi"
            textSize = 20f
            setTextColor(Color.parseColor("#1A1A2E"))
            gravity = Gravity.CENTER
        }
        root.addView(title)

        val listView = ListView(this).apply {
            divider = null
            dividerHeight = 0
        }
        root.addView(listView, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT).apply {
            setMargins(0, 24, 0, 0)
        })

        val history = HistoryManager.getHistory(this)
        val adapter = object : ArrayAdapter<HistoryItem>(this, android.R.layout.simple_list_item_1, history) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val card = CardView(context).apply {
                    radius = 16f
                    cardElevation = 4f
                    setCardBackgroundColor(Color.WHITE)
                    setContentPadding(24, 16, 24, 16)
                }
                val layout = LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                }
                val item = getItem(position)!!
                val textView = TextView(context).apply {
                    text = item.text
                    textSize = 14f
                    setTextColor(Color.parseColor("#1A1A2E"))
                }
                val timeView = TextView(context).apply {
                    text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(item.time))
                    textSize = 12f
                    setTextColor(Color.parseColor("#9E9E9E"))
                }
                val statusView = TextView(context).apply {
                    text = if (item.success) "✅ Terkirim" else "❌ Gagal"
                    textSize = 12f
                    setTextColor(if (item.success) Color.parseColor("#4CAF50") else Color.parseColor("#E74C3C"))
                }
                layout.addView(textView)
                layout.addView(timeView)
                layout.addView(statusView)
                card.addView(layout)
                return card
            }
        }
        listView.adapter = adapter

        setContentView(root)
    }
}
