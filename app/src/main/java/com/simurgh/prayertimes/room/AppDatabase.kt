package com.simurgh.prayertimes.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.simurgh.prayertimes.home.quran.QuranTitle
import com.simurgh.prayertimes.home.times.PrayerTime
import com.simurgh.prayertimes.room.dao.PrayerTimeDao
import com.simurgh.prayertimes.room.dao.QuranTitleDao
import com.simurgh.prayertimes.room.dao.VerseDao
import com.simurgh.prayertimes.surah.Verse

@Database(entities = [
        QuranTitle::class,
        Verse::class,
        PrayerTime::class], version = 1, exportSchema = true)
abstract class AppDatabase: RoomDatabase(){

    abstract fun quranTitleDao(): QuranTitleDao
    abstract fun verseDao(): VerseDao
    abstract fun prayerTimeDao(): PrayerTimeDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java, "app_database")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
