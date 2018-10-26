package com.simurgh.prayertimes;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cleveroad.loopbar.widget.LoopBarView;
import com.cleveroad.loopbar.widget.OnItemClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivityOriginal extends AppCompatActivity
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        tv_prayer_name = (TextView)findViewById(R.id.tv_next_prayer_name);
        tv_prayer_time = (TextView)findViewById(R.id.tv_next_prayer_time);
        tv_prayer_remaining = (TextView)findViewById(R.id.tv_next_prayer_remaining);
        tv_location = (TextView)findViewById(R.id.tv_location);
        tv_verse_name = (TextView)findViewById(R.id.tv_verse_name);
        tv_verse = (TextView)findViewById(R.id.tv_verse);
        tv_read_verse = (TextView)findViewById(R.id.tv_read_verse);
        tv_hadis_name = (TextView)findViewById(R.id.tv_hadis_name);
        tv_hadis = (TextView)findViewById(R.id.tv_hadis);
        tv_read_hadis = (TextView)findViewById(R.id.tv_read_hadis);
        tv_dua_name = (TextView)findViewById(R.id.tv_dua_name);
        tv_dua_arabic = (TextView)findViewById(R.id.tv_dua_arabic);
        tv_dua_arabic_transcribed = (TextView)findViewById(R.id.tv_dua_arabic_transcibed);
        tv_dua_tajik = (TextView)findViewById(R.id.tv_dua_tajik);
        tv_read_dua = (TextView)findViewById(R.id.tv_read_dua);
        tv_name_arabic = (TextView)findViewById(R.id.tv_name_arabic);
        tv_name_arabic_transcribed = (TextView)findViewById(R.id.tv_name_arabic_transcribed);
        tv_name_tajik = (TextView)findViewById(R.id.tv_name_tajik);
        tv_read_name = (TextView)findViewById(R.id.tv_read_name);
        tv_share = (TextView)findViewById(R.id.tv_share);


        /*Get Shared Preference*/
        sharedPreferences = getSharedPreferences("PrayerData",0);
        editor = sharedPreferences.edit();
        editor.apply();

        /*Get todays prayer timings*/
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        new GetPrayerTimes().execute(timestamp.getTime()/1000);

        loopBarView = (LoopBarView)findViewById(R.id.endlessView);
        loopBarView.setCategoriesAdapterFromMenu(R.menu.activity_main_drawer);
        loopBarView.addOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                switch (position){
                    case 0:

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_today) {
            // Handle the camera action
        } else if (id == R.id.nav_prayers) {
            Toast.makeText(getApplicationContext(),"Quran",Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_quran) {
            Toast.makeText(getApplicationContext(),"Quran",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_qibla) {

        }
        /*
        else if (id == R.id.nav_more) {

        }
        */
        else if (id == R.id.nav_lib){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class GetPrayerTimes extends AsyncTask<Long,Void,String>{
        @Override
        protected String doInBackground(Long... params) {
            String str_url = "http://api.aladhan.com/timings/"+params[0]+"?latitude=36.369400&longitude=127.364000&timezonestring=Asia/Seoul&method=3&school=1";

            try {
                URL url = new URL(str_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                return readStream(in);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject= new JSONObject(s);
                JSONObject data = jsonObject.getJSONObject("data");
                JSONObject timings = data.getJSONObject("timings");
                String fajr,sunrise,dhuhr,asr,sunset,maghrib,isha,imsak,midnight;
                fajr = timings.getString("Fajr");
                sunrise = timings.getString("Sunrise");
                dhuhr = timings.getString("Dhuhr");
                asr = timings.getString("Asr");
                sunset = timings.getString("Sunset");
                maghrib = timings.getString("Maghrib");
                isha = timings.getString("Isha");
                imsak = timings.getString("Imsak");
                midnight = timings.getString("Midnight");
                editor.putString("fajr",fajr);
                editor.putString("sunrise",sunrise);
                editor.putString("dhuhr",dhuhr);
                editor.putString("asr",asr);
                editor.putString("sunset",sunset);
                editor.putString("maghrib",maghrib);
                editor.putString("isha",isha);
                editor.putString("imsak",imsak);
                editor.putString("midnight",midnight);
                editor.apply();

                nextPrayerName();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    private void nextPrayerName(){
        String fajr,sunrise,dhuhr,asr,sunset,maghrib,isha,imsak,midnight;
        fajr = sharedPreferences.getString("fajr",null);
        sunrise = sharedPreferences.getString("sunrise",null);
        dhuhr = sharedPreferences.getString("dhuhr",null);
        asr = sharedPreferences.getString("asr",null);
        sunset = sharedPreferences.getString("sunset",null);
        maghrib = sharedPreferences.getString("maghrib",null);
        isha = sharedPreferences.getString("isha",null);
        imsak = sharedPreferences.getString("imsak",null);
        midnight = sharedPreferences.getString("midnight",null);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Date today = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        int curTime = getMinutes(dateFormat.format(today));

            int[] times = new int[]{getMinutes(fajr),getMinutes(sunrise),getMinutes(dhuhr),getMinutes(asr),
                    getMinutes(sunset),getMinutes(maghrib),getMinutes(isha),getMinutes(imsak),getMinutes(midnight)};

            if (curTime <= times[0]){// before fajr
                //setFajr(curTime,times[0],fajr);
                tv_prayer_time.setText(fajr);
                tv_prayer_name.setText("Бомдод");
                // start count timer
            }
            else if ((times[1] >= curTime) && (curTime > times[0])){// before sunrise
                //setFajr(curTime,times[0],fajr);
                tv_prayer_time.setText(sunrise);
                tv_prayer_name.setText("Баромади Офтоб");
                // start count timer
            }
            else if ((times[2] >= curTime) && (curTime > times[1])){// before dhur
                //setFajr(curTime,times[0],fajr);
                tv_prayer_time.setText(dhuhr);
                tv_prayer_name.setText("Пешин");
                // start count timer
            }
            else if ((times[3] >= curTime) && (curTime > times[2])){// before asr
                //setFajr(curTime,times[0],fajr);
                tv_prayer_time.setText(asr);
                tv_prayer_name.setText("Аср");
                // start count timer
            }
            else if ((times[5] >= curTime) && (curTime > times[3])){// before maghrib
                //setFajr(curTime,times[0],fajr);
                tv_prayer_time.setText(maghrib);
                tv_prayer_name.setText("Шом");
                // start count timer
            }
            else if ((times[6] >= curTime) && (curTime > times[5])){// before isha
                //setFajr(curTime,times[0],fajr);
                tv_prayer_time.setText(isha);
                tv_prayer_name.setText("Хуфтан");
                // start count timer
            }
            else {
                tv_prayer_name.setText("Бомдоди пагох");
                //
            }


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

    private int getMinutes(String time){
        String[] units = time.split(":"); //will break the string up into an array
        int hours = Integer.parseInt(units[0]); //first element
        int minutes = Integer.parseInt(units[1]); //second element
        int duration = 60 * hours + minutes; //add up our values
        return duration;
    }

    private String readStream(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();

        String line;

        try{
            while ((line = reader.readLine())!=null){
                stringBuilder.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }
}
