package com.lxh.andoridtest.util

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.lxh.andoridtest.AppApplication
import java.lang.Exception

object SharedPreferencesUtil {

    private val sharedPreferences by lazy {
        AppApplication.instance .getSharedPreferences(AppApplication.instance.packageName, Context.MODE_PRIVATE)
    }

    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun putFloat(key: String, value: Float) {
        sharedPreferences.edit().putFloat(key, value).apply()
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }

    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

//    // 保存bean对象
//    fun saveBeanByFastJson(key: String?, obj: Any?) {
//        val toJson = Gson().toJson(obj)
//        sharedPreferences.edit().putString(key, toJson).apply()
//    }
//
//    // 提取bean对象
//    fun <T> getBeanByFastJson(key: String?, clazz: Class<T>?): T? {
//        val string: String? = sharedPreferences.getString(key, "")
//        return Gson().fromJson(string, clazz)
//    }
//
//    // 提取list对象
//    fun <T> getListBeanByFastJson(key: String?, clazz: Class<T>?): List<T>? {
//        val string = sharedPreferences.getString(key, "")
//        val type = object : TypeToken<ArrayList<JsonObject?>?>() {}.type
//        val jsonObjects = Gson().fromJson<ArrayList<JsonObject>>(string, type) ?: return null
//        val arrayList = ArrayList<T>()
//        for (json in jsonObjects) {
//            arrayList.add(Gson().fromJson(json, clazz))
//        }
//        return arrayList
//    }


    fun <T> saveList(tag: String?, data: T?) {
        if (null == data) return
        val gson = Gson()
        val strJson = gson.toJson(data)
        sharedPreferences.edit().putString(tag, strJson).apply()
    }

    fun <T> getDataList(tag: String?, cls: Class<T>?): MutableList<T>? {
        val datalist: MutableList<T> = mutableListOf()
        val strJson: String = sharedPreferences.getString(tag, null) ?: return datalist
        try {
            val gson = Gson()
            val array: JsonArray = JsonParser().parse(strJson).asJsonArray
            for (jsonElement in array) {
                datalist.add(gson.fromJson(jsonElement, cls))
            }
        } catch (e: Exception) {
        }
        return datalist
    }

}

