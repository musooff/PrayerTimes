package com.simurgh.prayertimes;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by moshe on 28/06/2017.
 */

public class MosqueActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mosque);

        Bundle extras = getIntent().getExtras();
        String name = extras.getString("name");
        String address = extras.getString("address");
        Integer image = extras.getInt("image");
        Integer info = extras.getInt("info");

        TextView tv_name,tv_info,tv_address;
        ImageView imageView;

        tv_name = findViewById(R.id.tv_name);
        tv_info = findViewById(R.id.tv_info);
        tv_address = findViewById(R.id.tv_address);


        imageView = findViewById(R.id.iv_image);

        tv_name.setText(name);
        tv_address.setText(address);
        tv_info.setText(getResources().getString(info));

        imageView.setImageResource(image);
    }
}
