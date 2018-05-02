package com.digibuddies.rxjavaapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Vikram on 13-02-2018.
 */

public class countryadapter extends RecyclerView.Adapter<countryadapter.MyViewHolder>{
    List<Data.Worldpopulation> countryList;
    Context context;
    apiinterface apiin;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView info;
        public ImageView flag;

        public MyViewHolder(View view) {
            super(view);
            info = (TextView) view.findViewById(R.id.info);
            flag = (ImageView) view.findViewById(R.id.flag);
            }
    }


    public countryadapter(List<Data.Worldpopulation> countryList,Context context) {
        this.countryList = countryList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.country, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

            Data.Worldpopulation wp = countryList.get(position);
            holder.info.setText("Rank: "+wp.rank + " " + wp.country + "\n"+"Population: "+ wp.population);
            Picasso.with(context).load(wp.flag).into(holder.flag);
            holder.flag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,image.class);
                    intent.putExtra("image",wp.flag);
                    context.startActivity(intent);
                }
            });


        Log.d("loglog","done done");
            }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
