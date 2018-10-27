package com.simurgh.prayertimes.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.simurgh.prayertimes.R;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by moshe on 27/06/2017.
 */

public class LibraryActivity extends Activity {

    private DataBook requestedDataBook;

    private SharedPreferences.Editor editor;
    private DataBookAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_library);

        RecyclerView mRecyclerView = findViewById(R.id.rv_books);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        ArrayList<DataBook> dataBooks = new ArrayList<>();

        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("PrayerData", 0);
        editor = sharedPreferences.edit();
        editor.apply();

        mAdapter = new DataBookAdapter(getApplicationContext(), dataBooks);
        mRecyclerView.setAdapter(mAdapter);


        dataBooks.add(new DataBook(
                "Зиндагиномаи Паёмбар",
                "book1_zindaginoma.pdf",
                0.2,
                R.drawable.book_1,
                0,
                sharedPreferences.getBoolean("book1_zindaginoma.pdf",false)));
        dataBooks.add(new DataBook(
                "Маҷмуъаи васиятҳои Имоми Аъзам Абўҳанифа (р)",
                "book2_vasiyatho.pdf",
                0.3,
                R.drawable.book_2,
                1,
                sharedPreferences.getBoolean("book2_vasiyatho.pdf",false)));
        dataBooks.add(new DataBook(
                "МУСНАДИ ИМОМ АБӮҲАНИФА",
                "book3_musnad.pdf",
                4.5,
                R.drawable.book_3,
                2,
                sharedPreferences.getBoolean("book3_musnad.pdf",false)));
        dataBooks.add(new DataBook(
                "Нақши дуо дар зиндагии инсон",
                "book4_duo.pdf",
                1.3,
                R.drawable.book_4,
                3,
                sharedPreferences.getBoolean("book4_duo.pdf",false)));
        dataBooks.add(new DataBook(
                "САВОЛУ ҶАВОБ ДАР БОРАИ АҲКОМИ ҲАҶ ВА УМРА",
                "book5_savol.pdf",
                3.7,
                R.drawable.book_5,
                4,
                sharedPreferences.getBoolean("book5_savol.pdf",false)));
        dataBooks.add(new DataBook(
                "ЧИҲИЛ ҲАДИСИ МАККӢ",
                "book6_chihil.pdf",
                0.5,
                R.drawable.book_6,
                5,
                sharedPreferences.getBoolean("book6_chihil.pdf",false)));

        mAdapter.notifyDataSetChanged();
    }

    public void animate(ImageView imageView, boolean value){
        if (value) {
            RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(5000);
            rotate.setInterpolator(new LinearInterpolator());
            imageView.startAnimation(rotate);
        }
        else {
            imageView.clearAnimation();
        }
    }

    private void downloadBook(final DataBook category){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference book = storageRef.child("islamicBooks/"+category.getEngName());


        File storagePath = new File(Environment.getExternalStorageDirectory(), "PrayerTimesBooks");
        if(!storagePath.exists()) {
            storagePath.mkdirs();
        }
        final File localFile = new File(storagePath,category.getName()+".pdf");


        book.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                category.setDownloaded(true);
                editor.putBoolean(category.getEngName(),true);
                editor.apply();
                mAdapter.notifyDataSetChanged();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Storage Failure", exception.getMessage());
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 11235: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadBook(requestedDataBook);
                } else {
                    Toast.makeText(getApplicationContext(),"Лутфан барои боргирии китобхо ба мо ичоза дихед.",Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public class DataBookAdapter extends
            RecyclerView.Adapter<DataBookAdapter.ViewHolder> {

        private ArrayList<DataBook> mCategory;
        private Context mContext;


        public DataBookAdapter(Context context, ArrayList<DataBook> category) {
            mCategory = category;
            mContext = context;
        }

        private Context getContext() {
            return mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View categoryView = inflater.inflate(R.layout.single_book, parent, false);
            return new ViewHolder(categoryView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final DataBook category = mCategory.get(position);
            TextView name = holder.name;
            ImageView imageView = holder.imageView;
            final ImageView download =holder.download;

            name.setText(category.getName());
            if (category.isDownloaded()){
                download.setImageResource(R.drawable.ic_downloaded);
            }
            else {
                download.setImageResource(R.drawable.ic_not_download);

            }
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!category.isDownloaded()){
                        download.setImageResource(R.drawable.ic_downloading);
                        animate(download, true);
                        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestedDataBook = category;
                            ActivityCompat.requestPermissions(LibraryActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 11235);
                        }

                        else {
                            downloadBook(category);
                        }
                    }
                    else {
                        Toast.makeText(getContext(),"Шумо китобро боргири кардагиед.",Toast.LENGTH_SHORT).show();

                    }
                }
            });

            imageView.setImageResource(category.getImage());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (category.isDownloaded()){
                        Intent bookReader = new Intent(getContext(),BookActivity.class);
                        bookReader.putExtra("book",category.getName());
                        startActivity(bookReader);
                    }
                    else {
                        Toast.makeText(getContext(),"Китобро боргири кунед. (1 бор боргири кардан кофист)",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return mCategory.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView name;
            private ImageView download;
            private ImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.tv_name);
                download = itemView.findViewById(R.id.iv_download);
                imageView = itemView.findViewById(R.id.iv_image);
            }

        }
    }

}
