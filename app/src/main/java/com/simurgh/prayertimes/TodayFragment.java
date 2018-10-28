package com.simurgh.prayertimes;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.simurgh.prayertimes.names.NamesActivity;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

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

        /*Get Shared Preference*/
        sharedPreferences = getActivity().getSharedPreferences("PrayerData",0);
        editor = sharedPreferences.edit();
        editor.apply();

        setOneAyah();

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setOneAyah() {

        SimpleDateFormat dayFormat = new SimpleDateFormat("dd",Locale.US);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMyyyy",Locale.US);

        Date today = new Date();
        try {

            JSONObject sharedJson = new JSONObject(sharedPreferences.getString("ayahs"+dateFormat.format(today),"none"));

            JSONObject jsonObject = null;

            Log.e("dayVerse",dayFormat.format(today));

            jsonObject = sharedJson.getJSONArray("data").getJSONObject(Integer.parseInt(dayFormat.format(today))-1);
            tv_verse_name.setText(
                    jsonObject.getString("surahNameTajik")
                            + "("
                            +jsonObject.getInt("surahNumber")
                            +":"
                            +jsonObject.getInt("verseNumber")
                            +")");
            tv_verse.setText(jsonObject.getString("verseTajik"));


            final JSONObject finalJsonObject = jsonObject;
            tv_read_verse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.dialog_verve);

                        TextView diaArabic = (TextView)dialog.findViewById(R.id.tv_arabic);
                        TextView diaTajik = (TextView)dialog.findViewById(R.id.tv_eng);
                        TextView diaTranslate = (TextView)dialog.findViewById(R.id.tv_tran);
                        TextView tajName = (TextView)dialog.findViewById(R.id.tv_tajName);
                        TextView araName = (TextView)dialog.findViewById(R.id.tv_araName);

                        TextView tvID = (TextView)dialog.findViewById(R.id.tv_id);

                        try {
                            diaArabic.setText(finalJsonObject.getString("verseArabic"));
                            diaTranslate.setText(finalJsonObject.getString("verseTajik"));
                            tajName.setText(finalJsonObject.getString("surahNameTajik"));
                            araName.setText(finalJsonObject.getString("surahNameArabic"));
                            dialog.show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

    }

}
