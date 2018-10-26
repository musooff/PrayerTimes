package com.simurgh.prayertimes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by moshe on 27/06/2017.
 */

public class QuranFragment extends Fragment {


    ArrayList<DataSuraNames> dataSuraNames;

    RecyclerView mRecyclerView;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    JSONObject surahs;
    JSONArray dataSurah;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_quran,container,false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_surahs);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        dataSuraNames = new ArrayList<>();

        sharedPreferences = getActivity().getSharedPreferences("PrayerData",0);
        editor = sharedPreferences.edit();
        editor.apply();

        try {
            InputStream inputStream = getActivity().getResources().getAssets().open("surahs.json");
            JSONObject jsonObject = new JSONObject(readStream(inputStream));
            dataSurah = jsonObject.getJSONArray("data");


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DataSurahAdapter mAdapter = new DataSurahAdapter(getContext(),dataSuraNames);
        mRecyclerView.setAdapter(mAdapter);
        if (sharedPreferences.getBoolean("surahsAvailable",false)){
            // just open
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(sharedPreferences.getString("surahs","none"));
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i <data.length(); i++){
                    int number = data.getJSONObject(i).getInt("number");
                    String name = data.getJSONObject(i).getString("name");
                    //String englishName = data.getJSONObject(i).getString("englishName");
                    //String englishNameTranslation = data.getJSONObject(i).getString("englishNameTranslation");

                    String englishName = dataSurah.getJSONObject(i).getString("name");
                    String englishNameTranslation = dataSurah.getJSONObject(i).getString("trans");

                    int numberOfAyahs = data.getJSONObject(i).getInt("numberOfAyahs");
                    String revelationType = data.getJSONObject(i).getString("revelationType");
                    dataSuraNames.add(new DataSuraNames(name,englishName,englishNameTranslation,number));
                }

                mRecyclerView.getAdapter().notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else {
            new DownloadSuras().execute();
        }




        return view;

    }

    public class DownloadSuras extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... params) {
            String str_url = "http://api.alquran.cloud/surah";
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
                for (int i = 0; i <data.length(); i++){
                    int number = data.getJSONObject(i).getInt("number");
                    String name = data.getJSONObject(i).getString("name");
                    //String englishName = data.getJSONObject(i).getString("englishName");
                    //String englishNameTranslation = data.getJSONObject(i).getString("englishNameTranslation");


                    String englishName = dataSurah.getJSONObject(i).getString("name");
                    String englishNameTranslation = dataSurah.getJSONObject(i).getString("trans");

                    int numberOfAyahs = data.getJSONObject(i).getInt("numberOfAyahs");
                    String revelationType = data.getJSONObject(i).getString("revelationType");
                    dataSuraNames.add(new DataSuraNames(name,englishName,englishNameTranslation,number));
                }

                mRecyclerView.getAdapter().notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            editor.putString("surahs",s);
            editor.putBoolean("surahsAvailable",true);
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

    public class DataSurahAdapter extends
            RecyclerView.Adapter<DataSurahAdapter.ViewHolder> {

        private ArrayList<DataSuraNames> mCategory;
        private Context mContext;


        public DataSurahAdapter(Context context, ArrayList<DataSuraNames> category) {
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
            View categoryView = inflater.inflate(R.layout.single_surah, parent, false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(categoryView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // Get the data model based on position
            final DataSuraNames category = mCategory.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    Intent surah = new Intent(getActivity(),SurahActivity.class);
                    surah.putExtra("name",category.getNameArabic());
                    surah.putExtra("eng",category.getNameArabicTranscripted());
                    surah.putExtra("id",category.getId());
                    startActivity(surah);
                }
            });

            // Set item views based on your views and data model
            TextView arabic = holder.arabic;
            TextView english = holder.english;
            TextView translated =holder.translated;
            TextView id = holder.id;

            arabic.setText(category.getNameArabic());
            english.setText(category.getNameArabicTranscripted());
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
