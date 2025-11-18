package com.example.expensetracker

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetracker.databinding.ActivityStaticticsBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class ChartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStaticticsBinding
    private lateinit var dbHelper: ExpenseDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaticticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = ExpenseDatabaseHelper(this)

        binding.navHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.navExpenses.setOnClickListener {
            startActivity(Intent(this, LastExpensesActivity::class.java))
        }

        setupChart()
    }

    private fun setupChart() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT PRICE, CREATED_AT FROM EXPENSES ORDER BY CREATED_AT ASC",
            null
        )

        val entries = mutableListOf<Entry>()
        var index = 0f

        if (cursor.moveToFirst()) {
            do {
                val price = cursor.getFloat(cursor.getColumnIndexOrThrow("PRICE"))
                entries.add(Entry(index, price))
                index += 1f
            } while (cursor.moveToNext())
        }
        cursor.close()

        val dataSet = LineDataSet(entries, "Expenses")
        dataSet.color = Color.WHITE
        dataSet.valueTextColor = Color.WHITE
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 5f
        dataSet.setCircleColor(Color.WHITE)
        dataSet.setDrawFilled(true)
        dataSet.fillColor = Color.GREEN
        dataSet.fillAlpha = 50

        val lineData = LineData(dataSet)
        binding.lineChart.data = lineData
        binding.lineChart.axisLeft.textColor = Color.WHITE
        binding.lineChart.axisRight.isEnabled = false
        binding.lineChart.xAxis.textColor = Color.WHITE
        binding.lineChart.description.isEnabled = false
        binding.lineChart.invalidate() // Frissítés
    }
}