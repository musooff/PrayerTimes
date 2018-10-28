package com.simurgh.prayertimes.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.simurgh.prayertimes.surah.DayVerse
import com.simurgh.prayertimes.surah.Verse
import io.reactivex.Single

@Dao
interface VerseDao {
    @Query("SELECT * FROM Verse WHERE titleNo = :titleNo ORDER BY number ASC")
    fun getVerses(titleNo: Int): MutableList<Verse>

    @Query("SELECT * FROM Verse ORDER BY RANDOM() LIMIT 1")
    fun getRandomVerse(): Single<DayVerse>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(verses: MutableList<Verse>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(verse: Verse)
}