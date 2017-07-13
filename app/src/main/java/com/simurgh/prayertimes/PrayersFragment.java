package com.simurgh.prayertimes;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by moshe on 26/06/2017.
 */

public class PrayersFragment extends Fragment {

    TextView tv_date_georgian, tv_date_islamic;
    TextView tv_fajr_name, tv_fajr_time,
            tv_sunrise_name, tv_sunrise_time,
            tv_dhuhr_name, tv_dhuhr_time,
            tv_asr_name, tv_asr_time,
            tv_maghrib_name, tv_maghrib_time,
            tv_isha_name, tv_isha_time;

    ImageView iv_next, iv_previous;
    SharedPreferences sharedPreferences;

    ImageView iv_fajr,iv_sunrise,iv_dhuhr,iv_asr,iv_maghrib,iv_isha;

    Date curDate,shownDate;

    SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_prayers, container, false);

        tv_date_georgian = (TextView)view.findViewById(R.id.tv_date_georgian);
        tv_date_islamic = (TextView)view.findViewById(R.id.tv_date_islamic);

        tv_fajr_name = (TextView)view.findViewById(R.id.tv_fajr_name);
        tv_fajr_time = (TextView)view.findViewById(R.id.tv_fajr_time);
        tv_sunrise_name = (TextView)view.findViewById(R.id.tv_sunrise_name);
        tv_sunrise_time = (TextView)view.findViewById(R.id.tv_sunrise_time);
        tv_dhuhr_name = (TextView)view.findViewById(R.id.tv_dhuhr_name);
        tv_dhuhr_time = (TextView)view.findViewById(R.id.tv_dhuhr_time);
        tv_asr_name = (TextView)view.findViewById(R.id.tv_asr_name);
        tv_asr_time = (TextView)view.findViewById(R.id.tv_asr_time);
        tv_maghrib_name = (TextView)view.findViewById(R.id.tv_maghrib_name);
        tv_maghrib_time = (TextView)view.findViewById(R.id.tv_maghrib_time);
        tv_isha_name = (TextView)view.findViewById(R.id.tv_isha_name);
        tv_isha_time = (TextView)view.findViewById(R.id.tv_isha_time);

        sharedPreferences = getActivity().getSharedPreferences("PrayerData", 0);
        editor = sharedPreferences.edit();
        editor.apply();

        tv_fajr_time.setText(sharedPreferences.getString("fajr","00:00"));
        tv_sunrise_time.setText(sharedPreferences.getString("sunrise","00:00"));
        tv_dhuhr_time.setText(sharedPreferences.getString("dhuhr","00:00"));
        tv_asr_time.setText(sharedPreferences.getString("asr","00:00"));
        tv_maghrib_time.setText(sharedPreferences.getString("maghrib","00:00"));
        tv_isha_time.setText(sharedPreferences.getString("isha","00:00"));

        iv_next = (ImageView)view.findViewById(R.id.iv_next);
        iv_previous = (ImageView)view.findViewById(R.id.iv_previous);

        iv_fajr = (ImageView)view.findViewById(R.id.iv_fajr_not);
        iv_sunrise = (ImageView)view.findViewById(R.id.iv_sunrise_not);
        iv_dhuhr = (ImageView)view.findViewById(R.id.iv_dhuhr_not);
        iv_asr = (ImageView)view.findViewById(R.id.iv_asr_not);
        iv_maghrib = (ImageView)view.findViewById(R.id.iv_maghrib_not);
        iv_isha = (ImageView)view.findViewById(R.id.iv_isha_not);

        setNotifications();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sharedCalFormat = new SimpleDateFormat("MMYYYY", Locale.US);



        curDate = new Date(timestamp.getTime());
        shownDate = curDate;

        setDate(timestamp.getTime());
        //setPrayerTimes();


        Log.e("cur",sharedCalFormat.format(curDate));
        // set todays prayer times by iterating calendar;

        JSONObject calendar = null;
        try {
            calendar = new JSONObject(sharedPreferences.getString(sharedCalFormat.format(curDate),"none"));
            JSONArray data = calendar.getJSONArray("data");
            SimpleDateFormat  apiDateFormat = new SimpleDateFormat("dd MMM yyyy",Locale.US);
            for (int j = 0; j < data.length(); j++){
                //Log.e("showndate",apiDateFormat.format(shownDate));
                //Log.e("dateReadable",data.getJSONObject(j).getJSONObject("date").getString("readable"));
                if (apiDateFormat.format(curDate).equals(data.getJSONObject(j).getJSONObject("date").getString("readable"))){
                    setPrayerTimesFromJson(data.getJSONObject(j));
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JSONObject finalCalendar = calendar;

        //Log.e("json",calendar.toString());
        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONArray data = finalCalendar.getJSONArray("data");
                    SimpleDateFormat  apiDateFormat = new SimpleDateFormat("dd MMM yyyy",Locale.US);
                    for (int j = 0; j < data.length(); j++){
                        //Log.e("showndate",apiDateFormat.format(shownDate));
                        //Log.e("dateReadable",data.getJSONObject(j).getJSONObject("date").getString("readable"));
                        if (apiDateFormat.format(shownDate).equals(data.getJSONObject(j).getJSONObject("date").getString("readable"))){
                            String tomorrowTime = data.getJSONObject(j+1).getJSONObject("date").getString("timestamp");
                            setDate(Long.valueOf(tomorrowTime)*1000);
                            //Log.e("date",data.getJSONObject(j+1).getJSONObject("date").getString("readable"));
                            if (shownDate.equals(curDate)){
                                //setPrayerTimes();
                                setPrayerTimesFromJson(data.getJSONObject(j+1));
                            }
                            else {
                                setPrayerTimesFromJson(data.getJSONObject(j+1));
                            }
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        iv_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONArray data = finalCalendar.getJSONArray("data");
                    SimpleDateFormat  apiDateFormat = new SimpleDateFormat("dd MMM yyyy",Locale.US);
                    for (int j = 0; j < data.length(); j++){
                        //Log.e("showndate",apiDateFormat.format(shownDate));
                        //Log.e("dateReadable",data.getJSONObject(j).getJSONObject("date").getString("readable"));
                        if (apiDateFormat.format(shownDate).equals(data.getJSONObject(j).getJSONObject("date").getString("readable"))){
                            String tomorrowTime = data.getJSONObject(j-1).getJSONObject("date").getString("timestamp");
                            setDate(Long.valueOf(tomorrowTime)*1000);
                            //Log.e("date",data.getJSONObject(j-1).getJSONObject("date").getString("readable"));
                            if (shownDate.equals(curDate)){
                                //setPrayerTimes();
                                setPrayerTimesFromJson(data.getJSONObject(j-1));

                            }
                            else {
                                setPrayerTimesFromJson(data.getJSONObject(j-1));
                            }
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    private void setNotifications() {

        //set default notifications settings from shared preference;
        Integer[] images = new Integer[]{R.drawable.ic_notification_grey,
                R.drawable.ic_adhan_notification_grey,
                R.drawable.ic_silent_notification_grey,
                R.drawable.ic_block_notification_grey};
        iv_fajr.setImageResource(images[sharedPreferences.getInt("fajrNot",0)]);
        iv_sunrise.setImageResource(images[sharedPreferences.getInt("sunriseNot",3)]);
        iv_dhuhr.setImageResource(images[sharedPreferences.getInt("dhuhrNot",0)]);
        iv_asr.setImageResource(images[sharedPreferences.getInt("asrNot",0)]);
        iv_maghrib.setImageResource(images[sharedPreferences.getInt("maghribNot",0)]);
        iv_isha.setImageResource(images[sharedPreferences.getInt("ishaNot",0)]);


        // set tags so that later we change not settings and save in shared preference
        iv_fajr.setTag("fajrNot");
        iv_sunrise.setTag("sunriseNot");
        iv_dhuhr.setTag("dhuhrNot");
        iv_asr.setTag("asrNot");
        iv_maghrib.setTag("maghribNot");
        iv_isha.setTag("ishaNot");

        iv_fajr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRadioButtonDialog(iv_fajr);
            }
        });
        iv_sunrise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRadioButtonDialog(iv_sunrise);
            }
        });
        iv_dhuhr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRadioButtonDialog(iv_dhuhr);
            }
        });
        iv_asr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRadioButtonDialog(iv_asr);
            }
        });
        iv_maghrib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRadioButtonDialog(iv_maghrib);
            }
        });
        iv_isha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRadioButtonDialog(iv_isha);
            }
        });

    }

    private void setDate(long timestamp) {
        Date today = new Date(timestamp);
        SimpleDateFormat georDateFormat = new SimpleDateFormat("EEEEEEEEE, d MMM", Locale.US);
        SimpleDateFormat islDateFormat = new SimpleDateFormat("d MMMMMM yyyy", Locale.US);

        shownDate = today;
        if (georDateFormat.format(shownDate).equals(georDateFormat.format(curDate))){
            tv_date_georgian.setText(georDateFormat.format(today)+" (Имруз)");
        }
        else {
            tv_date_georgian.setText(georDateFormat.format(today));
        }
    }

    private void setPrayerTimesFromJson(JSONObject data){

        String fajr,sunrise,dhuhr,asr,sunset,maghrib,isha,imsak,midnight;

        String dateReadable,dateTimestamp;
        try {
            JSONObject timings = data.getJSONObject("timings");

            fajr = timings.getString("Fajr");
            sunrise = timings.getString("Sunrise");
            dhuhr = timings.getString("Dhuhr");
            asr = timings.getString("Asr");
            sunset = timings.getString("Sunset");
            maghrib = timings.getString("Maghrib");
            isha = timings.getString("Isha");
            imsak = timings.getString("Imsak");
            midnight = timings.getString("Midnight");

            fajr = fajr.substring(0,fajr.indexOf(" "));
            sunrise = sunrise.substring(0,sunrise.indexOf(" "));
            dhuhr = dhuhr.substring(0,dhuhr.indexOf(" "));
            asr = asr.substring(0,asr.indexOf(" "));
            sunset = sunset.substring(0,sunset.indexOf(" "));
            maghrib = maghrib.substring(0,maghrib.indexOf(" "));
            isha = isha.substring(0,isha.indexOf(" "));
            imsak = imsak.substring(0,imsak.indexOf(" "));
            midnight = midnight.substring(0,midnight.indexOf(" "));

            tv_fajr_time.setText(fajr);
            tv_sunrise_time.setText(sunrise);
            tv_dhuhr_time.setText(dhuhr);
            tv_asr_time.setText(asr);
            tv_maghrib_time.setText(maghrib);
            tv_isha_time.setText(isha);

        //dateReadable = timings.getString("readable");
        //dateTimestamp = timings.getString("timestamp");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Date today = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        int curTime = getMinutes(dateFormat.format(today));

        int[] times = new int[]{getMinutes(fajr),getMinutes(sunrise),getMinutes(dhuhr),getMinutes(asr),
                getMinutes(sunset),getMinutes(maghrib),getMinutes(isha),getMinutes(imsak),getMinutes(midnight)};

        SimpleDateFormat equalDateFormat = new SimpleDateFormat("EEE, MMM yyyy", Locale.US);

        Log.e("cur",equalDateFormat.format(curDate));

        Log.e("shown",equalDateFormat.format(shownDate));

        if (equalDateFormat.format(curDate).equals(equalDateFormat.format(shownDate))){
            if (curTime < times[1]){// before fajr and during
                //setFajr(curTime,times[0],fajr);
                tv_fajr_name.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_fajr_time.setTextColor(getResources().getColor(R.color.colorPrimary));
                // start count timer
            }
            else if ((times[2] > curTime) && (curTime >= times[1])){// during sunrise
                //setFajr(curTime,times[0],fajr);
                tv_sunrise_name.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_sunrise_time.setTextColor(getResources().getColor(R.color.colorPrimary));
                // start count timer
            }
            else if ((times[3] > curTime) && (curTime >= times[2])){// during dhuhr
                //setFajr(curTime,times[0],fajr);
                tv_dhuhr_name.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_dhuhr_time.setTextColor(getResources().getColor(R.color.colorPrimary));
                // start count timer
            }
            else if ((times[4] > curTime) && (curTime >= times[3])){// during asr
                //setFajr(curTime,times[0],fajr);
                tv_asr_name.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_asr_time.setTextColor(getResources().getColor(R.color.colorPrimary));
                // start count timer
            }
            else if ((times[6] > curTime) && (curTime >= times[5])){// during maghrib
                //setFajr(curTime,times[0],fajr);
                tv_maghrib_name.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_maghrib_time.setTextColor(getResources().getColor(R.color.colorPrimary));
                // start count timer
            }
            else if ( (curTime > times[6])){// during isha
                //setFajr(curTime,times[0],fajr);
                tv_isha_name.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_isha_time.setTextColor(getResources().getColor(R.color.colorPrimary));
                // start count timer
            }

        }
        else {
            tv_fajr_name.setTextColor(getResources().getColor(R.color.grey));
            tv_fajr_time.setTextColor(getResources().getColor(R.color.grey));
            tv_sunrise_name.setTextColor(getResources().getColor(R.color.grey));
            tv_sunrise_time.setTextColor(getResources().getColor(R.color.grey));
            tv_dhuhr_name.setTextColor(getResources().getColor(R.color.grey));
            tv_dhuhr_time.setTextColor(getResources().getColor(R.color.grey));
            tv_asr_name.setTextColor(getResources().getColor(R.color.grey));
            tv_asr_time.setTextColor(getResources().getColor(R.color.grey));
            tv_maghrib_name.setTextColor(getResources().getColor(R.color.grey));
            tv_maghrib_time.setTextColor(getResources().getColor(R.color.grey));
            tv_isha_name.setTextColor(getResources().getColor(R.color.grey));
            tv_isha_time.setTextColor(getResources().getColor(R.color.grey));
            //
        }

        } catch (JSONException e) {
            e.printStackTrace();
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
        SimpleDateFormat equalDateFormat = new SimpleDateFormat("EEE, MMM yyyy", Locale.US);

        Log.e("cur",equalDateFormat.format(curDate));

        Log.e("shown",equalDateFormat.format(shownDate));

        if (equalDateFormat.format(curDate).equals(equalDateFormat.format(shownDate))){
            if (curTime < times[1]){// before fajr and during
                //setFajr(curTime,times[0],fajr);
                tv_fajr_name.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_fajr_time.setTextColor(getResources().getColor(R.color.colorPrimary));
                // start count timer
            }
            else if ((times[2] > curTime) && (curTime >= times[1])){// during sunrise
                //setFajr(curTime,times[0],fajr);
                tv_sunrise_name.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_sunrise_time.setTextColor(getResources().getColor(R.color.colorPrimary));
                // start count timer
            }
            else if ((times[3] > curTime) && (curTime >= times[2])){// during dhuhr
                //setFajr(curTime,times[0],fajr);
                tv_dhuhr_name.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_dhuhr_time.setTextColor(getResources().getColor(R.color.colorPrimary));
                // start count timer
            }
            else if ((times[4] > curTime) && (curTime >= times[3])){// during asr
                //setFajr(curTime,times[0],fajr);
                tv_asr_name.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_asr_time.setTextColor(getResources().getColor(R.color.colorPrimary));
                // start count timer
            }
            else if ((times[6] > curTime) && (curTime >= times[5])){// during maghrib
                //setFajr(curTime,times[0],fajr);
                tv_maghrib_name.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_maghrib_time.setTextColor(getResources().getColor(R.color.colorPrimary));
                // start count timer
            }
            else if ( (curTime > times[6])){// during isha
                //setFajr(curTime,times[0],fajr);
                tv_isha_name.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_isha_time.setTextColor(getResources().getColor(R.color.colorPrimary));
                // start count timer
            }
            else {
                tv_fajr_name.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_fajr_time.setTextColor(getResources().getColor(R.color.colorPrimary));
                //
            }
        }


    }

    public int getMinutes(String time){
        String[] units = time.split(":"); //will break the string up into an array
        int hours = Integer.parseInt(units[0]); //first element
        int minutes = Integer.parseInt(units[1]); //second element
        return 60 * hours + minutes;
    }


    private void showRadioButtonDialog(final ImageView imageView) {

        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_radiobuttons);
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
                                Log.e("tag", (String) imageView.getTag());
                                switch (btn.getText().toString()){
                                    case "Азон":
                                        imageView.setImageResource(R.drawable.ic_adhan_notification_grey);
                                        editor.putInt((String) imageView.getTag(),1);
                                        editor.apply();
                                        break;
                                    case "Хотиррасони хомуш":
                                        imageView.setImageResource(R.drawable.ic_silent_notification_grey);
                                        editor.putInt((String) imageView.getTag(),2);
                                        editor.apply();
                                        break;
                                    case "Хотиррасон накардан":
                                        imageView.setImageResource(R.drawable.ic_block_notification_grey);
                                        editor.putInt((String) imageView.getTag(),3);
                                        editor.apply();
                                        break;
                                    case "Хотиррасони пешфарз":
                                    default:
                                        editor.putInt((String) imageView.getTag(),0);
                                        editor.apply();
                                        imageView.setImageResource(R.drawable.ic_notification_grey);
                                        break;
                                }
                                dialog.dismiss();

                            }
                        }.start();
                    }
                }
            }
        });

    }


}
