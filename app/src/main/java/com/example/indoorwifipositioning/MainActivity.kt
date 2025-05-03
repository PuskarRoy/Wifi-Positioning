package com.example.indoorwifipositioning

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.indoorwifipositioning.data.FourthFloorDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {


    private lateinit var predicted_textView: TextView
    private lateinit var actual_textView: TextView
    private lateinit var nextButton: Button
    private lateinit var previousButton: Button
    private lateinit var getRoomButton: Button
    private lateinit var showInMapButton: Button
    private lateinit var csvData: List<List<String>>
    private lateinit var csvTestData: List<List<String>>
    private lateinit var progressDialog: ProgressDialog
    private lateinit var toolbar: Toolbar
    private lateinit var filename: String
    private var row_count = 0
    private var predictedCellId = ""
    private var cellId = ""
    private var check = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.tooblar)
        setSupportActionBar(toolbar)
        predicted_textView = findViewById(R.id.predictedTextView)
        actual_textView = findViewById(R.id.actualTextView)
        nextButton = findViewById(R.id.nextButton)
        previousButton = findViewById(R.id.previousButton)
        getRoomButton = findViewById(R.id.getRoomButton)
        showInMapButton = findViewById(R.id.showInMapButton)
        getRoomButton.text = "Room Name"
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading...")
        filename = intent.getStringExtra("filename")!!
        getRoomButton.visibility =
            if (filename.equals("JUIndoorLoc-Training-data.csv")) View.VISIBLE else View.GONE
        showInMapButton.visibility =
            if (filename.equals("Complete_HL_ROY_Building_Data.csv")) View.INVISIBLE else View.VISIBLE
        readCSV(filename)
        nextButton.setOnClickListener {

            if (row_count < csvTestData.size) {
                check = false
                getRoomButton.text = "Room Name"
                predicted_textView.text = "Loading..."
                actual_textView.text = "Loading..."
                row_count++
                progressDialog.show()
                lifecycleScope.launch { testData() }
            } else {

                Toast.makeText(this, "End of Data", Toast.LENGTH_SHORT).show()
            }
        }

        previousButton.setOnClickListener {

            if (row_count > 0) {
                check = false
                getRoomButton.text = "Room Name"
                predicted_textView.text = "Loading..."
                actual_textView.text = "Loading..."
                row_count--
                progressDialog.show()
                lifecycleScope.launch { testData() }
            } else {
                Toast.makeText(this, "Start of Data", Toast.LENGTH_SHORT).show()
            }

        }

        getRoomButton.setOnClickListener {
            if (!check) {
                predicted_textView.text = getRoom(predictedCellId)
                actual_textView.text = getRoom(cellId)
                getRoomButton.text = "Cell ID"
                check = true
            } else {
                predicted_textView.text = predictedCellId
                actual_textView.text = cellId
                getRoomButton.text = "Room Name"
                check = false
            }
        }
        showInMapButton.setOnClickListener {

            Intent(this, MapActivity::class.java).also {
                it.putExtra("filename", filename)
                it.putExtra("cellId", cellId)
                startActivity(it)
            }
        }


    }

    fun testData() {

        lifecycleScope.launch(Dispatchers.Default) {
            cellId = csvTestData[row_count][0]
            val features = mutableListOf<Double>()
            for (i in 1 until csvTestData[row_count].size) {
                features.add(csvTestData[row_count][i].toDoubleOrNull() ?: -110.0)
            }
            predictedCellId = knnPredict(features)

            lifecycleScope.launch(Dispatchers.Main) {
                predicted_textView.text = predictedCellId
                actual_textView.text = cellId
                progressDialog.dismiss()

            }


        }


    }


    fun readCSV(fileName: String) {
        progressDialog.show()
        lifecycleScope.launch(Dispatchers.IO) {
            val data = mutableListOf<List<String>>()
            val inputStream = this@MainActivity.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.readLine()
            reader.forEachLine {
                data.add(it.split(","))
            }
            reader.close()
            csvData = data
            lifecycleScope.launch { readTestCSV("Training_$filename") }


        }


    }

    fun readTestCSV(fileName: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val data = mutableListOf<List<String>>()
            val inputStream = this@MainActivity.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.readLine()
            reader.forEachLine {
                data.add(it.split(","))
            }
            reader.close()
            csvTestData = data
            delay(2000)
            lifecycleScope.launch(Dispatchers.Main) {
                progressDialog.setMessage("Loading Prediction...")
                testData()
            }


        }

    }


    fun euclideanDistance(a: List<Double>, b: List<Double>): Double {
        var sum = 0.0
        for (i in a.indices) {
            sum += (a[i] - b[i]) * (a[i] - b[i])
        }
        return sqrt(sum)
    }

    fun knnPredict(input: List<Double>): String {
        val data = csvData
        val k = 3
        val distances = mutableListOf<Pair<String, Double>>()

        for (row in data) {
            val cellId = row[0]
            val features = mutableListOf<Double>()
            for (i in 1 until row.size) {
                features.add(row[i].toDoubleOrNull() ?: -110.0)
            }
            val distance = euclideanDistance(features, input)
            distances.add(Pair(cellId, distance))
        }

        distances.sortBy { it.second }
        val nearestNeighbors = distances.subList(0, k)

        val frequencyMap = mutableMapOf<String, Int>()
        for (neighbor in nearestNeighbors) {
            val cellId = neighbor.first
            frequencyMap[cellId] = frequencyMap.getOrDefault(cellId, 0) + 1
        }

        var maxCount = 0
        var predictedCellId = "Prediction unavailable"
        for ((cellId, count) in frequencyMap) {
            if (count > maxCount) {
                maxCount = count
                predictedCellId = cellId
            }
        }

        return predictedCellId
    }

    fun getRoom(cellId: String): String {
        val arr = cellId.split("-")
        val pair = Pair(arr[1].toInt(), arr[2].toInt())
        for (e in FourthFloorDetails.room.keys) {
            if (pair in e) {
                return FourthFloorDetails.room[e].toString()
            }
        }
        return cellId

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toobal_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.chooseDatasetMenu -> {
                Intent(this, FirstActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                    finish()
                }
                return true
            }

            else -> {
                return super.onOptionsItemSelected(item)
            }

        }
    }


}