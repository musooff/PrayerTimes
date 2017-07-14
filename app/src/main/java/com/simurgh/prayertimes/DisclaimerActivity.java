package com.simurgh.prayertimes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by moshe on 30/06/2017.
 */

public class DisclaimerActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);

        getSupportActionBar().setTitle("Ҳуқуқҳо");

        ImageView iv_tajik = (ImageView)findViewById(R.id.iv_tajik);
        ImageView iv_english = (ImageView)findViewById(R.id.iv_english);

        final TextView tv_disclaimer = (TextView)findViewById(R.id.tv_disclaimer);

        tv_disclaimer.setText(getResources().getString(R.string.copyritht_tj));

        iv_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_disclaimer.setText(getResources().getString(R.string.copyright_en));
                getSupportActionBar().setTitle("Copyright Rights and Disclaimer");
            }
        });
        iv_tajik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_disclaimer.setText(getResources().getString(R.string.copyritht_tj));
                getSupportActionBar().setTitle("Ҳуқуқҳо");

            }
        });

    }
}
