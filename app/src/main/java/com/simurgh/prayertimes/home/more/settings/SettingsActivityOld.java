package com.simurgh.prayertimes.home.more.settings;

import android.os.Bundle;

import com.simurgh.prayertimes.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by moshe on 30/06/2017.
 */

public class SettingsActivityOld extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Танзимот");
    }
}
