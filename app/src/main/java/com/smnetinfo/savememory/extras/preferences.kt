package com.smnetinfo.savememory.extras

import android.content.Context

class preferences {


    fun setGender(context: Context, userName: String) {
        val sharedPreferences = context.getSharedPreferences("save", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("GENDER", userName).apply()
    }

    fun getGender(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("save", Context.MODE_PRIVATE)
        return sharedPreferences.getString("GENDER", "")
    }


    fun setNationality(context: Context, userName: String) {
        val sharedPreferences = context.getSharedPreferences("save", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("Nationality", userName).apply()
    }

    fun getNationality(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("save", Context.MODE_PRIVATE)
        return sharedPreferences.getString("Nationality", "")
    }


    fun setMarital(context: Context, userName: String) {
        val sharedPreferences = context.getSharedPreferences("save", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("MARITAL", userName).apply()
    }

    fun getMarital(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("save", Context.MODE_PRIVATE)
        return sharedPreferences.getString("MARITAL", "")
    }
}