package com.simurgh.prayertimes.home.more;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.simurgh.prayertimes.R;
import com.simurgh.prayertimes.home.more.settings.SettingsActivityOld;

import androidx.fragment.app.Fragment;

/**
 * Created by moshe on 27/06/2017.
 */

public class MoreFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_more,container,false);

        LinearLayout ll_zakat,ll_qibla,ll_calendar,ll_tasbeh,ll_else,ll_settings,ll_help,ll_disclaimer;

        ll_zakat = view.findViewById(R.id.ll_zakat);
        ll_qibla = view.findViewById(R.id.ll_qibla);
        ll_calendar = view.findViewById(R.id.ll_calendar);
        ll_tasbeh = view.findViewById(R.id.ll_tasbih);
        ll_else = view.findViewById(R.id.ll_andmore);

        ll_settings = view.findViewById(R.id.ll_settings);
        ll_help = view.findViewById(R.id.ll_help);
        ll_disclaimer = view.findViewById(R.id.ll_disclaimer);


        // Under construction
        ll_zakat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Зери таъмирот аст. Дар муддати кутох бахри шумо пешкаш мекунем!",Toast.LENGTH_SHORT).show();
            }
        });
        ll_qibla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Зери таъмирот аст. Дар муддати кутох бахри шумо пешкаш мекунем!",Toast.LENGTH_SHORT).show();
            }
        });
        ll_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Зери таъмирот аст. Дар муддати кутох бахри шумо пешкаш мекунем!",Toast.LENGTH_SHORT).show();
            }
        });
        ll_tasbeh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Зери таъмирот аст. Дар муддати кутох бахри шумо пешкаш мекунем!",Toast.LENGTH_SHORT).show();
            }
        });
        ll_else.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Зери таъмирот аст. Дар муддати кутох бахри шумо пешкаш мекунем!",Toast.LENGTH_SHORT).show();
            }
        });


        ll_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings = new Intent(getActivity(),SettingsActivityOld.class);
                startActivity(settings);
            }
        });
        ll_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent settings = new Intent(getActivity(),HelpActivity.class);
                startActivity(settings);            }
        });
        ll_disclaimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent settings = new Intent(getActivity(),DisclaimerActivity.class);
                startActivity(settings);            }
        });


        return view;

    }
}
