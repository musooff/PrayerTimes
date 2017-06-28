package com.simurgh.prayertimes;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cleveroad.loopbar.widget.LoopBarView;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by moshe on 26/06/2017.
 */

public class TodayFragment extends Fragment {



    TextView tv_prayer_time, tv_prayer_name, tv_prayer_remaining, tv_location,
            tv_verse_name, tv_verse, tv_read_verse,
            tv_hadis_name, tv_hadis, tv_read_hadis,
            tv_dua_name, tv_dua_arabic, tv_dua_arabic_transcribed, tv_dua_tajik, tv_read_dua,
            tv_name_arabic, tv_name_arabic_transcribed, tv_name_tajik,tv_read_name,
            tv_share;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ImageView iv_notification;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_today, container, false);

        tv_prayer_name = (TextView)view.findViewById(R.id.tv_next_prayer_name);
        tv_prayer_time = (TextView)view.findViewById(R.id.tv_next_prayer_time);
        tv_prayer_remaining = (TextView)view.findViewById(R.id.tv_next_prayer_remaining);
        tv_location = (TextView)view.findViewById(R.id.tv_location);
        tv_verse_name = (TextView)view.findViewById(R.id.tv_verse_name);
        tv_verse = (TextView)view.findViewById(R.id.tv_verse);
        tv_read_verse = (TextView)view.findViewById(R.id.tv_read_verse);
        tv_hadis_name = (TextView)view.findViewById(R.id.tv_hadis_name);
        tv_hadis = (TextView)view.findViewById(R.id.tv_hadis);
        tv_read_hadis = (TextView)view.findViewById(R.id.tv_read_hadis);
        tv_dua_name = (TextView)view.findViewById(R.id.tv_dua_name);
        tv_dua_arabic = (TextView)view.findViewById(R.id.tv_dua_arabic);
        tv_dua_arabic_transcribed = (TextView)view.findViewById(R.id.tv_dua_arabic_transcibed);
        tv_dua_tajik = (TextView)view.findViewById(R.id.tv_dua_tajik);
        tv_read_dua = (TextView)view.findViewById(R.id.tv_read_dua);
        tv_name_arabic = (TextView)view.findViewById(R.id.tv_name_arabic);
        tv_name_arabic_transcribed = (TextView)view.findViewById(R.id.tv_name_arabic_transcribed);
        tv_name_tajik = (TextView)view.findViewById(R.id.tv_name_tajik);
        tv_read_name = (TextView)view.findViewById(R.id.tv_read_name);
        tv_share = (TextView)view.findViewById(R.id.tv_share);


        iv_notification = (ImageView)view.findViewById(R.id.iv_notification);
        iv_notification.setTag("fajrNot");
        iv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRadioButtonDialog();
            }
        });

        /*Get Shared Preference*/
        sharedPreferences = getActivity().getSharedPreferences("PrayerData",0);
        editor = sharedPreferences.edit();
        editor.apply();


        // for now just hide remaining time;
        tv_prayer_remaining.setVisibility(View.INVISIBLE);
        tv_location.setText("Дар "+sharedPreferences.getString("address","Address not found"));

        /*Get todays prayer timings*/
        //Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        //new GetPrayerTimesToday().execute(timestamp.getTime()/1000);


        setPrayerTimes();
        return view;

    }


    public class GetPrayerTimesToday extends AsyncTask<Long,Void,String> {
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

                setPrayerTimes();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }



    private void setPrayerTimes(){

        String fajr,sunrise,dhuhr,asr,sunset,maghrib,isha,imsak,midnight;

        String dateReadable,dateTimestamp;

        fajr = sharedPreferences.getString("fajr",null);
        sunrise = sharedPreferences.getString("sunrise",null);
        dhuhr = sharedPreferences.getString("dhuhr",null);
        asr = sharedPreferences.getString("asr",null);
        sunset = sharedPreferences.getString("sunset",null);
        maghrib = sharedPreferences.getString("maghrib",null);
        isha = sharedPreferences.getString("isha",null);
        imsak = sharedPreferences.getString("imsak",null);
        midnight = sharedPreferences.getString("midnight",null);

        dateReadable = sharedPreferences.getString("dateReadable",null);
        dateTimestamp = sharedPreferences.getString("dateTimestamp",null);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Date today = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        int curTime = getMinutes(dateFormat.format(today));

        int[] times = new int[]{getMinutes(fajr),getMinutes(sunrise),getMinutes(dhuhr),getMinutes(asr),
                getMinutes(sunset),getMinutes(maghrib),getMinutes(isha),getMinutes(imsak),getMinutes(midnight)};

        //set default notifications settings from shared preference;
        Integer[] images = new Integer[]{R.drawable.ic_notification_white,
                R.drawable.ic_adhan_notification_white,
                R.drawable.ic_silent_notification_white,
                R.drawable.ic_block_notification_white};

        if (curTime < times[1]){// before fajr and during
            //setFajr(curTime,times[0],fajr);
            tv_prayer_time.setText(fajr);
            tv_prayer_name.setText("Бомдод");
            iv_notification.setImageResource(images[sharedPreferences.getInt("fajrNot",0)]);
            iv_notification.setTag("fajrNot");
            // start count timer
        }
        else if ((times[2] > curTime) && (curTime >= times[1])){// during sunrise
            //setFajr(curTime,times[0],fajr);
            tv_prayer_time.setText(sunrise);
            tv_prayer_name.setText("Офтоб баромад");
            iv_notification.setImageResource(images[sharedPreferences.getInt("sunriseNot",3)]);
            iv_notification.setTag("sunriseNot");
            // start count timer
        }
        else if ((times[3] > curTime) && (curTime >= times[2])){// during dhuhr
            //setFajr(curTime,times[0],fajr);
            tv_prayer_time.setText(dhuhr);
            tv_prayer_name.setText("Пешин");
            iv_notification.setImageResource(images[sharedPreferences.getInt("dhuhrNot",0)]);
            iv_notification.setTag("dhuhrNot");
            // start count timer
        }
        else if ((times[4] > curTime) && (curTime >= times[3])){// during asr
            //setFajr(curTime,times[0],fajr);
            tv_prayer_time.setText(asr);
            tv_prayer_name.setText("Аср");
            iv_notification.setImageResource(images[sharedPreferences.getInt("asrNot",0)]);
            iv_notification.setTag("asrNot");
            // start count timer
        }
        else if ((times[6] > curTime) && (curTime >= times[5])){// during maghrib
            //setFajr(curTime,times[0],fajr);
            tv_prayer_time.setText(maghrib);
            tv_prayer_name.setText("Шом");
            iv_notification.setImageResource(images[sharedPreferences.getInt("maghribNot",0)]);
            iv_notification.setTag("maghribNot");
            // start count timer
        }
        else if ( (curTime > times[6])){// during isha
            //setFajr(curTime,times[0],fajr);
            tv_prayer_time.setText(isha);
            tv_prayer_name.setText("Хуфтан");
            iv_notification.setImageResource(images[sharedPreferences.getInt("ishaNot",0)]);
            iv_notification.setTag("ishaNot");
            // start count timer
        }
        else {
            tv_prayer_name.setText("Бомдоди пагох");
            iv_notification.setImageResource(images[sharedPreferences.getInt("fajrNot",0)]);
            iv_notification.setTag("fajrNot");
            //
        }


    }

    public int getMinutes(String time){
        String[] units = time.split(":"); //will break the string up into an array
        int hours = Integer.parseInt(units[0]); //first element
        int minutes = Integer.parseInt(units[1]); //second element
        return 60 * hours + minutes;
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

    private void showRadioButtonDialog() {

        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.radiobutton_dialog);
        List<String> stringList=new ArrayList<>();  // here is list
        stringList.add("Хотиррасони пешфарз");
        stringList.add("Азон");
        stringList.add("Хотиррасони хомуш");
        stringList.add("Хотиррасон накардан");

        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);

        Drawable[] drawables = new Drawable[]{getResources().getDrawable(R.drawable.ic_notification_grey),
                getResources().getDrawable(R.drawable.ic_adhan_notification_grey),
                        getResources().getDrawable(R.drawable.ic_silent_notification_grey),
                                getResources().getDrawable(R.drawable.ic_block_notification_grey)};
        for(int i=0;i<stringList.size();i++){
            RadioButton rb=new RadioButton(getActivity()); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(stringList.get(i));
            rb.setCompoundDrawablePadding(10);
            rb.setCompoundDrawablesWithIntrinsicBounds(null,null,drawables[i],null);
            rg.addView(rb);
        }

        dialog.show();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, final int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    final RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        Log.e("selected RadioButton->",btn.getText().toString()+checkedId);
                        new CountDownTimer(500,500) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                dialog.dismiss();
                                switch (btn.getText().toString()){
                                    case "Азон":
                                        iv_notification.setImageResource(R.drawable.ic_adhan_notification_white);

                                        editor.putInt((String) iv_notification.getTag(),1);
                                        editor.apply();
                                        break;
                                    case "Хотиррасони хомуш":
                                        iv_notification.setImageResource(R.drawable.ic_silent_notification_white);

                                        editor.putInt((String) iv_notification.getTag(),2);
                                        editor.apply();
                                        break;
                                    case "Хотиррасон накардан":
                                        iv_notification.setImageResource(R.drawable.ic_block_notification_white);

                                        editor.putInt((String) iv_notification.getTag(),3);
                                        editor.apply();
                                        break;
                                    case "Хотиррасони пешфарз":
                                    default:
                                        iv_notification.setImageResource(R.drawable.ic_notification_white);

                                        editor.putInt((String) iv_notification.getTag(),0);
                                        editor.apply();
                                        break;
                                }

                            }
                        }.start();
                    }
                }
            }
        });

    }
}
