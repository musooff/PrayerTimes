package com.simurgh.prayertimes.network

import retrofit2.Callback
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import com.google.gson.GsonBuilder
import com.simurgh.prayertimes.home.times.Result


class PrayerService{

    companion object {
        const val BASE_URL = "http://api.aladhan.com/v1/"
    }

    private fun startService(): PrayerAPI {
        val gson = GsonBuilder()
                .setLenient()
                .create()

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        return retrofit.create(PrayerAPI::class.java)

    }

    fun prayerTimes(latitude: Double, longitude: Double, method: Int, month: Int, year: Int, callback: Callback<Result>){
        val prayerAPI = startService()
        val call = prayerAPI.prayerTimes(latitude, longitude, method, month, year)
        call.enqueue(callback)
    }
}