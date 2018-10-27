package com.simurgh.prayertimes.home.mosque;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleveroad.fanlayoutmanager.FanLayoutManager;
import com.cleveroad.fanlayoutmanager.FanLayoutManagerSettings;
import com.simurgh.prayertimes.mosque.DataMosque;
import com.simurgh.prayertimes.mosque.MosqueActivity;
import com.simurgh.prayertimes.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by moshe on 27/06/2017.
 */

public class MosqueFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_mosques,container,false);

        RecyclerView mRecyclerView = view.findViewById(R.id.rv_mosques);



        FanLayoutManagerSettings fanLayoutManagerSettings = FanLayoutManagerSettings
                .newBuilder(getContext())
                .withFanRadius(true)
                .withAngleItemBounce(0)
                .withViewWidthDp(200)
                .withViewHeightDp(400)
                .build();
        FanLayoutManager fanLayoutManager = new FanLayoutManager(getContext(), fanLayoutManagerSettings);


        mRecyclerView.setLayoutManager(fanLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        ArrayList<DataMosque> dataMosques = new ArrayList<>();
        DataMosqueAdapter mAdapter = new DataMosqueAdapter(dataMosques);
        mRecyclerView.setAdapter(mAdapter);

        dataMosques.add(new DataMosque("Масҷиди ҷомеи марказии ш. Душанбе ба номи Ҳоҷӣ Яъқуб", "ш. Душанбе, кӯч.Шодмонӣ, 58 Телефон :+(992 37) 224-25-11",R.drawable.mj_markazi,R.string.mj_markazi));
        dataMosques.add(new DataMosque("Масҷиди ҷомеи ба номи Ҷалолуддини Румии ноҳияи Шоҳмансур","ш. Душанбе, н. Шоҳмансур, кӯч “Бӯстон-3”.Тел:+(992) 919219176", R.drawable.mj_rumi,R.string.mj_imom_shomansur));
        dataMosques.add(new DataMosque("Масҷиди ҷомеи ба номи Усмон ибни Аффон (р) ноҳияи И. Сомонӣ","ш. Душанбе, н. И. Сомонӣ, маҳаллаи Нодира 52, кӯч Хоҷамбиёи боло, тел: +992 904 56 66 67",R.drawable.mj_hz_usmon_somoni,R.string.mj_hz_usmon_somoni));
        dataMosques.add(new DataMosque("Масҷиди ҷомеи ба номи ҳазрати Усмон (р)-и ноҳияи Рӯдакӣ","н. Рӯдакӣ, ҷ/д “Зайнабобод“, деҳаи Тоҷикобод.Тел :+(992) 919438373",R.drawable.mj_hz_usmon_rudaki,R.string.mj_hz_usmon_rudaki));
        dataMosques.add(new DataMosque("Масҷиди ҷомеи ба номи Имоми Аъзами ноҳияи Фирдавсӣ","Суроғаи масҷид: ш. Душанбе, н. Фирдавсӣ,кӯч Фирдавсӣ 13/10.Тел :+(992) 918704850",R.drawable.mj_imom_firdavsi,R.string.mj_imom_firdavsi));
        dataMosques.add(new DataMosque("Масҷиди ҷомеи марказии ноҳияи Ҳисор ба номи Имоми Аъзам","Суроға: н. Ҳисор, ҷ/д Мирзо Ризо,деҳаи Тӯда, тел: +(992) 918 86 95 15",R.drawable.mj_imom_hisor,R.string.mj_imom_hisor));
        dataMosques.add(new DataMosque("Масҷиди ба номи ҳазрати Билол (р)-и ноҳияи Фирдавсии шаҳри Душанбе","ш. Душанбе, н. Фирдавсӣ, маҳаллаи 64, кӯч. Фирдавсӣ, 61 Тел: +(992) 915533474 ",R.drawable.mj_hz_bilon_firdavsi,R.string.mj_hz_bilol));
        dataMosques.add(new DataMosque("Масҷиди ҷомеи марказии н.Шоҳмансури ш.Душанбе ба номи Имоми Аъзам","ш. Душанбе, кӯч.Айни 46, Телефон :+(992) 937038118",R.drawable.mj_imom_shomansur,R.string.mj_imom_shomansur));
        dataMosques.add(new DataMosque("Масҷиди ҷомеи марказии н. Рӯдакӣ ба номи Мавлоно Яъқуби Чархӣ","н. Рӯдакӣ, ҷамоати деҳоти\"Гулистон\" Тел :+(992) 951857777",R.drawable.mj_charxi_rudaki,R.string.mj_charxi_dushanbe));
        dataMosques.add(new DataMosque("Масҷиди ҷомеи ш. Душанбе ба номи Умари Форуқ (р)", "ш. Душанбе, н.Фирдавсӣ, маҳаллаи 61, кӯч. 50-солагӣ, Тел:+(992) 935230214",R.drawable.mj_hz_umar_dushanbe,R.string.mj_hz_umar_dushanbe));

        mAdapter.notifyDataSetChanged();

        return view;

    }



    public class DataMosqueAdapter extends RecyclerView.Adapter<DataMosqueAdapter.ViewHolder> {

        private ArrayList<DataMosque> mCategory;

        DataMosqueAdapter(ArrayList<DataMosque> category) {
            mCategory = category;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View categoryView = inflater.inflate(R.layout.single_mosque, parent, false);
            return new ViewHolder(categoryView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final DataMosque category = mCategory.get(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mosque = new Intent(getActivity(),MosqueActivity.class);
                    mosque.putExtra("name",category.getName());
                    mosque.putExtra("info",category.getInfo());
                    mosque.putExtra("image",category.getImage());
                    mosque.putExtra("address", category.getAddress());
                    startActivity(mosque);
                }
            });

            ImageView imageView = holder.imageView;
            TextView name = holder.name;
            name.setText(category.getName());
            imageView.setImageResource(category.getImage());


        }

        @Override
        public int getItemCount() {
            return mCategory.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private ImageView imageView;
            private TextView name;

            ViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.iv_mosque);
                name = itemView.findViewById(R.id.tv_name);
            }

        }
    }



}
