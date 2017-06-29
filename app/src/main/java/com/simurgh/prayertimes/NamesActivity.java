package com.simurgh.prayertimes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import java.util.Arrays;

/**
 * Created by moshe on 29/06/2017.
 */

public class NamesActivity extends AppCompatActivity {


    ArrayList<DataNames> dataNames;

    RecyclerView mRecyclerView;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String[] test;

    int id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_names);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_names);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        dataNames = new ArrayList<>();

        sharedPreferences = getSharedPreferences("PrayerData",0);
        editor = sharedPreferences.edit();
        editor.apply();

        DataNamesAdapter mAdapter = new DataNamesAdapter(getApplicationContext(),dataNames);
        mRecyclerView.setAdapter(mAdapter);

        // get names from string
        String names = getResources().getString(R.string.names);
        String[] eachNames = names.split("\n");
        test = eachNames;
        //Log.e("name",eachNames[1]);
        //Log.e("name",eachNames[2]);

        String[] singleName;
        //singleName = eachNames[1].split(" — ");
        //Log.e("single",singleName[1]);
        //Log.e("single",singleName[2]);
        singleName = eachNames[1].split(" — ");
        dataNames.add(new DataNames(singleName[1],singleName[0],singleName[2],1));
        for (int i = 2; i < eachNames.length; i++){
            singleName = eachNames[i].split(" — ");
            dataNames.add(new DataNames(singleName[1],singleName[0].substring(1),singleName[2],i));
        }
        mAdapter.notifyDataSetChanged();


    }


    public class DataNamesAdapter extends
            RecyclerView.Adapter<DataNamesAdapter.ViewHolder> {

        private ArrayList<DataNames> mCategory;
        private Context mContext;


        public DataNamesAdapter(Context context, ArrayList<DataNames> category) {
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
            View categoryView = inflater.inflate(R.layout.single_name, parent, false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(categoryView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // Get the data model based on position
            final DataNames category = mCategory.get(position);


            // Set item views based on your views and data model
            TextView arabic = holder.arabic;
            TextView english = holder.english;
            TextView translated =holder.translated;
            //TextView id = holder.id;
            ImageView tran = holder.tran;
            ImageView sound = holder.sound;

            arabic.setText(category.getNameArabic());
            english.setText(category.getNameArabicTranscripted());
            //translated.setText(category.getNameEng());
            //id.setText(category.getId()+"");


            tran.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Dialog dialog = new Dialog(NamesActivity.this);
                    dialog.setContentView(R.layout.dialog_name);

                    TextView diaArabic = (TextView)dialog.findViewById(R.id.tv_arabic_dia);
                    TextView diaTajik = (TextView)dialog.findViewById(R.id.tv_tajik_dia);
                    TextView diaTranslate = (TextView)dialog.findViewById(R.id.tv_translate_dia);
                    ImageView diaSound = (ImageView)dialog.findViewById(R.id.iv_sound_dia);
                    ImageView diaBack = (ImageView)dialog.findViewById(R.id.iv_back_dia);

                    diaArabic.setText(category.getNameArabic());
                    diaTajik.setText(category.getNameArabicTranscripted());
                    diaTranslate.setText(category.getNameEng());

                    diaBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                }
            });




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
            //private TextView id;
            private  ImageView tran;
            private ImageView sound;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);

                arabic = (TextView) itemView.findViewById(R.id.tv_arabic);
                english = (TextView)itemView.findViewById(R.id.tv_tajik);
                //translated = (TextView)itemView.findViewById(R.id.tv_tran);
                //id = (TextView)itemView.findViewById(R.id.tv_id);
                tran = (ImageView)itemView.findViewById(R.id.iv_tran_idk);
                sound = (ImageView)itemView.findViewById(R.id.iv_sound);
            }

        }
    }
}
