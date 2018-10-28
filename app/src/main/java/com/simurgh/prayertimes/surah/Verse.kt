package com.simurgh.prayertimes.surah

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Relation
import com.simurgh.prayertimes.home.quran.QuranTitle

@Entity(primaryKeys = ["number", "titleNo"])
open class Verse() {
    var number: Int = 0
    var titleNo: Int = 0
    var text: String? = null
    var tajik: String? = null

    @Ignore
    var surah: Surah? = null
    @Ignore
    var numberInSurah: Int = 0

    constructor(titleNo: Int,  number: Int, arabic: String, tajik: String): this(){
        this.number = number
        this.titleNo = titleNo
        this.text = arabic
        this.tajik = tajik
    }
}

class Surah{
    var number: Int = 0
}

class DayVerse: Verse(){
    @Relation(parentColumn = "titleNo", entityColumn = "number")
    var quranTitles: List<QuranTitle> = ArrayList()
}

class VerseResult{
    var data: List<Verse> = ArrayList()
}