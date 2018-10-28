package com.simurgh.prayertimes.home.quran

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class QuranTitle() {
    @PrimaryKey
    var number: Int = 0
    var name: String? = null
    var englishName: String? = null
    var englishNameTranslation: String? = null

    constructor(titleNo: Int, name: String, transcribed: String, translated: String): this(){
        this.number = titleNo
        this.name = name
        this.englishName = transcribed
        this.englishNameTranslation = translated
    }
}