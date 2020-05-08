package com.example.newproject.serviceRecyclerView;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.example.newproject.R;

public class ServiceViewHolders extends RecyclerView.ViewHolder {
    public TextView registrationNumber,serviceType;
    public TextView phone,serviceDescription;

    public ServiceViewHolders(View itemView){
        super(itemView);
       // itemView.setOnClickListener(this);

        registrationNumber = (TextView) itemView.findViewById(R.id.customer);
        phone = (TextView) itemView.findViewById(R.id.phone);
        serviceType = (TextView) itemView.findViewById(R.id.service);
        serviceDescription = (TextView) itemView.findViewById(R.id.serviceDescription);


    }


}
