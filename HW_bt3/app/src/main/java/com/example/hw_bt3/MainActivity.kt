package com.example.hw_bt3

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
        val btnCheck = findViewById<Button>(R.id.btnCheck)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        btnCheck.setOnClickListener {
            val name = edtName.text.toString().trim()
            val ageText = edtAge.text.toString().trim()

            if (name.isEmpty() || ageText.isEmpty()) {
                tvResult.text = "Vui lòng nhập đầy đủ họ tên và tuổi!"
                tvResult.setTextColor(getColor(android.R.color.holo_red_dark))
                return@setOnClickListener
            }

            val age = ageText.toIntOrNull()
            if (age == null || age < 0) {
                tvResult.text = "Tuổi không hợp lệ!"
                tvResult.setTextColor(getColor(android.R.color.holo_red_dark))
                return@setOnClickListener
            }

            val category = when {
                age < 2 -> "Em bé"
                age in 2..6 -> "Trẻ em"
                age in 7..65 -> "Người lớn"
                else -> "Người già"
            }

            tvResult.text = "$name là $category"
            tvResult.setTextColor(getColor(android.R.color.holo_green_dark))
        }
    }
}

