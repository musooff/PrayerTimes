package com.simurgh.prayertimes.splash

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.simurgh.prayertimes.R
import com.simurgh.prayertimes.home.HomeBaseActivity
import com.simurgh.prayertimes.home.quran.QuranTitle
import com.simurgh.prayertimes.home.times.PrayerTime
import com.simurgh.prayertimes.home.times.Result
import com.simurgh.prayertimes.model.AppPreference
import com.simurgh.prayertimes.model.MyExtensions
import com.simurgh.prayertimes.network.PrayerService
import com.simurgh.prayertimes.room.AppDatabase
import com.simurgh.prayertimes.room.dao.PrayerTimeDao
import com.simurgh.prayertimes.room.dao.QuranTitleDao
import com.simurgh.prayertimes.room.dao.VerseDao
import com.simurgh.prayertimes.surah.Verse
import com.simurgh.prayertimes.surah.VerseResult
import io.reactivex.Observable
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class SplashActivity: Activity(){

    companion object {
        const val TODAY_VERSE_COUNT = 31
    }

    private lateinit var appDatabase: AppDatabase
    lateinit var prayerTimeDao: PrayerTimeDao
    lateinit var verseDao: VerseDao
    private lateinit var quranTitleDao: QuranTitleDao

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var today: Date? = null

    private var isPermissionTime = false
    private var month1downloaded = false
    private var month2downloaded = false
    private var isShowingError = false

    private var versesDownloaded = 0

    private var day: Int = 0
    var month: Int = 0
    var year: Int = 0
    var method: Int = 2

    var nextMonth = 0
    var yearOfNextMonth  = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        appDatabase = AppDatabase.getInstance(applicationContext)!!
        prayerTimeDao = appDatabase.prayerTimeDao()
        verseDao = appDatabase.verseDao()
        quranTitleDao = appDatabase.quranTitleDao()

        if (!getAppPref().isQuranTitlesSaved()){ saveVerseNames() }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCheck()

        today = getAppPref().getToday()
        day = today!!.date
        month = today!!.month + 1
        year = today!!.year +1900

        nextMonth = getAppPref().getNextMonth(today!!)
        yearOfNextMonth = if (nextMonth == 1) (year+1) else year

        timingsCheck()

        if (!getAppPref().isVersesExist()) downloadVerses() else versesDownloaded = TODAY_VERSE_COUNT

    }

    private fun timingsCheck(){
        prayerTimeDao
                .getThisAndNextMonth(arrayListOf(month, nextMonth), arrayListOf(year.toString(), yearOfNextMonth.toString()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: SingleObserver<List<PrayerTime>> {
                    override fun onSuccess(t: List<PrayerTime>) {
                        when (t.size) {
                            0 -> {
                                Log.e("MY_TAG_SPLASH", "No data found. Requesting cloud for $month/$year and $nextMonth/$yearOfNextMonth")
                                request(month, year)
                                request(nextMonth, yearOfNextMonth)
                            }
                            1 -> {
                                when (t[0].date!!.gregorian!!.month!!.number) {
                                    month -> {
                                        Log.e("MY_TAG_SPLASH", "Only one month's data found for $month/$year. Requesting cloud for $nextMonth/$yearOfNextMonth")
                                        month1downloaded = true
                                        request(nextMonth, yearOfNextMonth)
                                    }
                                    nextMonth -> {
                                        Log.e("MY_TAG_SPLASH", "Only one month's data found for $nextMonth/$yearOfNextMonth. Requesting cloud for $month/$year")
                                        month2downloaded = true
                                        request(month, year)
                                    }
                                }
                            }
                            2 -> {
                                Log.e("MY_TAG_SPLASH", "Data exists for $month/$year and $nextMonth/$yearOfNextMonth. Calling onFinish")
                                month1downloaded = true
                                month2downloaded = true
                                onFinished()
                            }
                        }
                    }
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        Log.e("MY_TAG_SPLASH", "Error while requesting database for $month/$year and $nextMonth/$yearOfNextMonth")
                    }
                })
    }

    private fun locationCheck(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isPermissionTime = true
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), HomeBaseActivity.locationRequestCode)
        }
    }

    private fun updateLocationData(location: Location){
        val addresses: List<Address>
        val geoCoder = Geocoder(applicationContext, Locale.getDefault())
        try {
            addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
            val address = addresses[0].getAddressLine(0)
            Log.e("MY_TAG_SPLASH", "Address is update to $address, Request downloading new times for $month/$year and $nextMonth/$yearOfNextMonth")
            AppPreference(applicationContext).setLatLon(location.latitude, location.longitude)
            AppPreference(applicationContext).setAddress(address)

            month1downloaded = false
            month2downloaded = false

            request(month, year)
            request(nextMonth, yearOfNextMonth)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,grantResults: IntArray) {
        when (requestCode) {
            1000 -> {
                isPermissionTime = false
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return
                    }
                    mFusedLocationClient!!.lastLocation.addOnSuccessListener {
                        if (it != null){
                            updateLocationData(it)
                        }
                        else onFinished()
                    }
                } else {
                    Toast.makeText(applicationContext, "Ичоза ба истифида барии макон гирифта нашуд. Вактхои Душанберо бор мекунем!", Toast.LENGTH_LONG).show()
                    Log.e("MY_TAG_SPLASH", "Permission denied. Calling onFinish")
                    onFinished()
                }
            }
        }
    }

    private fun getAppPref(): AppPreference {
        return AppPreference(applicationContext)
    }

    private fun showErrorDialog(){
        if (!isShowingError){
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.network_error)
                    .setMessage(R.string.network_error_splash)
                    .setPositiveButton(R.string.button_retry) { _, _ -> restart() }
                    .setNegativeButton(R.string.button_exit){_,_ -> finish()}
            builder.create().show()
        }
    }

    private fun restart(){
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun request(downMonth: Int, year: Int) {
        if (!getAppPref().isConnected()){
            showErrorDialog()
            isShowingError = true
            return
        }
        val latLon = getAppPref().getLatLon()
        method = getAppPref().getMethod()

        PrayerService().prayerTimes(latLon[0], latLon[1], method, downMonth, year,  object : Callback<Result> {
            override fun onFailure(call: Call<Result>, t: Throwable) {
                t.printStackTrace()
                Log.e("MY_TAG_SPLASH", "Error while requesting data from cloud for $downMonth/$year")

            }

            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                if (response.isSuccessful){
                    val result = response.body()!!.data
                    Observable.fromCallable{
                        prayerTimeDao.insert(result!!)
                    }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnComplete{
                                when(downMonth){
                                    month -> month1downloaded = true
                                    nextMonth -> month2downloaded = true
                                }
                                Log.e("MY_TAG_SPLASH", "Data downloaded for $downMonth/$year. Calling onFinish")
                                onFinished()
                            }
                            .subscribe()

                }
            }
        })
    }

    private fun onFinished(){
        if (!isPermissionTime && month1downloaded && month2downloaded && (versesDownloaded == TODAY_VERSE_COUNT) && getAppPref().isQuranTitlesSaved()) {
            val intent = Intent(this, HomeBaseActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun downloadVerses(){
        if (!getAppPref().isConnected()){
            showErrorDialog()
            isShowingError = true
            return
        }
        val titleNos = resources.getIntArray(R.array.titleNos)
        val verseNos = resources.getIntArray(R.array.verseNos)
        for (i in 0 until TODAY_VERSE_COUNT) {
            val key = "" + titleNos[i] + ":" + verseNos[i]
            PrayerService().getVerse(key, object : Callback<VerseResult> {
                override fun onFailure(call: Call<VerseResult>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("MY_TAG_HOME", "Error while requesting Quran Data from cloud")
                }

                override fun onResponse(call: Call<VerseResult>, response: Response<VerseResult>) {
                    if (response.isSuccessful) {
                        val result = response.body()!!.data
                        Observable.fromCallable{
                            verseDao.insert(Verse(result[0].surah!!.number, result[0].numberInSurah, result[0].text!!, result[1].text!!, true))
                        }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnComplete {
                                    versesDownloaded++
                                    if (versesDownloaded == TODAY_VERSE_COUNT) getAppPref().setVersesExist(true)
                                    Log.e("MY_TAG_SPLASH", "Verse downloaded for $i. Calling OnFinish")
                                    onFinished()
                                }
                                .subscribe()
                    }
                }
            })
        }
    }

    private fun saveVerseNames(){
        try {
            val inputStream = applicationContext.resources.assets.open("surahs.json")
            val jsonObject = JSONObject(MyExtensions.readStream(inputStream))
            val dataSurah = jsonObject.getJSONArray("quranTitles")

            val quranTitles = arrayListOf<QuranTitle>()
            for (i in 0 until dataSurah.length()) {

                val titleNo = dataSurah.getJSONObject(i).getInt("titleNo")
                val name = dataSurah.getJSONObject(i).getString("name")
                val transcribed = dataSurah.getJSONObject(i).getString("transcribed")
                val translated = dataSurah.getJSONObject(i).getString("translated")

                quranTitles.add(QuranTitle(titleNo, name, transcribed, translated))
            }

            Observable.fromCallable {
                quranTitleDao.insert(quranTitles)
            }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete {
                        getAppPref().setQuranTitlesSaved(true)
                        Log.e("MY_TAG_SPLASH", "Titles converted. Calling onFinish")
                        onFinished()
                    }
                    .subscribe()

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}