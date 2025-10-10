package com.example.hw2_bt2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val edtName = findViewById<EditText>(R.id.edtName)
        val edtAge = findViewById<EditText>(R.id.edtAge)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val btnCheck = findViewById<Button>(R.id.btnCheck)
        val tvMessage = findViewById<TextView>(R.id.tvMessage)

        btnCheck.setOnClickListener {
            val name = edtName.text.toString().trim()
            val age = edtAge.text.toString().trim()
            val email = edtEmail.text.toString().trim()

            when {
                email.isEmpty() -> {
                    tvMessage.text = "Email không hợp lệ"
                    tvMessage.setTextColor(getColor(android.R.color.holo_red_dark))
                    tvMessage.visibility = TextView.VISIBLE
                }

                !email.contains("@") -> {
                    tvMessage.text = "Email không đúng định dạng"
                    tvMessage.setTextColor(getColor(android.R.color.holo_red_dark))
                    tvMessage.visibility = TextView.VISIBLE
                }

                else -> {
                    tvMessage.text = "Bạn đã nhập email hợp lệ\nTên: $name - Tuổi: $age"
                    tvMessage.setTextColor(getColor(android.R.color.holo_green_dark))
                    tvMessage.visibility = TextView.VISIBLE
                }
            }
        }
    }
}
