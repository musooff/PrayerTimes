package com.simurgh.prayertimes.surah

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simurgh.prayertimes.R
import com.simurgh.prayertimes.extensions.MyExtensions
import com.simurgh.prayertimes.home.quran.QuranTitle
import com.simurgh.prayertimes.room.AppDatabase
import com.simurgh.prayertimes.room.dao.VerseDao
import io.reactivex.Observable
import io.reactivex.Scheduler
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
    
    var verses: MutableList<Verse> = mutableListOf()
    var surahAdapter = SurahAdapter()

    var titleNo: Int = 0
    var name: String? = null
    var transcribed: String? = null

    val compositeDisposable = CompositeDisposable()
    lateinit var appDatabase: AppDatabase
    lateinit var verseDao: VerseDao


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

        title_name.text = name
        title_transcribed.text = transcribed

        appDatabase = AppDatabase.getInstance(applicationContext)!!
        verseDao = appDatabase.verseDao()

        compositeDisposable.add(Observable.fromCallable {
            verses = verseDao.getVerses(titleNo)
            if (verses.size == 0){
                val str_url = "http://api.alquran.cloud/surah/$titleNo/editions/quran-simple,tg.ayati,en.transliteration"
                try {
                    val url = URL(str_url)
                    val httpURLConnection = url.openConnection() as HttpURLConnection
                    val inputStream = BufferedInputStream(httpURLConnection.inputStream)
                    val result = MyExtensions.readStream(inputStream)

                    val jsonObject = JSONObject(result)
                    val data = jsonObject.getJSONArray("data")
                    val numberOfAyahs = data.getJSONObject(0).getInt("numberOfAyahs")
                    for (i in 0 until numberOfAyahs) {
                        val verseNo = data.getJSONObject(0).getJSONArray("ayahs").getJSONObject(i).getInt("numberInSurah")
                        val tajik = data.getJSONObject(1).getJSONArray("ayahs").getJSONObject(i).getString("text")
                        var arabic = data.getJSONObject(0).getJSONArray("ayahs").getJSONObject(i).getString("text")
                        if (titleNo != 0 && i == 0 && arabic.contains(BISMILLAH)){ // that's for bismillah verse, it's strange
                            arabic = arabic.replace(BISMILLAH, "")
                        }
                        verses.add(Verse(titleNo, verseNo, arabic, tajik))
                    }
                    verseDao.insert(verses)

                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{surahAdapter.notifyDataSetChanged()})


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
            var verse = verses[position]
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

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}