package com.simurgh.prayertimes.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.simurgh.prayertimes.home.quran.QuranTitle

@Dao
interface QuranTitleDao {
    @Query("SELECT * FROM QuranTitle")
    fun getAll(): MutableList<QuranTitle>

    @Insert
    fun insert(quranTitles: MutableList<QuranTitle>)
}