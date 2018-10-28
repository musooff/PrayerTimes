package com.simurgh.prayertimes.home.quran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simurgh.prayertimes.R
import com.simurgh.prayertimes.extensions.MyExtensions
import com.simurgh.prayertimes.room.AppDatabase
import com.simurgh.prayertimes.room.dao.QuranTitleDao
import com.simurgh.prayertimes.surah.SurahActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_quran.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class QuranFragment: Fragment() {

    var quranTitles: MutableList<QuranTitle> = mutableListOf()
    lateinit var appDatabase: AppDatabase
    lateinit var quranTitleDao: QuranTitleDao
    val mAdapter = QuranTitleAdapter()
    val compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.content_quran, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_surahs.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        rv_surahs.setHasFixedSize(true)
        rv_surahs.adapter = mAdapter

        appDatabase = AppDatabase.getInstance(context!!)!!
        quranTitleDao = appDatabase.quranTitleDao()

        compositeDisposable.add(Observable.fromCallable {
            quranTitles = quranTitleDao.getAll()
            if (quranTitles.size == 0){
                try {
                    val inputStream = activity!!.resources.assets.open("surahs.json")
                    val jsonObject = JSONObject(MyExtensions.readStream(inputStream))
                    val dataSurah = jsonObject.getJSONArray("quranTitles")

                    for (i in 0 until dataSurah.length()) {

                        val titleNo = dataSurah.getJSONObject(i).getInt("titleNo")
                        val name = dataSurah.getJSONObject(i).getString("name")
                        val transcribed = dataSurah.getJSONObject(i).getString("transcribed")
                        val translated = dataSurah.getJSONObject(i).getString("translated")

                        quranTitles.add(QuranTitle(titleNo, name, transcribed, translated))
                    }

                    quranTitleDao.insert(quranTitles)

                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {mAdapter.notifyDataSetChanged()})
    }

    inner class QuranTitleAdapter: RecyclerView.Adapter<QuranTitleViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuranTitleViewHolder {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.quran_title_item, parent, false)
            return QuranTitleViewHolder(view)
        }

        override fun getItemCount(): Int {
            return quranTitles.size
        }

        override fun onBindViewHolder(holder: QuranTitleViewHolder, position: Int) {
            val item = quranTitles[position]
            holder.name.text = item.name
            holder.transcribed.text = item.englishName
            holder.translated.text = item.englishNameTranslation
            holder.titleNo.text = item.number.toString()

            holder.itemView.setOnClickListener {
                val intent = SurahActivity.newIntent(context!!, item)
                startActivity(intent)
            }

        }

    }

    class QuranTitleViewHolder(view: View): RecyclerView.ViewHolder(view){
        var name = view.findViewById<TextView>(R.id.tv_arabic)
        var transcribed = view.findViewById<TextView>(R.id.tv_eng)
        var translated = view.findViewById<TextView>(R.id.tv_tran)
        var titleNo = view.findViewById<TextView>(R.id.tv_id)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}