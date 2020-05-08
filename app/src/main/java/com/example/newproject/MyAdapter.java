package com.example.newproject;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newproject.serviceRecyclerView.ServiceObject;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        ArrayList<ServiceObject> profiles;
         Context context;


    public MyAdapter(Context c , ArrayList<ServiceObject> p)
        {
        context = c;
        profiles = p;
        }

@NonNull
@Override
public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_service,null,false));
        }

@Override
public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    holder.customerRequestID.setText(profiles.get(position).getUser());
    holder.service.setText(profiles.get(position).getService());
    //if(profiles.get(position).getTime()!=null){
   //     holder.time.setText(profiles.get(position).getTime());
   // }

        }

@Override
public int getItemCount() {
        return profiles.size();
        }

class MyViewHolder extends RecyclerView.ViewHolder
{
    public TextView customerRequestID,service;
    public TextView time;

    public MyViewHolder(View itemView){
        super(itemView);
        // itemView.setOnClickListener(this);
        customerRequestID = (TextView) itemView.findViewById(R.id.customer);
        service = (TextView) itemView.findViewById(R.id.phone);
        time = (TextView) itemView.findViewById(R.id.service);
    }
}
}
