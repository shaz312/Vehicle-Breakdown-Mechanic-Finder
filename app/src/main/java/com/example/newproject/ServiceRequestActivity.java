package com.example.newproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class ServiceRequestActivity extends AppCompatActivity {

    private RecyclerView mServiceRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_request);

        mServiceRecyclerView = (RecyclerView) findViewById(R.id.serviceRecylerView);
        mServiceRecyclerView.setNestedScrollingEnabled(true);
        mServiceRecyclerView.setHasFixedSize(true);
    }
}
