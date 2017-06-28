package com.simurgh.prayertimes;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.Fade;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cleveroad.fanlayoutmanager.FanLayoutManager;
import com.cleveroad.fanlayoutmanager.FanLayoutManagerSettings;
import com.cleveroad.loopbar.widget.OnItemClickListener;

import java.util.ArrayList;

/**
 * Created by moshe on 27/06/2017.
 */

public class MosqueFragment extends Fragment {

    FanLayoutManager fanLayoutManager;
    ArrayList<DataMosque> dataMosques;

    RecyclerView mRecyclerView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_qibla,container,false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_mosques);


        FanLayoutManagerSettings fanLayoutManagerSettings = FanLayoutManagerSettings
                .newBuilder(getContext())
                .withFanRadius(true)
                .withAngleItemBounce(0)
                .withViewWidthDp(200)
                .withViewHeightDp(400)
                .build();
        fanLayoutManager = new FanLayoutManager(getContext(),fanLayoutManagerSettings);


        mRecyclerView.setLayoutManager(fanLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        dataMosques = new ArrayList<>();
        DataMosqueAdapter mAdapter = new DataMosqueAdapter(getContext(),dataMosques);
        mRecyclerView.setAdapter(mAdapter);

        dataMosques.add(new DataMosque("Масҷиди ҷомеи марказии ш. Душанбе ба номи Ҳоҷӣ Яъқуб", "ш. Душанбе, кӯч.Шодмонӣ, 58 Телефон :+(992 37) 224-25-11",R.drawable.mj_markazi,R.string.mj_markazi));
        dataMosques.add(new DataMosque("Масҷиди ҷомеи ба номи Ҷалолуддини Румии ноҳияи Шоҳмансур","ш. Душанбе, н. Шоҳмансур, кӯч “Бӯстон-3”.Тел:+(992) 919219176", R.drawable.mj_rumi,R.string.mj_imom_shomansur));
        dataMosques.add(new DataMosque("Масҷиди ҷомеи ба номи Усмон ибни Аффон (р) ноҳияи И. Сомонӣ","ш. Душанбе, н. И. Сомонӣ, маҳаллаи Нодира 52, кӯч Хоҷамбиёи боло, тел: +992 904 56 66 67",R.drawable.mj_hz_usmon_somoni,R.string.mj_hz_usmon_somoni));
        dataMosques.add(new DataMosque("Масҷиди ҷомеи ба номи ҳазрати Усмон (р)-и ноҳияи Рӯдакӣ","н. Рӯдакӣ, ҷ/д “Зайнабобод“, деҳаи Тоҷикобод.Тел :+(992) 919438373",R.drawable.mj_hz_usmon_rudaki,R.string.mj_hz_usmon_rudaki));
        dataMosques.add(new DataMosque("Масҷиди ҷомеи ба номи Имоми Аъзами ноҳияи Фирдавсӣ","Суроғаи масҷид: ш. Душанбе, н. Фирдавсӣ,кӯч Фирдавсӣ 13/10.Тел :+(992) 918704850",R.drawable.mj_imom_firdavsi,R.string.mj_imom_firdavsi));
        dataMosques.add(new DataMosque("Масҷиди ҷомеи марказии ноҳияи Ҳисор ба номи Имоми Аъзам","Суроға: н. Ҳисор, ҷ/д Мирзо Ризо,деҳаи Тӯда, тел: +(992) 918 86 95 15",R.drawable.mj_imom_hisor,R.string.mj_imom_hisor));
        dataMosques.add(new DataMosque("Масҷиди ба номи ҳазрати Билол (р)-и ноҳияи Фирдавсии шаҳри Душанбе","ш. Душанбе, н. Фирдавсӣ, маҳаллаи 64, кӯч. Фирдавсӣ, 61 Тел: +(992) 915533474 ",R.drawable.mj_hz_bilon_firdavsi,R.string.mj_hz_bilol));
        dataMosques.add(new DataMosque("Масҷиди ҷомеи марказии н.Шоҳмансури ш.Душанбе ба номи Имоми Аъзам","ш. Душанбе, кӯч.Айни 46, Телефон :+(992) 937038118",R.drawable.mj_imom_shomansur,R.string.mj_imom_shomansur));
        dataMosques.add(new DataMosque("Масҷиди ҷомеи марказии н. Рӯдакӣ ба номи Мавлоно Яъқуби Чархӣ","н. Рӯдакӣ, ҷамоати деҳоти\"Гулистон\" Тел :+(992) 951857777",R.drawable.mj_charxi_rudaki,R.drawable.mj_charxi_rudaki));
        dataMosques.add(new DataMosque("Масҷиди ҷомеи ш. Душанбе ба номи Умари Форуқ (р)", "ш. Душанбе, н.Фирдавсӣ, маҳаллаи 61, кӯч. 50-солагӣ, Тел:+(992) 935230214",R.drawable.mj_hz_umar_dushanbe,R.string.mj_hz_umar_dushanbe));

        mAdapter.notifyDataSetChanged();

        //fanLayoutManager.collapseViews();

        return view;

    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private String[] mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public LinearLayout mLinearLayout;
            public ViewHolder(LinearLayout v) {
                super(v);
                mLinearLayout = v;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(String[] myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_mosque, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            //holder.mLinearLayout.setText(mDataset[position]);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }



    public class DataMosqueAdapter extends
            RecyclerView.Adapter<DataMosqueAdapter.ViewHolder> {

        private ArrayList<DataMosque> mCategory;
        private Context mContext;


        public DataMosqueAdapter(Context context, ArrayList<DataMosque> category) {
            mCategory = category;
            mContext = context;
        }



        // Easy access to the context object in the recyclerview
        private Context getContext() {
            return mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View categoryView = inflater.inflate(R.layout.single_mosque, parent, false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(categoryView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // Get the data model based on position
            final DataMosque category = mCategory.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    Intent mosque = new Intent(getActivity(),MosqueActivity.class);
                    mosque.putExtra("name",category.getName());
                    mosque.putExtra("address",category.getAddress());
                    mosque.putExtra("info",category.getInfo());
                    mosque.putExtra("image",category.getImage());

                    startActivity(mosque);
                }
            });

            // Set item views based on your views and data model
            ImageView imageView = holder.imageView;
            TextView name = holder.name;
            TextView address =holder.address;

            name.setText(category.getName());
            //address.setText(category.getAddress());
            imageView.setImageResource(category.getImage());




        }

        @Override
        public int getItemCount() {
            return mCategory.size();
        }

        // Provide a direct reference to each of the views within a data item
        // Used to cache the views within the item layout for fast access
        public class ViewHolder extends RecyclerView.ViewHolder{
            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            private ImageView imageView;
            private TextView name;
            private TextView address;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);

                imageView = (ImageView)itemView.findViewById(R.id.iv_mosque);
                name = (TextView)itemView.findViewById(R.id.tv_name);
                address = (TextView)itemView.findViewById(R.id.tv_address);
            }

        }
    }



}
