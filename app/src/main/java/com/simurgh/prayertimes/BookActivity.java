package com.simurgh.prayertimes;

import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

public class BookActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        // create a new renderer

        Bundle extras = getIntent().getExtras();
        PDFView pdfView = (PDFView)findViewById(R.id.pdfView);
        pdfView.fromAsset("book_"+extras.getInt("book")+".pdf")
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .defaultPage(0)
                .load();;
    }
}