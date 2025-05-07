package com.example.indoorwifipositioning

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FirstActivity : AppCompatActivity() {

    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var testingButton: Button
    private lateinit var realTimeButton: Button
    private var testingBool: Boolean = false
    private lateinit var textView1: TextView
    private lateinit var textView2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        button1 = findViewById(R.id.HL_ROY_Building_Data)
        button2 = findViewById(R.id.JU_IndoorLoc_data)
        button3 = findViewById(R.id.Alpha_Lab_Data)
        testingButton = findViewById(R.id.testingButton)
        realTimeButton = findViewById(R.id.realTimeButton)
        textView1 = findViewById(R.id.txt1)
        textView2 = findViewById(R.id.txt2)

        testingButton.setBackgroundColor(Color.GRAY)
        realTimeButton.setBackgroundColor(Color.WHITE)
        button1.visibility = View.VISIBLE
        button2.visibility = View.VISIBLE
        button3.visibility = View.VISIBLE
        textView1.visibility = View.GONE
        textView2.visibility = View.GONE


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
           if(testingBool) {
                Intent(this, MainActivity::class.java).also {
                    it.putExtra("filename", "Complete_AlphaLab_Data.csv")
                    startActivity(it)
                    finish()
                }
            }
            else{

               Intent(this, RealTimeActivity::class.java).also {
                   startActivity(it)
                   finish()
               }

           }
        }

        testingButton.setOnClickListener {
            testingBool = true
            testingButton.setBackgroundColor(Color.GRAY)
            realTimeButton.setBackgroundColor(Color.WHITE)
            button1.visibility = View.VISIBLE
            button2.visibility = View.VISIBLE
            button3.visibility = View.VISIBLE
            textView1.visibility = View.GONE
            textView2.visibility = View.GONE
        }
        realTimeButton.setOnClickListener {
            testingBool = false
            testingButton.setBackgroundColor(Color.WHITE)
            realTimeButton.setBackgroundColor(Color.GRAY)
            button1.visibility = View.GONE
            button2.visibility = View.GONE
            button3.visibility = View.VISIBLE
            textView1.visibility = View.VISIBLE
            textView2.visibility = View.VISIBLE
        }
    }
}