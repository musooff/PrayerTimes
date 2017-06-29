package com.simurgh.prayertimes;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SplashActivity extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*Get Shared Preference*/
        sharedPreferences = getSharedPreferences("PrayerData", 0);
        editor = sharedPreferences.edit();

        editor.apply();


        /*Get todays prayer timings*/
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());


        if (sharedPreferences.getBoolean("firstRun",true)) {
            Log.v("loc", "asking location");

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                String longitude = "Longitude: " + location.getLongitude();
                                Log.v("long", longitude);
                                String latitude = "Latitude: " + location.getLatitude();
                                Log.v("lat", latitude);

                            /*------- To get city name from coordinates -------- */
                                String cityName = null;
                                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                                List<Address> addresses;
                                try {
                                    addresses = gcd.getFromLocation(location.getLatitude(),
                                            location.getLongitude(), 1);
                                    if (addresses.size() > 0) {
                                        System.out.println(addresses.get(0).getLocality());
                                        cityName = addresses.get(0).getLocality();
                                    }
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }

                                editor.putFloat("locLat", (float) location.getLatitude());
                                editor.putFloat("locLong", (float) location.getLongitude());
                                editor.putString("address",cityName);
                                editor.apply();

                                Log.e("city",cityName);

                                new GetPrayerTimesToday().execute(timestamp.getTime()/1000);
                                new GetPrayerTimesMonth().execute(new int[]{6,2017});
                            }
                        }
                    });



            setDefaultPreferences();

            editor.putBoolean("firstRun", false);
            editor.apply();
        }
        else {
            Intent main = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(main);
            finish();
        }

        // download or skip prayer times
        /*
        if (sharedPreferences.getBoolean("data",false)){
            Intent main = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(main);
            finish();
        }
        else {
            new GetPrayerTimesToday().execute(timestamp.getTime()/1000);
            new GetPrayerTimesMonth().execute(new int[]{6,2017});
        }

        */
    }

    private void setDefaultPreferences() {
        editor.putInt("fajrNot",0);
        editor.putInt("sunriseNot",3);
        editor.putInt("dhuhrNot",0);
        editor.putInt("asrNot",0);
        editor.putInt("maghribNot",0);
        editor.putInt("ishaNot",0);
        editor.apply();
    }


    private void setAlarm() {


        SimpleDateFormat apiDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm",Locale.US);
        SimpleDateFormat testDateFormat = new SimpleDateFormat("dd MM yyyy HH:mm:ss",Locale.US);

        String[] names = new String[]{"Бомдод","Офтоббарои","Пешин","Аср","Шом","Хуфтан"};

        try {

            int[] notSettings = new int[]{sharedPreferences.getInt("fajrNot",0),
                    sharedPreferences.getInt("sunriseNot",3),
                    sharedPreferences.getInt("dhuhrNot",0),
                    sharedPreferences.getInt("asrNot",0),
                    sharedPreferences.getInt("maghribNot",0),
                    sharedPreferences.getInt("ishaNot",0)};

            JSONObject today = new JSONObject(sharedPreferences.getString("todayJson","{None}"));
            JSONObject timings = today.getJSONObject("timings");
            JSONObject date = today.getJSONObject("date");
/*
            String[] times = new String[]{timings.getString("Fajr").substring(0,timings.getString("Fajr").indexOf(" ")),
                    timings.getString("Sunrise").substring(0,timings.getString("Sunrise").indexOf(" ")),
                    timings.getString("Dhuhr").substring(0,timings.getString("Dhuhr").indexOf(" ")),
                    timings.getString("Asr").substring(0,timings.getString("Asr").indexOf(" ")),
                    timings.getString("Maghrib").substring(0,timings.getString("Maghrib").indexOf(" ")),
                    timings.getString("Isha").substring(0,timings.getString("Isha").indexOf(" "))};
*/
            String[] times = new String[]{timings.getString("Fajr"),
                    timings.getString("Sunrise"),
                    timings.getString("Dhuhr"),
                    timings.getString("Asr"),
                    timings.getString("Maghrib"),
                    timings.getString("Isha")};

            String readable = date.getString("readable");

            String[] alarmTimes = new String[6];
            for (int i = 0; i < 6; i++){
                alarmTimes[i] = readable+" "+times[i];
                Date alarm = apiDateFormat.parse(alarmTimes[i]);
                Log.e("Alarm",testDateFormat.format(alarm));

                Intent notificationService = new Intent(getApplicationContext(),NotificationService.class);
                notificationService.putExtra(NotificationService.NOTIFICATION_SOUND,notSettings[i]);
                notificationService.putExtra(NotificationService.NOTIFICATION_NAME,"Намози "+names[i]);
                //This is alarm manager
                PendingIntent pi = PendingIntent.getBroadcast(this, 0 , notificationService, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                am.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getTime(),
                        AlarmManager.INTERVAL_DAY, pi);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // set Test Alarm
        Intent notificationService = new Intent(getApplicationContext(),NotificationService.class);
        notificationService.putExtra(NotificationService.NOTIFICATION_SOUND,1);
        notificationService.putExtra(NotificationService.NOTIFICATION_NAME,"Намози "+"Test");


        /*
        try {
            Date testDate = apiDateFormat.parse("29 Jun 2017 03:07");
            //This is alarm manager
            PendingIntent pi = PendingIntent.getBroadcast(this, 0 , notificationService, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC_WAKEUP, testDate.getTime(),
                    AlarmManager.INTERVAL_DAY, pi);
            Date today = new Date();
            Log.e("alarm",apiDateFormat.format(testDate));
            Log.e("now",apiDateFormat.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }
*/
    }


    public String readStream(InputStream is) {
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



    public class GetPrayerTimesMonth extends AsyncTask<int[],Void,String> {

        int cal[];
        @Override
        protected String doInBackground(int[]... params) {
            cal = params[0];

            String str_url = "http://api.aladhan.com/calendar?latitude="+sharedPreferences.getFloat("locLat", Float.parseFloat("0.000000"))+"&longitude="+sharedPreferences.getFloat("locLong", Float.parseFloat("0.000000"))+"&timezonestring=Asia/Seoul&method=3&school=1&month="+cal[0]+"&year="+cal[1];
            Log.e("str_url",str_url);
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

            Log.e("sharedDate","cal"+cal[0]+""+cal[1]);
            editor.putString("cal0"+cal[0]+""+cal[1],s);
            editor.putBoolean("data",true);
            editor.apply();

            Intent main = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(main);
            finish();
        }
    }


    public class GetPrayerTimesToday extends AsyncTask<Long,Void,String>{
        @Override
        protected String doInBackground(Long... params) {
            String str_url = "http://api.aladhan.com/timings/"+params[0]+"?latitude="+sharedPreferences.getFloat("locLat", (float) 0.000000)+"&longitude="+sharedPreferences.getFloat("locLong", Float.parseFloat("0.000000"))+"&timezonestring=Asia/Seoul&method=3&school=1";
            Log.e("str_url",str_url);
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
                JSONObject date = data.getJSONObject("date");

                editor.putString("todayJson",data.toString());

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

                editor.putString("dateReadable",date.getString("readable"));
                editor.putString("dateTimestamp",date.getString("timestamp"));

                editor.apply();


                /* here I will set notify service*/
                setAlarm();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}