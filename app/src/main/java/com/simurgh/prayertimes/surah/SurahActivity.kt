package com.simurgh.prayertimes.surah

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simurgh.prayertimes.R
import com.simurgh.prayertimes.model.MyExtensions
import com.simurgh.prayertimes.home.quran.QuranTitle
import com.simurgh.prayertimes.model.AppPreference
import com.simurgh.prayertimes.network.PrayerService
import com.simurgh.prayertimes.room.AppDatabase
import com.simurgh.prayertimes.room.dao.VerseDao
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_surah.*
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class SurahActivity: Activity() {
    
    var verses: List<Verse> = arrayListOf()
    private var surahAdapter = SurahAdapter()

    var titleNo: Int = 0
    var name: String? = null
    private var transcribed: String? = null

    private val compositeDisposable = CompositeDisposable()
    private lateinit var appDatabase: AppDatabase
    private lateinit var verseDao: VerseDao


    companion object {

        const val TITLE_NO = "titleNo"
        const val NAME = "name"
        const val TRANSCRIBED = "transcribed"

        const val BISMILLAH = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ "

        fun newIntent(context: Context, quranTitle: QuranTitle): Intent {
            val intent = Intent(context, SurahActivity::class.java)
            intent.putExtra(TITLE_NO, quranTitle.number)
            intent.putExtra(NAME, quranTitle.name)
            intent.putExtra(TRANSCRIBED, quranTitle.englishName)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surah)

        rv_ayats.layoutManager = LinearLayoutManager(applicationContext)
        rv_ayats.adapter = surahAdapter

        val extras = intent.extras
        name = extras!!.getString(NAME)
        transcribed = extras.getString(TRANSCRIBED)
        titleNo = extras.getInt( TITLE_NO)

        startings()

        appDatabase = AppDatabase.getInstance(applicationContext)!!
        verseDao = appDatabase.verseDao()

        getVerses()


    }

    private fun startings(){
        title_name.text = name
        title_transcribed.text = transcribed
        if (titleNo == 9){
            starting.visibility = View.GONE
        }
    }

    fun getVerses(){
        compositeDisposable.add(Observable.fromCallable {
            val verseList = verseDao.getVerses(titleNo)
            if (verseList.size < 3){
                if (!getAppPref().isConnected()){
                    runOnUiThread { showErrorDialog() }
                    return@fromCallable
                }
                PrayerService().getVerses(titleNo)
                        .doOnError { throwable ->  Log.e("MY_TAG_SURAH",throwable.message)}
                        .subscribe({ surahResult ->
                            for (i in 0 until surahResult.data[0].numberOfAyahs) {
                                val arabic = surahResult.data[0].ayahs[i]
                                val tajik = surahResult.data[1].ayahs[i]
                                verseList.add(Verse(titleNo, arabic.numberInSurah, arabic.text!!, tajik.text!!))
                            }
                            verseDao.insert(verseList)
                            verses = verseList.sortedBy { verse -> verse.number }
                        }, {t -> Log.e("MY_TAG_SURAH", t.message)})

            }
            else verses = verseList
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    gif.visibility = View.GONE
                    surahAdapter.notifyDataSetChanged()
                })
    }

    private fun showErrorDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.network_error)
                .setMessage(R.string.network_error_surah)
                .setPositiveButton(R.string.button_retry) { _, _ ->
                    getVerses()
                    gif.visibility = View.VISIBLE
                }
                .setNegativeButton(R.string.button_exit) {_,_ -> finish()}
                .setCancelable(false)
        builder.create().show()
    }

    private fun getAppPref(): AppPreference {
        return AppPreference(applicationContext)
    }

    
    inner class SurahAdapter: RecyclerView.Adapter<SurahViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.single_ayat,parent, false)
            return SurahViewHolder(view)
        }

        override fun getItemCount(): Int {
            return verses.size
        }

        override fun onBindViewHolder(holder: SurahViewHolder, position: Int) {
            val verse = verses[position]
            holder.arabic.text = verse.text
            holder.translated.text = verse.tajik
            holder.id.text = verse.number.toString()
        }

    }
    
    class SurahViewHolder(view:View): RecyclerView.ViewHolder(view){
        val arabic: TextView = view.findViewById(R.id.tv_arabic)
        val translated: TextView = view.findViewById(R.id.tv_tran)
        val id: TextView = view.findViewById(R.id.tv_id)
    }

    override fun onBackPressed() {
        compositeDisposable.dispose()
        super.onBackPressed()
    }
}