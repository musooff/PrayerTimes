package com.simurgh.prayertimes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by moshe on 27/06/2017.
 */

public class MoreFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_more,container,false);

        LinearLayout ll_zakat,ll_qibla,ll_calendar,ll_tasbeh,ll_else,ll_settings,ll_help,ll_disclaimer;

        ll_zakat = (LinearLayout)view.findViewById(R.id.ll_zakat);
        ll_qibla = (LinearLayout)view.findViewById(R.id.ll_qibla);
        ll_calendar = (LinearLayout)view.findViewById(R.id.ll_calendar);
        ll_tasbeh = (LinearLayout)view.findViewById(R.id.ll_tasbih);
        ll_else = (LinearLayout)view.findViewById(R.id.ll_andmore);

        ll_settings = (LinearLayout)view.findViewById(R.id.ll_settings);
        ll_help = (LinearLayout)view.findViewById(R.id.ll_help);
        ll_disclaimer = (LinearLayout)view.findViewById(R.id.ll_disclaimer);


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
                Intent settings = new Intent(getActivity(),SettingsActivity.class);
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
