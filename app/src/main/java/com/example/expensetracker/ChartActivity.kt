package com.example.expensetracker

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetracker.databinding.ActivityStaticticsBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

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

        setupBarChart()
        setupMonthlyBarChart()
    }

    private fun setupBarChart() {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT CATEGORY, SUM(PRICE) as TOTAL FROM EXPENSES GROUP BY CATEGORY",
            null
        )

        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        var index = 0

        if (cursor.moveToFirst()) {
            do {
                val categoryCode = cursor.getInt(cursor.getColumnIndexOrThrow("CATEGORY"))
                val total = cursor.getFloat(cursor.getColumnIndexOrThrow("TOTAL"))

                // Az X érték -> label
                entries.add(BarEntry(index.toFloat(), total))

                val category = ExpenseCategory.fromCode(categoryCode)
                labels.add(category.getLocalizedName(this))

                index++
            } while (cursor.moveToNext())
        }
        cursor.close()

        val dataSet = BarDataSet(entries, getString(R.string.expenses_by_categories))   //Valamiért kell a getString
        dataSet.color = Color.GREEN
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 14f

        val barData = BarData(dataSet)
        barData.barWidth = 0.6f

        binding.barChart.data = barData
        binding.barChart.setFitBars(true)

        // X tengely
        val xAxis = binding.barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.granularity = 1f
        xAxis.labelCount = labels.size
        xAxis.textColor = Color.WHITE
        xAxis.setDrawGridLines(false)
        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        xAxis.labelRotationAngle = -20f

        // Y tengely (Ft)
        val leftAxis = binding.barChart.axisLeft
        leftAxis.textColor = Color.WHITE

        // Y tengely magyarázat
        binding.barChart.description.isEnabled = true
        binding.barChart.description.text = getString(R.string.chart_amount)
        binding.barChart.description.textColor = Color.WHITE
        binding.barChart.description.textSize = 12f
        binding.barChart.description.setPosition(200f, 50f)

        binding.barChart.axisRight.isEnabled = false

        // Jelmagyarázat
        binding.barChart.legend.textColor = Color.WHITE
        binding.barChart.legend.textSize = 14f

        binding.barChart.invalidate()
    }

    private fun setupMonthlyBarChart() {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT CATEGORY, SUM(PRICE) as TOTAL 
        FROM EXPENSES 
        WHERE strftime('%Y-%m', CREATED_AT) = strftime('%Y-%m', 'now')
        GROUP BY CATEGORY
        """.trimIndent(),
            null
        )

        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()
        var index = 0

        if (cursor.moveToFirst()) {
            do {
                val categoryCode =
                    cursor.getInt(cursor.getColumnIndexOrThrow("CATEGORY"))
                val total =
                    cursor.getFloat(cursor.getColumnIndexOrThrow("TOTAL"))

                entries.add(BarEntry(index.toFloat(), total))

                val category = ExpenseCategory.fromCode(categoryCode)
                labels.add(category.getLocalizedName(this))

                index++
            } while (cursor.moveToNext())
        }
        cursor.close()

        val dataSet = BarDataSet(entries, getString(R.string.expenses_by_categories))
        dataSet.color = Color.CYAN
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 14f

        val barData = BarData(dataSet)
        barData.barWidth = 0.6f

        binding.barChartMonthly.data = barData
        binding.barChartMonthly.setFitBars(true)

        val xAxis = binding.barChartMonthly.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.WHITE
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -20f

        val leftAxis = binding.barChartMonthly.axisLeft
        leftAxis.textColor = Color.WHITE

        binding.barChartMonthly.axisRight.isEnabled = false
        binding.barChartMonthly.legend.textColor = Color.WHITE

        binding.barChartMonthly.description.text = getString(R.string.this_month)
        binding.barChartMonthly.description.textColor = Color.WHITE

        binding.barChartMonthly.invalidate()
    }

}