package com.simurgh.prayertimes;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by moshe on 28/06/2017.
 */

public class MosqueActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mosque);

        getSupportActionBar().setTitle("Масчиди Чомеъ");

        Bundle extras = getIntent().getExtras();
        String name = extras.getString("name");
        String address = extras.getString("address");
        Integer image = extras.getInt("image");
        Integer info = extras.getInt("info");

        TextView tv_name,tv_info,tv_address;
        ImageView imageView;

        tv_name = (TextView)findViewById(R.id.tv_name);
        tv_info = (TextView)findViewById(R.id.tv_info);
        tv_address = (TextView)findViewById(R.id.tv_address);


        imageView = (ImageView) findViewById(R.id.iv_image);

        tv_name.setText(name);
        tv_address.setText(address);
        tv_info.setText(getResources().getString(info));

        imageView.setImageResource(image);
    }
}
