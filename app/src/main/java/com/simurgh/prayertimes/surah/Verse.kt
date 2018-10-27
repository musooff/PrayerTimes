package com.simurgh.prayertimes.surah

import androidx.room.Entity

@Entity(primaryKeys = ["titleNo", "verseNo"])
class Verse() {
    var titleNo: Int = 0
    var verseNo: Int = 0
    var arabic: String? = null
    var tajik: String? = null

    constructor(titleNo: Int,  verseNo: Int, arabic: String, tajik: String): this(){
        this.titleNo = titleNo
        this.verseNo = verseNo
        this.arabic = arabic
        this.tajik = tajik
    }
}
