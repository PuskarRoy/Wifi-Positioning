package com.example.indoorwifipositioning

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.indoorwifipositioning.RealTimeActivity
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class showwifidata : AppCompatActivity() {

    private lateinit var wifiReceiver: BroadcastReceiver
    private lateinit var csvData: List<List<String>>
    private lateinit var wifiManager: WifiManager
    private lateinit var textView: TextView
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showwifidata)
        textView = findViewById(R.id.textView)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading...")
        readCSV("Training_Complete_AlphaLab_Data.csv")
        wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val success = wifiManager.startScan()
        if (!success) {
            Toast.makeText(this@showwifidata, "Scan failed", Toast.LENGTH_SHORT).show()
        } else {
            progressDialog.setMessage("Scanning...")
            progressDialog.show()
        }
    }

    fun readCSV(fileName: String) {
        progressDialog.show()
        lifecycleScope.launch(Dispatchers.IO) {
            val data = mutableListOf<List<String>>()
            val inputStream = this@showwifidata.assets.open(fileName)
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

    private fun setupWifiScan() {
        wifiReceiver = object : BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            override fun onReceive(context: Context?, intent: Intent?) {
                val results = wifiManager.scanResults
                val scanMap = results.associateBy({ it.BSSID }, { it.level.toDouble() })
                val inputFeatures = extractWifiLevel(scanMap)
                textView.text = inputFeatures.toString()

                runOnUiThread {
                    progressDialog.dismiss()
                }

            }
        }
        registerReceiver(
            wifiReceiver, android.content.IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        )

    }

    private fun extractWifiLevel(scanMap: Map<String, Double>): List<Double> {
        val featureList = mutableListOf<Double>()
        val headers = csvData.first().drop(1)

        for (bssid in headers) {
            featureList.add(scanMap[bssid] ?: -110.0)
        }

        return featureList
    }
}