package com.example.hw2

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val edtNumber = findViewById<EditText>(R.id.text)
        val btnCreate = findViewById<Button>(R.id.btnCreate)
        val listContainer = findViewById<LinearLayout>(R.id.listContainer)
        val tvError = findViewById<TextView>(R.id.tvError)

        btnCreate.setOnClickListener {
            val input = edtNumber.text.toString()
            listContainer.removeAllViews()

            try {
                val number = input.toInt()
                tvError.visibility = TextView.GONE

                for (i in 1..number) {
                    val tv = TextView(this)
                    tv.text = i.toString()
                    tv.textSize = 18f
                    tv.setTextColor(resources.getColor(android.R.color.white))
                    tv.setBackgroundColor(resources.getColor(android.R.color.holo_red_dark))
                    tv.gravity = Gravity.CENTER
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(0, 10, 0, 0)
                    tv.layoutParams = params
                    tv.setPadding(0, 20, 0, 20)
                    listContainer.addView(tv)
                }
            } catch (e: Exception) {
                tvError.visibility = TextView.VISIBLE
                e.printStackTrace()
            }
        }
    }
}
