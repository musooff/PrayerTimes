package com.simurgh.prayertimes.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.simurgh.prayertimes.home.quran.QuranTitle
import com.simurgh.prayertimes.room.dao.QuranTitleDao

@Database(entities = [QuranTitle::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase(){

    abstract fun quranTitleDao(): QuranTitleDao

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
