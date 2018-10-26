package com.simurgh.prayertimes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by moshe on 29/06/2017.
 */

public class SurahActivity extends AppCompatActivity {

    ArrayList<DataAyats> dataAyatses;

    RecyclerView mRecyclerView;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    int id;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surah);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_ayats);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        dataAyatses = new ArrayList<>();

        sharedPreferences = getSharedPreferences("PrayerData",0);
        editor = sharedPreferences.edit();
        editor.apply();
        DataAyatsAdapter mAdapter = new DataAyatsAdapter(getApplicationContext(),dataAyatses);
        mRecyclerView.setAdapter(mAdapter);

        Bundle extras = getIntent().getExtras();
        String arabicName = extras.getString("name");
        String eng = extras.getString("eng");
        id = extras.getInt("id");

        TextView tv_eng,tv_ara;
        tv_eng = (TextView)findViewById(R.id.tv_eng);
        tv_ara = (TextView)findViewById(R.id.tv_arabic);

        tv_eng.setText(eng);
        tv_ara.setText(arabicName);

        if (sharedPreferences.getBoolean("ayatsAvailable"+id,false)){
            // just open
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(sharedPreferences.getString("ayats"+id,"none"));
                JSONArray data = jsonObject.getJSONArray("data");
                int numberOfAyahs = data.getJSONObject(0).getInt("numberOfAyahs");
                for (int i = 0; i < numberOfAyahs; i++){
                    int numberInSurah = data.getJSONObject(0).getJSONArray("ayahs").getJSONObject(i).getInt("numberInSurah");
                    String name = data.getJSONObject(0).getJSONArray("ayahs").getJSONObject(i).getString("text");
                    String englishName = data.getJSONObject(2).getJSONArray("ayahs").getJSONObject(i).getString("text");
                    String englishNameTranslation = data.getJSONObject(1).getJSONArray("ayahs").getJSONObject(i).getString("text");

                    dataAyatses.add(new DataAyats(name,englishName,englishNameTranslation,numberInSurah));
                }

                mRecyclerView.getAdapter().notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else {
            new DownloadAyats().execute(id);
        }



    }


    public class DownloadAyats extends AsyncTask<Integer,Void,String> {
        @Override
        protected String doInBackground(Integer... params) {
            String str_url = "http://api.alquran.cloud/surah/"+params[0]+"/editions/quran-simple,tg.ayati,en.transliteration";
            try {
                URL url = new URL(str_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                return readStream(inputStream);

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
                JSONObject jsonObject = new JSONObject(s);
                JSONArray data = jsonObject.getJSONArray("data");
                int numberOfAyahs = data.getJSONObject(0).getInt("numberOfAyahs");
                for (int i = 0; i < numberOfAyahs; i++){
                    int numberInSurah = data.getJSONObject(0).getJSONArray("ayahs").getJSONObject(i).getInt("numberInSurah");
                    String name = data.getJSONObject(0).getJSONArray("ayahs").getJSONObject(i).getString("text");
                    String englishName = data.getJSONObject(2).getJSONArray("ayahs").getJSONObject(i).getString("text");
                    String englishNameTranslation = data.getJSONObject(1).getJSONArray("ayahs").getJSONObject(i).getString("text");

                    dataAyatses.add(new DataAyats(name,englishName,englishNameTranslation,numberInSurah));
                }

                mRecyclerView.getAdapter().notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            editor.putString("ayats"+id,s);
            editor.putBoolean("ayatsAvailable"+id,true);
            editor.apply();
        }
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

    public class DataAyatsAdapter extends
            RecyclerView.Adapter<DataAyatsAdapter.ViewHolder> {

        private ArrayList<DataAyats> mCategory;
        private Context mContext;


        public DataAyatsAdapter(Context context, ArrayList<DataAyats> category) {
            mCategory = category;
            mContext = context;
        }



        // Easy access to the context object in the recyclerview
        private Context getContext() {
            return mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View categoryView = inflater.inflate(R.layout.single_ayat, parent, false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(categoryView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // Get the data model based on position
            final DataAyats category = mCategory.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {

                }
            });

            // Set item views based on your views and data model
            TextView arabic = holder.arabic;
            TextView english = holder.english;
            TextView translated =holder.translated;
            TextView id = holder.id;

            arabic.setText(category.getNameArabic());
            //english.setText(category.getNameArabicTranscripted());
            translated.setText(category.getNameEng());
            id.setText(category.getId()+"");




        }

        @Override
        public int getItemCount() {
            return mCategory.size();
        }

        // Provide a direct reference to each of the views within a data item
        // Used to cache the views within the item layout for fast access
        public class ViewHolder extends RecyclerView.ViewHolder{
            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            private TextView arabic;
            private TextView english;
            private TextView translated;
            private TextView id;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);

                arabic = (TextView) itemView.findViewById(R.id.tv_arabic);
                english = (TextView)itemView.findViewById(R.id.tv_eng);
                translated = (TextView)itemView.findViewById(R.id.tv_tran);
                id = (TextView)itemView.findViewById(R.id.tv_id);
            }

        }
    }
}
