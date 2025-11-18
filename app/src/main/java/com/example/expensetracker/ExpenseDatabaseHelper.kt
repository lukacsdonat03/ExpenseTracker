package com.example.expensetracker

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ExpenseDatabaseHelper(context: Context): SQLiteOpenHelper(context, "EXPENSE_DB",null,1) {
    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL("CREATE TABLE IF NOT EXISTS EXPENSES(" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "CATEGORY INTEGER," +
            " PRICE REAL,  CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP)")

    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        db?.execSQL("DROP TABLE IF EXISTS EXPENSES")
        onCreate(db)
    }

    fun insertExpense(categoryCode: Int, price: Float): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("CATEGORY", categoryCode)
            put("PRICE", price)
            // CREATED_AT automatikusan kitöltődik a DEFAULT CURRENT_TIMESTAMP miatt
        }
        return db.insert("EXPENSES", null, values)
    }

}