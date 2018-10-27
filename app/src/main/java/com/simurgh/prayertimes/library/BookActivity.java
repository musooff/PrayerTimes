package com.simurgh.prayertimes.library;

import android.os.Build;
import android.os.Environment;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
import com.simurgh.prayertimes.R;

import java.io.File;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class BookActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        Bundle extras = getIntent().getExtras();
        String bookName = extras.getString("book");

        getSupportActionBar().setTitle(bookName);

        File storagePath = new File(Environment.getExternalStorageDirectory(), "PrayerTimesBooks");
        final File localFile = new File(storagePath,bookName+".pdf");


        PDFView pdfView = findViewById(R.id.pdfView);
        pdfView.fromFile(localFile)
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .defaultPage(0)
                .load();;
    }
}
