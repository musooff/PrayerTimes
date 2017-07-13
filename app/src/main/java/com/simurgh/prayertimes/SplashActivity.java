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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class SplashActivity extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private FusedLocationProviderClient mFusedLocationClient;

    TextView tv_download;
    private JSONObject alarmFromJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        /*Get Shared Preference*/
        sharedPreferences = getSharedPreferences("PrayerData", 0);
        editor = sharedPreferences.edit();

        editor.apply();


        tv_download = (TextView)findViewById(R.id.tv_download);


        /*Get todays date and download times for the month*/
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Date today = new Date(timestamp.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMyyyy",Locale.US);
        final String strDate = dateFormat.format(today);
        Log.e("date",strDate);






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
                                Log.e("long", longitude);
                                String latitude = "Latitude: " + location.getLatitude();
                                Log.e("lat", latitude);

                                Geocoder geocoder;
                                List<Address> addresses;
                                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(),
                                            location.getLongitude(),
                                            1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    String city = addresses.get(0).getLocality();
                                    String state = addresses.get(0).getAdminArea();
                                    String country = addresses.get(0).getCountryName();
                                    String postalCode = addresses.get(0).getPostalCode();
                                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                                    Log.e("address",address);

                                    editor.putFloat("locLat", (float) location.getLatitude());
                                    editor.putFloat("locLong", (float) location.getLongitude());
                                    editor.putString("address",address);
                                    editor.apply();

                                    //Log.e("city",cityName);

                                    //new GetPrayerTimesToday().execute(timestamp.getTime()/1000);
                                    tv_download.setText("Боргирии вактхо.\nЛутфан мунтазир шавед. Ин факат 1 бор мебошад.");
                                    new GetPrayerTimesMonth().execute(new String[]{strDate.substring(0,2),strDate.substring(2)});


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }


                        }
                    });



            setDefaultPreferences();
            setBookDownloads();



            editor.putBoolean("firstRun", false);
            editor.apply();
        }
        else if (sharedPreferences.getString(strDate,"none").equals("none")){
            //download this months prayer times;

            Log.e("New Month","downloading new Month data");
            new GetPrayerTimesMonth().execute(new String[]{strDate.substring(0,2),strDate.substring(2)});
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



    private void downloadVerse() {

        tv_download.setText("Боргирии Оятхо.\nЛутфан мунтазир шавед. Ин факат 1 бор мебошад.");

        final SimpleDateFormat dateFormat = new SimpleDateFormat("MMyyyy",Locale.US);
        final Date today = new Date();
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mVerse = mRootRef.child("VerseOfMonth");
        DatabaseReference mMonth = mVerse.child(dateFormat.format(today));
        mMonth.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                JSONObject ayahs = new JSONObject();
                JSONArray data = new JSONArray();
                try {
                    ayahs.put("data",data);
                    for (DataSnapshot day: dataSnapshot.getChildren()){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("surahNameArabic",day.child("surahNameArabic").getValue(String.class));
                        jsonObject.put("surahNameTajik",day.child("surahNameTajik").getValue(String.class));
                        jsonObject.put("surahNameTajikTranscribed",day.child("surahNameTajikTranscribed").getValue(String.class));
                        jsonObject.put("surahNumber",day.child("surahNumber").getValue(Integer.class));
                        jsonObject.put("verseArabic",day.child("verseArabic").getValue(String.class));
                        jsonObject.put("verseNumber",day.child("verseNumber").getValue(Integer.class));
                        jsonObject.put("versePlace",day.child("versePlace").getValue(String.class));
                        jsonObject.put("verseTajik",day.child("verseTajik").getValue(String.class));
                        jsonObject.put("verseTajikTranscribed",day.child("verseTajikTranscribed").getValue(String.class));
                        Log.e("day",jsonObject.toString());
                        data.put(jsonObject);
                    }

                    editor.putString("ayahs"+dateFormat.format(today),ayahs.toString());
                    editor.apply();


                    // start main activity
                    Intent main = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(main);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void downloadAndUploadVerse() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMyyyy",Locale.US);
        Date today = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        int numDates =cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        Log.e("numDates",numDates+"");
        Random randomAyah = new Random();

        for (int i = 0; i< numDates; i++){

            int ranAyah = randomAyah.nextInt(6236)+1;
            String str_url = "http://api.alquran.cloud/ayah/"+ranAyah+"/editions/quran-simple,tg.ayati";
            if (i == numDates-1){
                new DownloadAyah().execute(new String[]{str_url, String.valueOf(i+1), dateFormat.format(today),"1"});// last download
            }
            else {
                new DownloadAyah().execute(new String[]{str_url, String.valueOf(i+1), dateFormat.format(today),"0"});

            }
        }
    }

    private void setBookDownloads() {
        editor.putBoolean("book1_zindaginoma.pdf",false);
        editor.putBoolean("book2_vasiyatho.pdf",false);
        editor.putBoolean("book3_musnad.pdf",false);
        editor.putBoolean("book4_duo.pdf",false);
        editor.putBoolean("book5_savol.pdf",false);
        editor.putBoolean("book6_chihil.pdf",false);
        editor.apply();
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
                //notificationService.putExtra(NotificationService.NOTIFICATION_SOUND,notSettings[i]);
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
        //notificationService.putExtra(NotificationService.NOTIFICATION_SOUND,1);
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



    private void setAlarmForMonth() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sharedCalFormat = new SimpleDateFormat("MMYYYY", Locale.US);


        Date curDate = new Date(timestamp.getTime());


        Log.e("cur", sharedCalFormat.format(curDate));
        // set todays prayer times by iterating calendar;

        JSONObject calendar = null;
        try {
            calendar = new JSONObject(sharedPreferences.getString(sharedCalFormat.format(curDate), "none"));
            JSONArray data = calendar.getJSONArray("data");
            SimpleDateFormat apiDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
            for (int j = 0; j < data.length(); j++) {
                //Log.e("showndate",apiDateFormat.format(shownDate));
                //Log.e("dateReadable",data.getJSONObject(j).getJSONObject("date").getString("readable"));
                setAlarmFromJson(data.getJSONObject(j));
                Log.d("Alarm","Setting alarm for the day "+data.getJSONObject(j).getJSONObject("date").getString("readable"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Test alarms

        /*

        Log.e("AlarmSet","This is test alarm");
        SimpleDateFormat apiDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm",Locale.US);
        Date alarm = null;
        try {
            alarm = apiDateFormat.parse("10 Jul 2017 19:58");
            Log.e("set at",alarm.toString());
            Intent notificationService = new Intent(getApplicationContext(),NotificationService.class);
            notificationService.putExtra(NotificationService.NOTIFICATION_ID,2);
            notificationService.putExtra(NotificationService.NOTIFICATION_NAME,"Notification Test");
            //This is alarm manager
            PendingIntent pi = PendingIntent.getBroadcast(this, 0 , notificationService, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getTime(),
                    AlarmManager.INTERVAL_DAY, pi);
        } catch (ParseException e) {
            e.printStackTrace();
        }
*/


    }

    public void setAlarmFromJson(JSONObject alarmFromJson) {

        SimpleDateFormat apiDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm",Locale.US);
        SimpleDateFormat testDateFormat = new SimpleDateFormat("dd MM yyyy HH:mm:ss",Locale.US);

        String[] names = new String[]{"Бомдод","Офтоббарои","Пешин","Аср","Шом","Хуфтан"};

        try {

            //JSONObject today = new JSONObject(sharedPreferences.getString("todayJson","{None}"));
            JSONObject timings = alarmFromJson.getJSONObject("timings");
            JSONObject date = alarmFromJson.getJSONObject("date");
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

            for (int k = 0; k< times.length; k++){
                Log.e("times",times[k]);
                times[k] = times[k].substring(0, times[k].indexOf(" "));

                Log.e("times",times[k]);
            }

            String readable = date.getString("readable");

            String[] alarmTimes = new String[6];
            for (int i = 0; i < 6; i++){
                alarmTimes[i] = readable+" "+times[i];
                Log.e("alarm Time",alarmTimes[i]);


                Date alarm = apiDateFormat.parse(alarmTimes[i]);


                Log.e("AlarmSet",apiDateFormat.format(alarm));


                Intent notificationService = new Intent(getApplicationContext(),NotificationService.class);
                notificationService.putExtra(NotificationService.NOTIFICATION_ID,i);
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

    }


    public class GetPrayerTimesMonth extends AsyncTask<String[],Void,String> {

        String cal[];
        @Override
        protected String doInBackground(String[]... params) {
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

            Log.e("sharedDate",cal[0]+""+cal[1]);
            editor.putString(cal[0]+""+cal[1],s);
            editor.putBoolean("data",true);
            editor.apply();

            //download verses
            downloadVerse();

            //set alarm for all times of month
            //setAlarmForMonth();



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
                //setAlarm();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public class DownloadAyah extends AsyncTask<String[],Void,String> {
        String day;
        String month;
        @Override
        protected String doInBackground(String[]... params) {

            day = params[0][1];
            month = params[0][2];

            try {
                URL url = new URL(params[0][0]);
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
                JSONArray data = jsonObject.getJSONArray("data");

                JSONObject tajik = data.getJSONObject(1);
                JSONObject arabic = data.getJSONObject(0);

                String textTajik = tajik.getString("text");
                String textArabic = arabic.getString("text");

                JSONObject surahTajik = tajik.getJSONObject("surah");
                JSONObject surahArabic = arabic.getJSONObject("surah");

                String surahNameTajik = surahTajik.getString("englishName");
                String surahNameArabic = surahTajik.getString("name");
                String revelationType = surahTajik.getString("revelationType");

                int surahNumber = surahTajik.getInt("number");

                int verseNumber = tajik.getInt("numberInSurah");


                DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();


                DatabaseReference mVerse = mRootRef.child("VerseOfMonth");
                DatabaseReference mMonth = mVerse.child(month);
                DatabaseReference mDay = mMonth.child(day);

                mDay.child("verseNumber").setValue(verseNumber);
                mDay.child("surahNumber").setValue(surahNumber);
                mDay.child("verseArabic").setValue(textArabic);
                mDay.child("verseTajik").setValue(textTajik);
                mDay.child("verseTajikTranscribed").setValue("Not available");
                mDay.child("surahNameArabic").setValue(surahNameArabic);
                mDay.child("surahNameTajik").setValue(surahNameTajik);
                mDay.child("surahNameTajikTranscribed").setValue("Not available");
                mDay.child("versePlace").setValue(revelationType);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
