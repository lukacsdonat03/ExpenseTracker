package com.example.expensetracker

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.expensetracker.databinding.LastExpensesBinding

class LastExpensesActivity: AppCompatActivity() {

    private lateinit var binding: LastExpensesBinding
    private lateinit var dbHelper: ExpenseDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LastExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = ExpenseDatabaseHelper(this)

        binding.navHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        loadExpenses()
    }

    private fun loadExpenses() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT ID, CATEGORY, PRICE, CREATED_AT FROM EXPENSES ORDER BY CREATED_AT DESC LIMIT 10",
            null
        )

        val expenseList = mutableListOf<String>()
        val expenseIds = mutableListOf<Int>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
                val categoryCode = cursor.getInt(cursor.getColumnIndexOrThrow("CATEGORY"))
                val price = cursor.getFloat(cursor.getColumnIndexOrThrow("PRICE"))
                val createdAt = cursor.getString(cursor.getColumnIndexOrThrow("CREATED_AT"))

                val category = ExpenseCategory.fromCode(categoryCode)
                expenseList.add("${category.getLocalizedName(this)}: $price Ft\n$createdAt")
                expenseIds.add(id)
            } while (cursor.moveToNext())
        } else {
            expenseList.add(getString(com.example.expensetracker.R.string.no_expenses_yet))
        }

        cursor.close()

        //val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, expenseList)

        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, expenseList) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(ContextCompat.getColor(context, R.color.white))
                return view
            }
        }

        binding.listViewExpenses.adapter = adapter

        binding.listViewExpenses.setOnItemLongClickListener { _, _, position, _ ->
            if (expenseIds.isNotEmpty() && position < expenseIds.size) {
                val expenseId = expenseIds[position]
                db.delete("EXPENSES", "ID=?", arrayOf(expenseId.toString()))
                Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show()
                loadExpenses() // újratöltés
            }
            true
        }
    }



}