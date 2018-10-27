package com.simurgh.prayertimes.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.simurgh.prayertimes.surah.Verse

@Dao
interface VerseDao {
    @Query("SELECT * FROM Verse WHERE titleNo = :titleNo ORDER BY verseNo ASC")
    fun getVerses(titleNo: Int): MutableList<Verse>

    @Insert
    fun insert(verses: MutableList<Verse>)
}