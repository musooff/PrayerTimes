package com.simurgh.prayertimes.network

import com.simurgh.prayertimes.home.times.PrayerTime
import com.simurgh.prayertimes.home.times.Result
import com.simurgh.prayertimes.surah.Verse
import com.simurgh.prayertimes.surah.VerseResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface PrayerAPI {
    @GET("calendar")
    fun prayerTimes(@Query("latitude") latitude: Double, @Query("longitude") longitude: Double, @Query("method") method: Int, @Query("month") month: Int, @Query("year") year: Int): Call<Result>

    @GET("ayah/{verseNo}/editions/quran-simple,tg.ayati")
    fun randomVerse(@Path("verseNo") verseNo: Int): Call<VerseResult>
}