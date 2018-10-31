package com.simurgh.prayertimes.home

import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.cleveroad.slidingtutorial.*
import com.simurgh.prayertimes.*
import com.simurgh.prayertimes.R
import com.simurgh.prayertimes.home.mosque.MosqueFragment
import com.simurgh.prayertimes.home.quran.QuranFragment
import com.simurgh.prayertimes.home.times.TimesFragment
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.footer.*
import com.simurgh.prayertimes.home.home.HomeFragment
import com.simurgh.prayertimes.home.more.MoreFragment
import com.simurgh.prayertimes.model.AppPreference

open class HomeBaseActivity: FragmentActivity() {


    companion object {
        const val NUM_PAGES = 5
        const val locationRequestCode = 1000
    }

    private lateinit var currentItem: View

    private var doubleBackToExitPressedOnce = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        currentItem = ll_today
        currentItem.isActivated = true

        pager.adapter = HomePagerAdapter(supportFragmentManager)
        pager.setPagingEnabled(false)
        linkFooterToPager()

        if (getAppPref().isFirstRun()) runIntroSlides()

    }

    private fun getAppPref(): AppPreference {
        return AppPreference(applicationContext)
    }

    private fun runIntroSlides() {
        getAppPref().setFirstRun(false)
        val indicatorOptions = IndicatorOptions.newBuilder(applicationContext)
                .setElementColorRes(R.color.white)
                .setSelectedElementColorRes(R.color.grey)
                .setRenderer { canvas, elementBounds, paint, _ ->
                    var radius = Math.min(elementBounds.width(), elementBounds.height())
                    radius /= 2f
                    canvas.drawCircle(elementBounds.centerX(), elementBounds.centerY(), radius, paint)
                }
                .build()

        val tutorialPageOptionsProvider = TutorialPageOptionsProvider { position ->
            @LayoutRes val pageLayoutResId: Int
            val tutorialItems: Array<TransformItem>
            when (position) {
                0 -> {
                    pageLayoutResId = R.layout.intro_first
                    tutorialItems = arrayOf(TransformItem.create(R.id.iv_main, Direction.LEFT_TO_RIGHT, 0.1f), TransformItem.create(R.id.iv_first, Direction.RIGHT_TO_LEFT, 0.2f), TransformItem.create(R.id.iv_second, Direction.RIGHT_TO_LEFT, 0.3f), TransformItem.create(R.id.iv_fourth, Direction.RIGHT_TO_LEFT, 0.5f), TransformItem.create(R.id.iv_fifth, Direction.RIGHT_TO_LEFT, 0.6f), TransformItem.create(R.id.iv_sixth, Direction.RIGHT_TO_LEFT, 0.7f), TransformItem.create(R.id.iv_seventh, Direction.RIGHT_TO_LEFT, 0.8f), TransformItem.create(R.id.iv_eigth, Direction.RIGHT_TO_LEFT, 0.9f))
                }
                1 -> {
                    pageLayoutResId = R.layout.intro_second
                    tutorialItems = arrayOf(TransformItem.create(R.id.iv_main, Direction.LEFT_TO_RIGHT, 0.2f), TransformItem.create(R.id.iv_second, Direction.RIGHT_TO_LEFT, 0.05f), TransformItem.create(R.id.iv_third, Direction.RIGHT_TO_LEFT, 0.07f))
                }
                2 -> {
                    pageLayoutResId = R.layout.intro_third
                    tutorialItems = arrayOf(TransformItem.create(R.id.iv_main, Direction.LEFT_TO_RIGHT, 0.2f), TransformItem.create(R.id.iv_second, Direction.RIGHT_TO_LEFT, 0.05f), TransformItem.create(R.id.iv_third, Direction.RIGHT_TO_LEFT, 0.07f))
                }
                3 -> {
                    pageLayoutResId = R.layout.intro_fourth
                    tutorialItems = arrayOf(TransformItem.create(R.id.iv_main, Direction.LEFT_TO_RIGHT, 0.2f), TransformItem.create(R.id.iv_second, Direction.RIGHT_TO_LEFT, 0.05f), TransformItem.create(R.id.iv_third, Direction.RIGHT_TO_LEFT, 0.07f))
                }
                4 -> {
                    pageLayoutResId = R.layout.intro_fifth
                    tutorialItems = arrayOf(TransformItem.create(R.id.iv_main, Direction.LEFT_TO_RIGHT, 0.2f), TransformItem.create(R.id.iv_second, Direction.RIGHT_TO_LEFT, 0.05f), TransformItem.create(R.id.iv_third, Direction.RIGHT_TO_LEFT, 0.07f))
                }
                5 -> {
                    pageLayoutResId = R.layout.intro_sixth
                    tutorialItems = arrayOf(TransformItem.create(R.id.iv_main, Direction.LEFT_TO_RIGHT, 0.2f), TransformItem.create(R.id.iv_second, Direction.RIGHT_TO_LEFT, 0.05f), TransformItem.create(R.id.iv_third, Direction.RIGHT_TO_LEFT, 0.07f))
                }
                6 -> {
                    pageLayoutResId = R.layout.intro_seventh
                    tutorialItems = arrayOf(TransformItem.create(R.id.iv_main, Direction.LEFT_TO_RIGHT, 0.2f))
                }
                else -> {
                    throw IllegalArgumentException("Unknown position: $position")
                }
            }

            PageOptions.create(pageLayoutResId, position, *tutorialItems)
        }

        val tutorialOptions = TutorialFragment.newTutorialOptionsBuilder(applicationContext)
                .setUseAutoRemoveTutorialFragment(false)
                .setUseInfiniteScroll(false)
                .setTutorialPageProvider(tutorialPageOptionsProvider)
                .setIndicatorOptions(indicatorOptions)
                .setUseAutoRemoveTutorialFragment(true)
                .setPagesCount(7)
                .build()

        val tutorialFragment = TutorialFragment.newInstance(tutorialOptions)
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, tutorialFragment)
                .commit()
    }

    override fun onBackPressed() {
        if (pager.currentItem == 0) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Барои хуруч боз як бори дигар пахш кунед", Toast.LENGTH_SHORT).show()

            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)

        } else {
            pager.currentItem = 0
            updateFooterButton(ll_today)
        }
    }

    private inner class HomePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = NUM_PAGES

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> HomeFragment()
                1 -> TimesFragment()
                2 -> QuranFragment()
                3 -> MosqueFragment()
                4 -> MoreFragment()
                else -> {HomeFragment()
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