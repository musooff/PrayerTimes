package com.simurgh.prayertimes.home.times

import androidx.annotation.NonNull
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

class SingleTime {
    var Fajr:String? = null
        set(value) {
            field = if (value!!.contains('(')){
                value.substring(0, value.indexOf('(') - 1)
            } else{
                value
            }
        }
    var Sunrise: String? = null
        set(value) {
            field = if (value!!.contains('(')){
                value.substring(0, value.indexOf('(') - 1)
            } else{
                value
            }
        }
    var Dhuhr: String? = null
        set(value) {
            field = if (value!!.contains('(')){
                value.substring(0, value.indexOf('(') - 1)
            } else{
                value
            }
        }
    var Asr: String? = null
        set(value) {
            field = if (value!!.contains('(')){
                value.substring(0, value.indexOf('(') - 1)
            } else{
                value
            }
        }
    var Maghrib: String? = null
        set(value) {
            field = if (value!!.contains('(')){
                value.substring(0, value.indexOf('(') - 1)
            } else{
                value
            }
        }
    var Isha: String? = null
        set(value) {
            field = if (value!!.contains('(')){
                value.substring(0, value.indexOf('(') - 1)
            } else{
                value
            }
        }
    var Midnight:String? = null
        set(value) {
            field = if (value!!.contains('(')){
                value.substring(0, value.indexOf('(') - 1)
            } else{
                value
            }
        }
}

class Gregorean{

    var day: String = ""
    @Embedded
    @NonNull
    var month:Month? = null
    var year: String = ""
}
class Hijri{
    var day: String = ""
    @Embedded
    var month:Month? = null
    var year: String = ""
}
class Month{
    var number: Int = 0
}
class Date{
    var timestamp: String? = null
    @NonNull
    @Embedded(prefix = "gre_")
    var gregorian: Gregorean? = null
    @Embedded(prefix = "hij_")
    var hijri: Hijri? = null

}

@Entity(primaryKeys = ["gre_day", "gre_number", "gre_year"])
class PrayerTime {
    @Embedded
    var timings: SingleTime? = null
    @Embedded
    @NonNull
    var date: Date? = null
}

class Result{
    var data: List<PrayerTime>? = ArrayList()
}