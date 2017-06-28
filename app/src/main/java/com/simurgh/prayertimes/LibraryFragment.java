package com.simurgh.prayertimes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by moshe on 27/06/2017.
 */

public class LibraryFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_library,container,false);

        ImageView book_1 = (ImageView)view.findViewById(R.id.iv_book_1);
        book_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bookReader = new Intent(getActivity(),BookActivity.class);
                bookReader.putExtra("book",1);
                startActivity(bookReader);
            }
        });

        ImageView book_2 = (ImageView)view.findViewById(R.id.iv_book_2);
        book_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bookReader = new Intent(getActivity(),BookActivity.class);
                bookReader.putExtra("book",2);
                startActivity(bookReader);
            }
        });
        return view;

    }
}
