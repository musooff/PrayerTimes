package com.simurgh.prayertimes.home.home

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.simurgh.prayertimes.R
import com.simurgh.prayertimes.extensions.MyExtensions
import com.simurgh.prayertimes.home.times.PrayerTime
import com.simurgh.prayertimes.home.times.Result
import com.simurgh.prayertimes.library.LibraryActivity
import com.simurgh.prayertimes.model.AppPreference
import com.simurgh.prayertimes.names.NamesActivity
import com.simurgh.prayertimes.network.PrayerService
import com.simurgh.prayertimes.room.AppDatabase
import com.simurgh.prayertimes.room.dao.PrayerTimeDao
import com.simurgh.prayertimes.room.dao.VerseDao
import com.simurgh.prayertimes.surah.DayVerse
import com.simurgh.prayertimes.surah.Verse
import com.simurgh.prayertimes.surah.VerseResult
import io.reactivex.Observable
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_home.*
import kotlinx.android.synthetic.main.dialog_verve.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class HomeFragment : Fragment(){

    lateinit var appDatabase: AppDatabase
    lateinit var prayerTimeDao: PrayerTimeDao
    lateinit var verseDao: VerseDao
    var disposable = CompositeDisposable()


    var tomorrow: PrayerTime? = null

    private var latLon: Array<Double> = arrayOf()
    private var today: Date? = null
    private var day:Int = 0
    var month: Int = 0
    var year: Int = 0
    var method: Int = 2


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appDatabase = AppDatabase.getInstance(context!!)!!
        prayerTimeDao = appDatabase.prayerTimeDao()
        verseDao = appDatabase.verseDao()

        today = getAppPref().getToday()
        day = today!!.date
        month = today!!.month + 1
        year = today!!.year +1900

        getTimings(true)
        setOneName()
        setOneHadis()
        setOneAyah()
        setOneDua()

        //downloadVerses()

        tv_read_name.setOnClickListener {
            val names = Intent(activity, NamesActivity::class.java)
            startActivity(names)
        }

        tv_lib.setOnClickListener{
            val lib = Intent(activity, LibraryActivity::class.java)
            startActivity(lib)
        }

        tv_share.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Барномаи Исломӣ анакнун бо забони тоҷикӣ")
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.simurgh.prayertimes")
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }

    }

    private fun getTimings(isToday: Boolean = false){
        val tempDay = if (day < 10) '0'+day.toString() else day.toString()
        prayerTimeDao
                .getTodayTimes(tempDay, month, year.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: SingleObserver<PrayerTime> {
                    override fun onSuccess(t: PrayerTime) {
                        if (isToday){
                            setPrayerTime(t)
                        }
                        else{
                            tomorrow = t
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        Log.e("MY_TAG_HOME", "No data found for Date:$tempDay/$month/$year\nRequesting cloud for given date")
                        request(month, year)
                    }
                })
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
                tv_prayer_name.text = "Хуфтан"
                timeCountDown( t1 - nowMin, prayerTime)
            }
            nowMin in (t1 + 1)..t2 -> {
                tv_prayer_name.text = "Бомдод"
                timeCountDown( t2 - nowMin, prayerTime)
            }
            nowMin in (t2 + 1)..t3 -> {
                tv_prayer_name.text = "Тулуъ"
                timeCountDown( t3 - nowMin, prayerTime)
            }
            nowMin in (t3 + 1)..t4 -> {
                tv_prayer_name.text = "Пешин"
                timeCountDown(t4 - nowMin, prayerTime)
            }
            nowMin in (t4 + 1)..t5 -> {
                tv_prayer_name.text = "Арс"
                timeCountDown( t5 - nowMin, prayerTime)
            }
            nowMin in (t5 + 1)..t6 -> {
                tv_prayer_name.text = "Шом"
                timeCountDown( t6 - nowMin, prayerTime)
            }
            nowMin > t6 -> tv_prayer_name.text = "Хуфтан"
        }
    }

    private fun getMinutes(time: String): Int {
        val units = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val hours = Integer.parseInt(units[0])
        val minutes = Integer.parseInt(units[1])
        return 60 * hours + minutes
    }

    private fun timeCountDown(minute: Int, prayerTime: PrayerTime){
        object: CountDownTimer(minute * 60000L, 60000L){
            override fun onFinish() {
                tv_next_prayer_remaining.text = ""
                setPrayerTime(prayerTime)
            }

            override fun onTick(millisUntilFinished: Long) {
                tv_next_prayer_remaining.text = getTime((millisUntilFinished/60000).toInt() + 1)
            }
        }.start()

    }
    private fun getTime(minute: Int): String {
        if (minute/60 == 0){
            return ""+(minute%60)+" дакика барои итмом"
        }
        return ""+(minute/60).toString()+" соату "+(minute%60)+" дакика барои итмом"
    }

    private fun setOneName() {
        val random = Random()
        val names = resources.getString(R.string.names)
        val eachNames = names.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val randomInt = 1 + random.nextInt(99)
        Log.e("MY_TAG", randomInt.toString() + "")
        val singleName: Array<String>
        singleName = eachNames[randomInt].split(" — ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        tv_name_arabic.text = singleName[1]
        tv_name_arabic_transcribed.text = singleName[0]
        tv_name_tajik.text = singleName[2]

    }

    private fun setOneHadis() {
        val random = Random()
        val randomInt = random.nextInt(15)
        try {
            val inputStream = activity!!.resources.assets.open("hadises.json")
            val jsonObject = JSONObject(MyExtensions.readStream(inputStream))
            val data = jsonObject.getJSONArray("data")
            val hadis = data.getJSONObject(randomInt).getString("hadis")
            val source = data.getJSONObject(randomInt).getString("soursce")
            tv_hadis_name.text = source
            tv_hadis.text = hadis

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun setOneDua() {
        val random = Random()
        val randomInt = random.nextInt(10)
        try {
            val inputStream = activity!!.resources.assets.open("duas.json")
            val jsonObject = JSONObject(MyExtensions.readStream(inputStream))
            val data = jsonObject.getJSONArray("data")
            val arabic = data.getJSONObject(randomInt).getString("arabic")
            val tajikTranscribed = data.getJSONObject(randomInt).getString("tajikTranscribed")
            val tajik = data.getJSONObject(randomInt).getString("tajik")
            val name = data.getJSONObject(randomInt).getString("name")

            tv_dua_arabic.text = arabic
            tv_dua_arabic_transcibed.text = tajikTranscribed
            tv_dua_name.text = name
            tv_dua_tajik.text = tajik

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun setOneAyah(){
        verseDao.getRandomVerse()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<DayVerse>{
                    override fun onSuccess(t: DayVerse) {
                        tv_verse.text = t.tajik
                        tv_verse_name.text = "${t.quranTitles[0].englishName} (${t.titleNo}:${t.number})"

                        tv_read_verse.setOnClickListener {
                            val dialog = Dialog(activity!!)
                            dialog.setContentView(R.layout.dialog_verve)

                            val diaArabic = dialog.findViewById<View>(R.id.tv_arabic) as TextView
                            val diaTranslate = dialog.findViewById<View>(R.id.tv_tran) as TextView
                            val tajName = dialog.findViewById<View>(R.id.tv_tajName) as TextView
                            val araName = dialog.findViewById<View>(R.id.tv_araName) as TextView

                            diaArabic.text = t.text
                            diaTranslate.text = t.tajik
                            tajName.text = t.quranTitles[0].englishName
                            araName.text = t.quranTitles[0].name
                            dialog.show()
                        }
                    }
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        Log.e("MY_TAG_HOME", "Error while getting verse from DB")
                    }
                })
    }

    private fun downloadVerses(){
        for (i in 0 until 50) {
            val ranVerse = Random().nextInt(6236) + 1
            PrayerService().randomVerse(ranVerse, object : Callback<VerseResult> {
                override fun onFailure(call: Call<VerseResult>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("MY_TAG_HOME", "Error while requesting Quran Data from cloud")
                }

                override fun onResponse(call: Call<VerseResult>, response: Response<VerseResult>) {
                    if (response.isSuccessful) {
                        val result = response.body()!!.data
                        Observable.fromCallable{
                            verseDao.insert(Verse(result[0].surah!!.number, result[0].numberInSurah, result[0].text!!, result[1].text!!))
                        }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe()
                    }
                }
            })
        }
    }



    private fun getAppPref(): AppPreference {
        return AppPreference(context!!)
    }

    private fun request(month: Int, year: Int) {

        latLon = getAppPref().getLatLon()
        method = getAppPref().getMethod()

        PrayerService().prayerTimes(latLon[0], latLon[1], method, month, year,  object : Callback<Result> {
            override fun onFailure(call: Call<Result>, t: Throwable) {
                t.printStackTrace()
                Log.e("MY_TAG_HOME", "Error while requesting Data from cloud")

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
                                Log.e("MY_TAG_HOME", "Data downloaded\n Updating times")
                                getTimings()

                            })

                }
            }
        })
    }

}