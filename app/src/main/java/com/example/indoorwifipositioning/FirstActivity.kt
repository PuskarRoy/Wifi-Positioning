package com.example.indoorwifipositioning

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class FirstActivity : AppCompatActivity() {

    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        button1 = findViewById(R.id.HL_ROY_Building_Data)
        button2 = findViewById(R.id.JU_IndoorLoc_data)
        button3 = findViewById(R.id.Alpha_Lab_Data)

        button1.setOnClickListener {
            Intent(this, MainActivity::class.java).also {

                it.putExtra("filename", "Complete_HL_ROY_Building_Data.csv")
                startActivity(it)
                finish()
            }
        }
        button2.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                it.putExtra("filename", "JUIndoorLoc-Training-data.csv")
                startActivity(it)
                finish()
            }
        }

        button3.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                it.putExtra("filename", "Complete_AlphaLab_Data.csv")
                startActivity(it)
                finish()
            }
        }
    }
}