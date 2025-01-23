package com.myjar.jarassignment.data.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.myjar.jarassignment.data.model.ComputerItem
import java.lang.reflect.Type

class PreferenceManager(context: Context) {

    private val sharedPref = context.getSharedPreferences("LIST_DATA", Context.MODE_PRIVATE)
    val key = "LIST_DATA"
    val gson = Gson()

    fun saveData(data: List<ComputerItem>) {
        val stringData = gson.toJson(data)
        sharedPref.edit().putString(key, stringData).apply()
    }

    fun retriveData(): List<ComputerItem> {
        val json = sharedPref.getString(key, null)
        val type: Type = object : TypeToken<List<ComputerItem?>?>() {}.type

        val dataList  = gson.fromJson<Any>(json, type) as List<ComputerItem>
        return  dataList
    }
}