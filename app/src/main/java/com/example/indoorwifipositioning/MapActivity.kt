package com.example.indoorwifipositioning

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MapActivity : AppCompatActivity() {

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
    private lateinit var markerView: ImageView
    private lateinit var floorplanImage: ImageView

    private val GRID_COLUMNS = 42
    private val GRID_ROWS = 19

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
        markerView = findViewById(R.id.markerView)
        floorplanImage = findViewById(R.id.floorplanImage)
        val filename = intent.getStringExtra("filename")
        floorplanImage.setBackgroundResource(if (filename.equals("JUIndoorLoc-Training-data.csv")) R.drawable.ju_indoor_floorplan else R.drawable.alpha_lab)
        val cellId = intent.getStringExtra("cellId")
        if (filename.equals("JUIndoorLoc-Training-data.csv")) {
            showPointerJU(cellId!!)
        }
        else{
            showPointerAlpha(cellId!!)
        }


    }

    private fun showPointerJU(cellId: String) {
        val parts = cellId.split("-")
        if (parts.size < 3) return
        val col = parts[1].toIntOrNull() ?: return
        val row = parts[2].toIntOrNull() ?: return

        floorplanImage.post {
            val imgW = floorplanImage.width.toFloat()
            val imgH = floorplanImage.height.toFloat()

            val cellW = imgW / GRID_COLUMNS
            val cellH = imgH / GRID_ROWS

            val xPx = (col - 0.5f) * cellW
            val yPx = (row - 0.5f) * cellH

            val lp = markerView.layoutParams as FrameLayout.LayoutParams
            lp.leftMargin = xPx.toInt() - markerView.width / 2
            lp.topMargin = yPx.toInt() - markerView.height / 2
            markerView.layoutParams = lp

            markerView.visibility = View.VISIBLE
        }
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
}