package com.simurgh.prayertimes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by moshe on 27/06/2017.
 */

public class LibraryFragment extends Fragment {

    ArrayList<DataBook> dataBooks;

    RecyclerView mRecyclerView;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    int id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_library,container,false);


        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_books);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        dataBooks = new ArrayList<>();

        sharedPreferences = getActivity().getSharedPreferences("PrayerData",0);
        editor = sharedPreferences.edit();
        editor.apply();
        DataBookAdapter mAdapter = new DataBookAdapter(getContext(),dataBooks);
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

        return view;

    }

    private void setBookDownloads() {
        editor.putBoolean("book1_zindaginoma.pdf",true);
        editor.putBoolean("book2_vasiyatho.pdf",false);
        editor.putBoolean("book3_musnad.pdf",false);
        editor.putBoolean("book4_duo.pdf",true);
        editor.putBoolean("book5_savol.pdf",false);
        editor.putBoolean("book6_chihil.pdf",true);
        editor.apply();
    }


    public class DataBookAdapter extends
            RecyclerView.Adapter<DataBookAdapter.ViewHolder> {

        private ArrayList<DataBook> mCategory;
        private Context mContext;


        public DataBookAdapter(Context context, ArrayList<DataBook> category) {
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
            View categoryView = inflater.inflate(R.layout.single_book, parent, false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(categoryView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // Get the data model based on position
            final DataBook category = mCategory.get(position);

            /*
            holder.itemView.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    // open the book

                    *
                    Intent bookReader = new Intent(getActivity(),BookActivity.class);
                bookReader.putExtra("book",1);
                startActivity(bookReader);

                }
            });

    */
            // Set item views based on your views and data model
            TextView name = holder.name;
            ImageView imageView = holder.imageView;
            final ImageView download =holder.download;

            name.setText(category.getName());
            if (category.isDownloaded()){
                download.setImageResource(R.drawable.ic_downloaded_blue);
            }
            else {
                download.setImageResource(R.drawable.ic_download_grey);

            }
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!category.isDownloaded()){
                        // request permission
                        // Here, thisActivity is the current activity
                        if (ActivityCompat.checkSelfPermission(getContext(),
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{
                                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    11235);
                        }


                        // download here
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        StorageReference book = storageRef.child("islamicBooks/"+category.getEngName());


                        File storagePath = new File(Environment.getExternalStorageDirectory(), "PrayerTimesBooks");
                        // Create direcorty if not exists
                        if(!storagePath.exists()) {
                            storagePath.mkdirs();
                        }
                        final File localFile = new File(storagePath,category.getName()+".pdf");


                        book.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                //Toast.makeText(getContext(),localFile.getName(),Toast.LENGTH_SHORT).show();
                                download.setImageResource(R.drawable.ic_downloaded_blue);
                                category.setDownloaded(true);
                                editor.putBoolean(category.getEngName(),true);
                                editor.apply();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });

                    }
                    else {
                        // prompt user to remove and remove
                        Toast.makeText(getContext(),"Шумо китобро боргири кардагиед.",Toast.LENGTH_SHORT).show();

                    }
                }
            });

            imageView.setImageResource(category.getImage());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (category.isDownloaded()){
                        Intent bookReader = new Intent(getActivity(),BookActivity.class);
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

        // Provide a direct reference to each of the views within a data item
        // Used to cache the views within the item layout for fast access
        public class ViewHolder extends RecyclerView.ViewHolder{
            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            private TextView name;
            private ImageView download;
            private ImageView imageView;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);

                name = (TextView) itemView.findViewById(R.id.tv_name);
                download = (ImageView) itemView.findViewById(R.id.iv_download);
                imageView = (ImageView) itemView.findViewById(R.id.iv_image);
            }

        }
    }

}
