package com.simurgh.prayertimes.model

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices




class AppPreference() {
    lateinit var context: Context

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    companion object {
        const val defaultLat = 38.559800
        const val defaultLon = 68.787000
    }
    constructor(context: Context):this(){
        this.context = context
        sharedPreferences = context.getSharedPreferences("app_preference", 0)
        editor = sharedPreferences.edit().apply {  }
    }

    fun getInstance(): AppPreference{
        return this
    }

    fun getToday(): Date{
        val timestamp = Timestamp(System.currentTimeMillis())
        val sharedCalFormat = SimpleDateFormat("MMyyyy", Locale.US)
        return Date(timestamp.time)
    }

    fun getLatLon(): Array<Double>{
        return arrayOf(sharedPreferences.getFloat("locLat", defaultLat.toFloat()).toDouble(),
                sharedPreferences.getFloat("locLon", defaultLon.toFloat()).toDouble())
    }

    fun setLatLon(lat: Double, lon: Double){
        editor.putFloat("locLat", lat.toFloat())
        editor.putFloat("locLon", lon.toFloat())
        editor.apply()
    }

    fun getAddress(): String{
        return sharedPreferences.getString("address", "Душанбе, Точикистон")
    }

    fun setAddress(address: String){
        editor.putString("address", address).apply()
    }

    fun getMethod(): Int{
        return sharedPreferences.getInt("method", 2)
    }

    fun setMethod(int: Int){
        editor.putInt("method", int)
    }

    fun getFormattedTime(timestamp: String): String{
        val date = Date(timestamp.toLong()*1000)
        val simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return simpleDateFormat.format(date)
    }

    fun addDays(date: Date, days: Int): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DATE, days)
        return cal.time
    }

}