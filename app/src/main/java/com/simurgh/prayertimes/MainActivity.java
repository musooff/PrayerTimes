package com.simurgh.prayertimes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.cleveroad.loopbar.widget.LoopBarView;
import com.cleveroad.loopbar.widget.OnItemClickListener;
import com.google.android.material.navigation.NavigationView;
import com.simurgh.prayertimes.home.mosque.MosqueFragment;
import com.simurgh.prayertimes.home.quran.QuranFragment;
import com.simurgh.prayertimes.home.times.TimesFragment;
import com.simurgh.prayertimes.model.CustomViewPager;
import com.simurgh.prayertimes.home.more.DisclaimerActivity;
import com.simurgh.prayertimes.home.more.MoreFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    TextView tv_prayer_time, tv_prayer_name, tv_prayer_remaining, tv_location,
            tv_verse_name, tv_verse, tv_read_verse,
            tv_hadis_name, tv_hadis, tv_read_hadis,
            tv_dua_name, tv_dua_arabic, tv_dua_arabic_transcribed, tv_dua_tajik, tv_read_dua,
            tv_name_arabic, tv_name_arabic_transcribed, tv_name_tajik,tv_read_name,
            tv_share;

    LoopBarView loopBarView;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private CustomViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        mViewPager = (CustomViewPager) findViewById(R.id.pager);
        mViewPager.setPagingEnabled(false);
        mViewPager.setCurrentItem(0);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);


        sharedPreferences = getSharedPreferences("PrayerData",0);
        editor = sharedPreferences.edit();
        editor.apply();

        loopBarView = (LoopBarView)findViewById(R.id.endlessView);
        loopBarView.setCategoriesAdapterFromMenu(R.menu.activity_main_drawer);
        loopBarView.addOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                switch (position){
                    case 0:
                        mViewPager.setCurrentItem(0);
                        getSupportActionBar().setTitle("Вақтҳои Намоз");
                        break;
                    case 1:
                        mViewPager.setCurrentItem(1);
                        getSupportActionBar().setTitle("Вақтҳои Намоз");
                        break;
                    case 2:
                        mViewPager.setCurrentItem(2);
                        getSupportActionBar().setTitle("Қуръони Карим");
                        break;
                    case 3:
                        mViewPager.setCurrentItem(3);
                        getSupportActionBar().setTitle("Масҷидҳои ҷомеъ");
                        break;
                    case 4:
                        mViewPager.setCurrentItem(4);
                        getSupportActionBar().setTitle("Китобхона");
                        break;
                    case 5:
                        mViewPager.setCurrentItem(5);
                        getSupportActionBar().setTitle("Ғайра");
                        break;


                    default:

                }
            }
        });


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //editor.clear();
            //editor.apply();
            //return true;

            Intent disclaimer = new Intent(MainActivity.this,DisclaimerActivity.class);
            startActivity(disclaimer);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_today) {
            mViewPager.setCurrentItem(0);

            getSupportActionBar().setTitle("Вактхои Намоз");
        } else if (id == R.id.nav_prayers) {
            mViewPager.setCurrentItem(1);
            getSupportActionBar().setTitle("Вактхои Намоз");

        } else if (id == R.id.nav_quran) {
            mViewPager.setCurrentItem(2);
            getSupportActionBar().setTitle("Куръони Карим");
        } else if (id == R.id.nav_qibla) {
            mViewPager.setCurrentItem(3);
            getSupportActionBar().setTitle("Масчидхои Чомеъ");
        }
        /*
        else if (id == R.id.nav_more) {
            mViewPager.setCurrentItem(5);
            getSupportActionBar().setTitle("Гайра");

        }
        */
        else if (id == R.id.nav_lib){
            mViewPager.setCurrentItem(4);
            getSupportActionBar().setTitle("Китобхона");

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void countDown(){

    }

    // settime each time with timer
    private void setFajr(long curTime,long time, String fajr){
        final Date[] timeleft = new Date[1];
        tv_prayer_time.setText(fajr);
        tv_prayer_name.setText("Бомдод");
        // start count timer
        CountDownTimer countDownTimer  = new CountDownTimer(time-curTime,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                SimpleDateFormat countFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
                timeleft[0] = new Date(millisUntilFinished);
                tv_prayer_remaining.setText(countFormat.format(timeleft[0]));
            }

            @Override
            public void onFinish() {
            }
        };
        countDownTimer.start();
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new TodayFragment();

                case 1:
                    return new TimesFragment();

                case 2:
                    return new QuranFragment();

                case 3:
                    return new MosqueFragment();

                case 4:
                    return new MoreFragment();

                case 5:
                    return new MoreFragment();

                default:
                    break;
            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages. Offline and Online
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Today";
                case 1:
                    return "Prayers";
                case 2:
                    return "Quran";
                case 3:
                    return "Qibla";
                case 4:
                    return "Library";
                case 5:
                    return "More";
            }
            return null;
        }
    }

    public int getMinutes(String time){
        String[] units = time.split(":"); //will break the string up into an array
        int hours = Integer.parseInt(units[0]); //first element
        int minutes = Integer.parseInt(units[1]); //second element
        return 60 * hours + minutes;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 11235: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(getApplicationContext(),"Анакнун, шумо метавонед китобхоро боргири ва мутолиа кунед.",Toast.LENGTH_SHORT).show();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(),"Лутфан барои боргирии китобхо ба мо ичоза дихед.",Toast.LENGTH_SHORT).show();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
