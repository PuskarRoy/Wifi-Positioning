package com.example.indoorwifipositioning

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.sqrt

class RealTimeActivity : AppCompatActivity() {

    private lateinit var r1_marker: ImageView
    private lateinit var r2_marker: ImageView
    private lateinit var r3_marker: ImageView
    private lateinit var r4_marker: ImageView
    private lateinit var r5_marker: ImageView
    private lateinit var r6_marker: ImageView
    private lateinit var r7_marker: ImageView
    private lateinit var r8_marker: ImageView
    private lateinit var r9_marker: ImageView
    private lateinit var r10_marker: ImageView
    private lateinit var floorplanImage: ImageView
    private lateinit var progressDialog: ProgressDialog
    private lateinit var csvData: List<List<String>>
    private lateinit var floatingActionButton: FloatingActionButton

    private lateinit var wifiManager: WifiManager
    private lateinit var wifiReceiver: BroadcastReceiver
    private val LOCATION_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time)
        r1_marker = findViewById(R.id.r1_marker)
        r2_marker = findViewById(R.id.r2_markar)
        r3_marker = findViewById(R.id.r3_markar)
        r4_marker = findViewById(R.id.r4_markar)
        r5_marker = findViewById(R.id.r5_markar)
        r6_marker = findViewById(R.id.r6_markar)
        r7_marker = findViewById(R.id.r7_markar)
        r8_marker = findViewById(R.id.r8_markar)
        r9_marker = findViewById(R.id.r9_markar)
        r10_marker = findViewById(R.id.r10_markar)
        floorplanImage = findViewById(R.id.floorplanImage)
        floatingActionButton = findViewById(R.id.floatingActionButton)
        floatingActionButton.visibility = View.VISIBLE
        floorplanImage.setBackgroundResource(R.drawable.alpha_lab)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading...")
        requestLocationPermission()
        readCSV("Training_Complete_AlphaLab_Data.csv")
        wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager

        floatingActionButton.setOnClickListener {
            val success = wifiManager.startScan()
            if (!success) {
                Toast.makeText(this@RealTimeActivity, "Scan failed", Toast.LENGTH_SHORT).show()
            } else {
                progressDialog.setMessage("Scanning...")
                progressDialog.show()
            }
        }

    }

    fun readCSV(fileName: String) {
        progressDialog.show()
        lifecycleScope.launch(Dispatchers.IO) {
            val data = mutableListOf<List<String>>()
            val inputStream = this@RealTimeActivity.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.readLine()
            reader.forEachLine {
                data.add(it.split(","))
            }
            reader.close()
            csvData = data
            lifecycleScope.launch(Dispatchers.Main) {
                progressDialog.setMessage("Scanning...")
                setupWifiScan()
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
    private fun showPointerAlpha(cellId: String) {
        if (cellId.equals("R1")) {
            r1_marker.visibility = View.VISIBLE
        } else if (cellId.equals("R2")) {
            r2_marker.visibility = View.VISIBLE
        } else if (cellId.equals("R3")) {
            r3_marker.visibility = View.VISIBLE
        } else if (cellId.equals("R4")) {
            r4_marker.visibility = View.VISIBLE
        } else if (cellId.equals("R5")) {
            r5_marker.visibility = View.VISIBLE
        } else if (cellId.equals("R6")) {
            r6_marker.visibility = View.VISIBLE
        } else if (cellId.equals("R7")) {
            r7_marker.visibility = View.VISIBLE
        } else if (cellId.equals("R8")) {
            r8_marker.visibility = View.VISIBLE
        } else if (cellId.equals("R9")) {
            r9_marker.visibility = View.VISIBLE
        } else if (cellId.equals("R10")) {
            r10_marker.visibility = View.VISIBLE
        }
    }
    private fun hideAllMarkers() {
        r1_marker.visibility = View.GONE
        r2_marker.visibility = View.GONE
        r3_marker.visibility = View.GONE
        r4_marker.visibility = View.GONE
        r5_marker.visibility = View.GONE
        r6_marker.visibility = View.GONE
        r7_marker.visibility = View.GONE
        r8_marker.visibility = View.GONE
        r9_marker.visibility = View.GONE
        r10_marker.visibility = View.GONE
    }
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), LOCATION_PERMISSION_CODE
            )
        }
    }


    private fun extractWifiLevel(scanMap: Map<String, Double>): List<Double> {
        val featureList = mutableListOf<Double>()
        val headers = csvData.first().drop(1)

        for (bssid in headers) {
            featureList.add(scanMap[bssid] ?: -110.0)
        }

        return featureList
    }
    private fun setupWifiScan() {
        wifiReceiver = object : BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            override fun onReceive(context: Context?, intent: Intent?) {
                val results = wifiManager.scanResults
                val scanMap = results.associateBy({ it.BSSID }, { it.level.toDouble() })
                val inputFeatures = extractWifiLevel(scanMap)
                val predictedCell = knnPredict(inputFeatures)
                runOnUiThread {
                    progressDialog.dismiss()
                    hideAllMarkers()
                    showPointerAlpha(predictedCell)
                }

            }
        }

        registerReceiver(
            wifiReceiver, android.content.IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(wifiReceiver)
        } catch (e: Exception) {
            Log.e("RealTimeActivity", "Receiver already unregistered or not registered")
        }
    }


}


