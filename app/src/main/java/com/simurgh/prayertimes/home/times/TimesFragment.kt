package com.simurgh.prayertimes.home.times

import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.simurgh.prayertimes.R
import com.simurgh.prayertimes.model.AppPreference
import com.simurgh.prayertimes.network.PrayerService
import com.simurgh.prayertimes.room.AppDatabase
import com.simurgh.prayertimes.room.dao.PrayerTimeDao
import io.reactivex.Observable
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_times.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class TimesFragment: Fragment() {

    lateinit var appDatabase: AppDatabase
    lateinit var prayerTimeDao: PrayerTimeDao
    var disposable = CompositeDisposable()

    var latLon: Array<Double> = arrayOf()
    var today: Date? = null
    var day:Int = 0
    var month: Int = 0
    var year: Int = 0
    var method: Int = 2


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_times, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDatabase = AppDatabase.getInstance(context!!)!!
        prayerTimeDao = appDatabase.prayerTimeDao()

        today = getAppPref().getToday()
        day = today!!.date
        month = today!!.month + 1
        year = today!!.year +1900

        getTimings()
        setButtons()

    }

    private fun getTimings(){
        val tempDay = if (day < 10) '0'+day.toString() else day.toString()
        prayerTimeDao
                .getTodayTimes(tempDay, month, year.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: SingleObserver<PrayerTime>{
                    override fun onSuccess(t: PrayerTime) {
                        setTimes(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        Log.e("MY_TAG", "No data found for Date:$tempDay/$month/$year\nRequesting cloud for given date")
                        request(month, year)
                    }
                })
    }

    private fun setButtons(){
        next_time.setOnClickListener {
            day++
            today = getAppPref().addDays(today!!, 1)
            if (today!!.month + 1 > month){
                day = 1
                month = today!!.month + 1
            }
            if (today!!.year + 1900 > year){
                day = 1
                month = 1
                year = today!!.year + 1900
            }
            getTimings()
        }
        prev_time.setOnClickListener{
            day--
            today = getAppPref().addDays(today!!, -1)
            if (today!!.month + 1 < month){
                day = today!!.date
                month = today!!.month + 1
            }
            if (today!!.year + 1900 < year){
                day = today!!.date
                month = today!!.month + 1
                year = today!!.year + 1900
            }
            getTimings()
        }
    }

    private fun setTimes(data: PrayerTime){
        time_today.text = getAppPref().getFormattedTime(data.date!!.timestamp!!)
        time_time_1.text = data.timings!!.Fajr
        time_time_2.text = data.timings!!.Sunrise
        time_time_3.text = data.timings!!.Dhuhr
        time_time_4.text = data.timings!!.Asr
        time_time_5.text = data.timings!!.Maghrib
        time_time_6.text = data.timings!!.Isha
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    fun request(month: Int, year: Int) {

        latLon = getAppPref().getLatLon()
        method = getAppPref().getMethod()

        PrayerService().prayerTimes(latLon[0], latLon[1], method, month, year,  object : Callback<Result>{
            override fun onFailure(call: Call<Result>, t: Throwable) {
                t.printStackTrace()
                Log.e("MY_TAG", "Error while requesting Data from cloud")

            }

            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                if (response.isSuccessful){
                    val result = response.body()!!.data
                    disposable.add(Observable.fromCallable{
                        prayerTimeDao.insert(result!!)
                    }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe{
                                Log.e("MY_TAG", "Data downloaded\n Updating times")
                                getTimings()

                            })

                }
            }
        })
    }

    fun getAppPref(): AppPreference{
        return AppPreference(context!!)
    }
}