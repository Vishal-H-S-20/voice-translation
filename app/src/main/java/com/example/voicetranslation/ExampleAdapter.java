package com.example.voicetranslation;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {

    private ArrayList<ExampleItem> mExampleList;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder{

        public ImageView mimageView;
        public TextView mtextView;
        public TextView mtextView2;
        public ExampleViewHolder( View itemView) {
            super(itemView);

            mimageView = itemView.findViewById(R.id.im);
            mtextView = itemView.findViewById(R.id.textView_from);
            mtextView2 = itemView.findViewById(R.id.textView_to);
        }
    }


    public ExampleAdapter(ArrayList<ExampleItem> exampleList)
    {
         mExampleList=exampleList;

    }


    @Override
    public ExampleViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View v =LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item,parent,false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder( ExampleAdapter.ExampleViewHolder holder, int position) {

        ExampleItem currentItem = mExampleList.get(position);

        holder.mimageView.setImageResource(currentItem.getmImageResource());
        holder.mtextView.setText(currentItem.getMtextView_from());
        holder.mtextView2.setText(currentItem.getMtextView_to());

    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
