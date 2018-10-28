package com.simurgh.prayertimes.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.simurgh.prayertimes.*
import com.simurgh.prayertimes.home.mosque.MosqueFragment
import com.simurgh.prayertimes.home.quran.QuranFragment
import com.simurgh.prayertimes.home.times.TimesFragment
import com.simurgh.prayertimes.model.AppPreference
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.footer.*
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.simurgh.prayertimes.home.home.HomeFragment
import com.simurgh.prayertimes.home.more.MoreFragment
import java.io.IOException
import java.util.*


open class HomeBaseActivity: FragmentActivity() {


    companion object {
        const val NUM_PAGES = 5
        const val locationRequestCode = 1000
    }

    private lateinit var currentItem: View
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        currentItem = ll_today
        currentItem.isActivated = true

        val pagerAdapter = HomePagerAdapter(supportFragmentManager)
        pager.adapter = pagerAdapter
        pager.setPagingEnabled(false)
        linkFooterToPager()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCheck()
    }

    private fun locationCheck(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), locationRequestCode)
        }
    }

    private fun updateLocationData(location: Location){
        val addresses: List<Address>
        val geoCoder = Geocoder(applicationContext, Locale.getDefault())
        try {
            addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
            val address = addresses[0].getAddressLine(0)
            Log.e("MY_TAG", address)
            AppPreference(applicationContext).setLatLon(location.latitude, location.longitude)
            AppPreference(applicationContext).setAddress(address)

        } catch (e: IOException) {
            e.printStackTrace()
        }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,grantResults: IntArray) {
        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return
                    }
                    mFusedLocationClient!!.lastLocation.addOnSuccessListener {
                        if (it != null){
                            updateLocationData(it)
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "Ичоза ба истифида барии макон гирифта нашуд. Вактхои Душанберо бор мекунем!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}