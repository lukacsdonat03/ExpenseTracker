package com.example.expensetracker

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.expensetracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var dbHelper: ExpenseDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        dbHelper = ExpenseDatabaseHelper(this);

        enableEdgeToEdge()

        //setContentView(R.layout.activity_main)
        setContentView(binding.root)

        val spinnerCategories = binding.spinnerCategory

        //Kategóriák Spinner-be töltése induláskor
        val categories = ExpenseCategory.values().map { it.getLocalizedName(this) }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategories.adapter = adapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.navExpenses.setOnClickListener {
            startActivity(Intent(this, LastExpensesActivity::class.java))
        }

        //Expense hozzáadása
        binding.buttonAddExpense.setOnClickListener {

            val priceText = binding.editTextExpense.text.toString().trim()

            if(priceText.isEmpty()){
                Toast.makeText(applicationContext, R.string.empty_amount,Toast.LENGTH_LONG).show()
                return@setOnClickListener   //Így kell return-elni, és így nem fut tovább a kód
            }

            val price = priceText.toDouble()

            if(price == 0.0){
                Toast.makeText(applicationContext, R.string.empty_amount,Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            //Category Id kinyíerése
            val selectedIndex = binding.spinnerCategory.selectedItemPosition
            val category = ExpenseCategory.values()[selectedIndex]

            //Insert
            val db = dbHelper.writableDatabase
            val statement = db.compileStatement(
                "INSERT INTO EXPENSES (CATEGORY,PRICE) VALUES(?,?)"
            )

            statement.bindLong(1,category.code.toLong())
            statement.bindDouble(2,price)

            statement.executeInsert()
            db.close()

            //Input ürítése
            binding.editTextExpense.setText("")
            Toast.makeText(applicationContext,R.string.added_successfuly,Toast.LENGTH_SHORT).show()
        }
    }
}