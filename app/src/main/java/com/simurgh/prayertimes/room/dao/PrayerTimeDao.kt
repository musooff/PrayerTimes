package com.simurgh.prayertimes.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.simurgh.prayertimes.home.times.PrayerTime
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface PrayerTimeDao {
    @Query("SELECT * FROM PrayerTime")
    fun getAll(): Flowable<List<PrayerTime>>

    @Query("SELECT * FROM PrayerTime WHERE gre_day = :day AND gre_number = :month AND gre_year = :year")
    fun getTodayTimes(day:String, month: Int, year: String): Single<PrayerTime>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(prayerTimes: List<PrayerTime>)
}