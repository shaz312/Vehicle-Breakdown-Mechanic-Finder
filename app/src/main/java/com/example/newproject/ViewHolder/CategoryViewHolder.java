package com.example.newproject.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView customerRequestID,time,serviceType;


    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        customerRequestID = (TextView)itemView.findViewById(R.id.customer);
        time = (TextView)itemView.findViewById(R.id.time);
        serviceType = (TextView)itemView.findViewById(R.id.service);
    }

    @Override
    public void onClick(View v) {

    }
}