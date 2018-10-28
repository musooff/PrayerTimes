package com.simurgh.prayertimes.more;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.simurgh.prayertimes.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by moshe on 30/06/2017.
 */

public class DisclaimerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);


        ImageView iv_tajik = findViewById(R.id.iv_tajik);
        ImageView iv_english = findViewById(R.id.iv_english);

        final TextView tv_disclaimer = findViewById(R.id.tv_disclaimer);
        final TextView disclaimer_text = findViewById(R.id.disclaimer_text);

        tv_disclaimer.setText(getResources().getString(R.string.copyritht_tj));

        iv_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_disclaimer.setText(getResources().getString(R.string.copyright_en));
                disclaimer_text.setText("Copyright Rights and Disclaimer");
            }
        });
        iv_tajik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_disclaimer.setText(getResources().getString(R.string.copyritht_tj));
                disclaimer_text.setText("Ҳуқуқҳо");

            }
        });

    }
}
