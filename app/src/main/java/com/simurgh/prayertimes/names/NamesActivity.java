package com.simurgh.prayertimes.names;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.simurgh.prayertimes.R;
import com.simurgh.prayertimes.library.LibraryActivity;
import com.simurgh.prayertimes.model.AppPreference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by moshe on 29/06/2017.
 */

public class NamesActivity extends Activity implements MediaPlayer.OnPreparedListener {


    ArrayList<DataNames> dataNames;


    DataNames requestedDataName;
    MediaPlayer mMediaplayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_names);


        RecyclerView mRecyclerView = findViewById(R.id.rv_names);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new LibraryActivity.GridSpacingItemDecoration(2, 30, true));
        dataNames = new ArrayList<>();

        DataNamesAdapter mAdapter = new DataNamesAdapter(dataNames);
        mRecyclerView.setAdapter(mAdapter);

        // get names from string
        String names = getResources().getString(R.string.names);
        String[] eachNames = names.split("\n");
        String[] singleName;
        singleName = eachNames[1].split(" — ");
        dataNames.add(new DataNames(singleName[1],singleName[0],singleName[2],1));
        for (int i = 2; i < eachNames.length; i++){
            singleName = eachNames[i].split(" — ");
            dataNames.add(new DataNames(singleName[1],singleName[0].substring(1),singleName[2],i));
        }
        mAdapter.notifyDataSetChanged();


    }

    private void showErrorDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.network_error)
                .setMessage(R.string.network_error_names)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
    private AppPreference getAppPref() {
        return new AppPreference(this);
    }

    public void downloadAndPlay(DataNames category){
        if (!getAppPref().isConnected()){
            showErrorDialog();
            return;
        }
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference names = storageRef.child("names/name_"+category.getId()+".mp3");
        names.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        try {
                            final String url = uri.toString();
                            stopPlaying();
                            mMediaplayer = new MediaPlayer();
                            mMediaplayer.setDataSource(url);
                            mMediaplayer.setOnPreparedListener(NamesActivity.this);
                            mMediaplayer.prepareAsync();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("File problem", exception.getMessage());
                    }});

    }

    private void stopPlaying() {
        if (mMediaplayer != null) {
            mMediaplayer.stop();
            mMediaplayer.release();
            mMediaplayer = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public class DataNamesAdapter extends
            RecyclerView.Adapter<DataNamesAdapter.ViewHolder> {

        private ArrayList<DataNames> mCategory;


        DataNamesAdapter(ArrayList<DataNames> category) {
            mCategory = category;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View categoryView = inflater.inflate(R.layout.single_name, parent, false);

            return new ViewHolder(categoryView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final DataNames category = mCategory.get(position);
            TextView arabic = holder.arabic;
            TextView english = holder.english;
            ImageView tran = holder.tran;
            ImageView sound = holder.sound;

            sound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadAndPlay(category);
                }
            });

            arabic.setText(category.getNameArabic());
            english.setText(category.getNameArabicTranscripted());


            tran.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Dialog dialog = new Dialog(NamesActivity.this);
                    dialog.setContentView(R.layout.dialog_name);

                    TextView diaArabic = dialog.findViewById(R.id.tv_arabic_dia);
                    TextView diaTajik = dialog.findViewById(R.id.tv_tajik_dia);
                    TextView diaTranslate = dialog.findViewById(R.id.tv_translate_dia);
                    ImageView diaBack = dialog.findViewById(R.id.iv_back_dia);

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

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView arabic;
            private TextView english;
            private  ImageView tran;
            private ImageView sound;

            public ViewHolder(View itemView) {
                super(itemView);
                arabic = itemView.findViewById(R.id.tv_arabic);
                english = itemView.findViewById(R.id.tv_tajik);
                tran = itemView.findViewById(R.id.iv_tran_idk);
                sound = itemView.findViewById(R.id.iv_sound);
            }

        }
    }
}
