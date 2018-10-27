package com.simurgh.prayertimes.home

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.simurgh.prayertimes.*
import com.simurgh.prayertimes.home.mosque.MosqueFragment
import com.simurgh.prayertimes.home.quran.QuranFragment
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.footer.*



open class HomeBaseActivity: FragmentActivity() {

    companion object {
        const val NUM_PAGES = 5
    }

    private lateinit var currentItem: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        currentItem = ll_today
        currentItem.isActivated = true

        val pagerAdapter = HomePagerAdapter(supportFragmentManager)
        pager.adapter = pagerAdapter
        pager.setPagingEnabled(false)
        linkFooterToPager()
    }

    override fun onBackPressed() {
        if (pager.currentItem == 0) {
            super.onBackPressed()
        } else {
            pager.currentItem = 0
            updateFooterButton(ll_today)
        }
    }

    private inner class HomePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = NUM_PAGES

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> TodayFragment()
                1 -> PrayersFragment()
                2 -> QuranFragment()
                3 -> MosqueFragment()
                4 -> MoreFragment()
                else -> {TodayFragment()
                }
            }
        }

    }

    private fun linkFooterToPager(){
        ll_today.setOnClickListener {
            pager.currentItem = 0
            updateFooterButton(ll_today)

        }
        ll_times.setOnClickListener {
            pager.currentItem = 1
            updateFooterButton(ll_times)

        }
        ll_quran.setOnClickListener {
            pager.currentItem = 2
            updateFooterButton(ll_quran)
        }
        ll_mosque.setOnClickListener {
            pager.currentItem = 3
            updateFooterButton(ll_mosque)
        }
        ll_more.setOnClickListener {
            pager.currentItem = 4
            updateFooterButton(ll_more)
        }
    }

    private fun updateFooterButton(view: LinearLayout){
        currentItem.isActivated = false
        currentItem = view
        currentItem.isActivated = true
    }

}