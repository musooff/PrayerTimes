package com.simurgh.prayertimes.home.quran

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class QuranTitle() {
    @PrimaryKey
    var titleNo: Int = 0
    var name: String? = null
    var transcribed: String? = null
    var translated: String? = null

    constructor(titleNo: Int, name: String, transcribed: String, translated: String): this(){
        this.titleNo = titleNo
        this.name = name
        this.transcribed = transcribed
        this.translated = translated
    }
}