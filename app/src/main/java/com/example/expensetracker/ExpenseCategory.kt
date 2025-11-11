package com.example.expensetracker

import android.content.Context

enum class ExpenseCategory(val code: Int) {
    FOOD(0),
    SHOPPING(1),
    TRANSPORT(2),
    ENTERTAINMENT(3),
    OTHER(4);


    //DB-BEN INTEGER-KÉNT VAN TÁROLVA
    companion object {
        fun fromCode(code: Int): ExpenseCategory {
            return entries.find { it.code == code } ?: OTHER
        }
    }

    //FORDÍTÁS MIATT
    fun getLocalizedName(context: Context): String {
        val resId = when (this) {
            FOOD -> R.string.category_food
            SHOPPING -> R.string.category_shopping
            TRANSPORT -> R.string.category_transport
            ENTERTAINMENT -> R.string.category_entertainment
            OTHER -> R.string.category_other
        }
        return context.getString(resId)
    }
}