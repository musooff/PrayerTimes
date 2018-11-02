package com.simurgh.prayertimes.network

import retrofit2.Callback
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.simurgh.prayertimes.home.times.Result
import com.simurgh.prayertimes.surah.SurahResult
import com.simurgh.prayertimes.surah.Verse
import com.simurgh.prayertimes.surah.VerseResult
import io.reactivex.Observable


class PrayerService{

    companion object {
        const val PRAYER_BASE_URL = "http://api.aladhan.com/v1/"
        const val QURAN_BASE_URL = "http://api.alquran.cloud/"
    }

    private fun startPrayerService(): PrayerAPI {
        val gson = GsonBuilder()
                .setLenient()
                .create()

        val retrofit = Retrofit.Builder()
                .baseUrl(PRAYER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        return retrofit.create(PrayerAPI::class.java)

    }

    private fun startQuranService(): PrayerAPI {
        val gson = GsonBuilder()
                .setLenient()
                .create()

        val retrofit = Retrofit.Builder()
                .baseUrl(QURAN_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        return retrofit.create(PrayerAPI::class.java)

    }


    private fun startQuranServiceObservable(): PrayerAPI {
        val gson = GsonBuilder()
                .setLenient()
                .create()

        val retrofit = Retrofit.Builder()
                .baseUrl(QURAN_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        return retrofit.create(PrayerAPI::class.java)

    }

    fun prayerTimes(latitude: Double, longitude: Double, method: Int, month: Int, year: Int, callback: Callback<Result>){
        val prayerAPI = startPrayerService()
        val call = prayerAPI.prayerTimes(latitude, longitude, method, month, year)
        call.enqueue(callback)
    }

    fun getVerse(key: String, callback: Callback<VerseResult>){
        val prayerAPI = startQuranService()
        val call = prayerAPI.getVerse(key)
        call.enqueue(callback)
    }

    fun getVerses(titleNo: Int): Observable<SurahResult>{
        val prayerAPI = startQuranServiceObservable()
        return prayerAPI.getAllVerses(titleNo)
    }
}