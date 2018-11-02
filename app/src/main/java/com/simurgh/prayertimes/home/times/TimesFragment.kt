package com.simurgh.prayertimes.home.times

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.location.places.ui.PlaceAutocomplete.getStatus
import com.google.android.gms.location.places.Place
import android.content.Intent
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import io.reactivex.Completable


class TimesFragment: Fragment() {

    companion object {
        const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 13

    }

    private lateinit var appDatabase: AppDatabase
    lateinit var prayerTimeDao: PrayerTimeDao
    var disposable = CompositeDisposable()

    var timer: CountDownTimer? = null

    lateinit var curName: TextView
    lateinit var curTime:TextView

    private var latLon: Array<Double> = arrayOf()
    private var today: Date? = null
    private var realToday: Date? = null

    private var day:Int = 0
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
        realToday = today
        day = today!!.date
        month = today!!.month + 1
        year = today!!.year +1900

        setButtons()
        setLocationUpdater()

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

    private fun setPrayerTime(prayerTime: PrayerTime){

        val timings = prayerTime.timings
        val now = getAppPref().getFormattedNow(getAppPref().getToday())
        val nowMin = getMinutes(now)
        val t1 = getMinutes(timings!!.Fajr!!)
        val t2 = getMinutes(timings.Sunrise!!)
        val t3 = getMinutes(timings.Dhuhr!!)
        val t4 = getMinutes(timings.Asr!!)
        val t5 = getMinutes(timings.Maghrib!!)
        val t6 = getMinutes(timings.Isha!!)

        when {
            nowMin <= t1 -> {
                curName = time_name_6
                curTime = time_time_6
                timeCountDown( t1 - nowMin, prayerTime)
            }
            nowMin in (t1 + 1)..t2 -> {
                curName = time_name_1
                curTime = time_time_1
                timeCountDown( t2 - nowMin, prayerTime)
            }
            nowMin in (t2 + 1)..t3 -> {
                curName = time_name_2
                curTime = time_time_2
                timeCountDown( t3 - nowMin, prayerTime)
            }
            nowMin in (t3 + 1)..t4 -> {
                curName = time_name_3
                curTime = time_time_3
                timeCountDown(t4 - nowMin, prayerTime)
            }
            nowMin in (t4 + 1)..t5 -> {
                curName = time_name_4
                curTime = time_time_4
                timeCountDown( t5 - nowMin, prayerTime)
            }
            nowMin in (t5 + 1)..t6 -> {
                curName = time_name_5
                curTime = time_time_5
                timeCountDown( t6 - nowMin, prayerTime)
            }
            nowMin > t6 -> {
                curName = time_name_6
                curTime = time_time_6
            }

        }

        curName.setTextColor(resources.getColor(R.color.greenMain))
        curTime.setTextColor(resources.getColor(R.color.greenMain))

        time_location.text = getAppPref().getAddress()

    }

    private fun setLocationUpdater(){
        time_location.setOnClickListener{
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(activity)
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
        }
    }

    private fun timeCountDown(minute: Int, prayerTime: PrayerTime){
        timer = object: CountDownTimer(minute * 60000L, minute * 60000L){
            override fun onFinish() {
                curName.setTextColor(resources.getColor(R.color.black))
                curTime.setTextColor(resources.getColor(R.color.black))
                setPrayerTime(prayerTime)
            }

            override fun onTick(millisUntilFinished: Long) {
            }
        }.start()

    }

    private fun getMinutes(time: String): Int {
        val units = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val hours = Integer.parseInt(units[0])
        val minutes = Integer.parseInt(units[1])
        return 60 * hours + minutes
    }

    private fun setTimes(data: PrayerTime){
        time_today.text = getAppPref().getFormattedTime(data.date!!.timestamp!!)
        time_time_1.text = data.timings!!.Fajr
        time_time_2.text = data.timings!!.Sunrise
        time_time_3.text = data.timings!!.Dhuhr
        time_time_4.text = data.timings!!.Asr
        time_time_5.text = data.timings!!.Maghrib
        time_time_6.text = data.timings!!.Isha

        if (realToday == today){
            setPrayerTime(data)
        }
        else{
            curName.setTextColor(resources.getColor(R.color.black))
            curTime.setTextColor(resources.getColor(R.color.black))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
        disposable.dispose()
    }
    override fun onPause() {
        timer?.cancel()
        super.onPause()

    }

    override fun onResume() {
        super.onResume()
        getTimings()
    }

    private fun showErrorDialog(month: Int, year: Int){
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(R.string.network_error)
                .setMessage(R.string.network_error_splash)
                .setPositiveButton(R.string.button_retry) { _, _ -> request(month, year) }
                .setNegativeButton(R.string.button_ok){dialog,_ -> dialog.dismiss()}
        builder.create().show()
    }

    fun request(month: Int, year: Int) {
        if (!getAppPref().isConnected()){
            showErrorDialog(month, year)
            return
        }

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
                                Log.e("MY_TAG_TIMES", "Data downloaded\n Updating times")
                                getTimings()

                            })

                }
            }
        })
    }

    fun getAppPref(): AppPreference{
        return AppPreference(context!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    val place = PlaceAutocomplete.getPlace(activity!!.applicationContext, data)
                    updateLocation(place)
                    Log.i("MY_TAG_TIMES", "Place: " + place.name)
                }
                PlaceAutocomplete.RESULT_ERROR -> {
                    val status = PlaceAutocomplete.getStatus(activity!!.applicationContext, data)
                    Log.i("MY_TAG_TIMES", status.statusMessage)

                }
                RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
        }
    }

    private fun updateLocation(place: Place){
        getAppPref().setLatLon(place.latLng.latitude, place.latLng.longitude)
        getAppPref().setAddress(place.name.toString())
        Completable.fromAction {
            prayerTimeDao.deleteAll()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

}