package com.example.newproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newproject.historyRecyclerView.HistoryObject;
import com.example.newproject.serviceRecyclerView.ServiceAdapter;
import com.example.newproject.serviceRecyclerView.ServiceObject;
import com.example.newproject.serviceRecyclerView.ServiceViewHolders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TestActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<ServiceObject> arrayList;
    private FirebaseRecyclerOptions<ServiceObject> options;
    private FirebaseRecyclerAdapter<ServiceObject, ServiceViewHolders> adapter;
    private DatabaseReference databaseReference;

    protected void onStart(){
        super.onStart();
        adapter.startListening();
    }

    protected void onStop(){
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        recyclerView = (RecyclerView) findViewById(R.id.serviceRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        arrayList = new ArrayList<ServiceObject>();

        databaseReference =FirebaseDatabase.getInstance().getReference().child("Customers Requests");
        databaseReference.keepSynced(true);

        options = new FirebaseRecyclerOptions.Builder<ServiceObject>().setQuery(databaseReference,ServiceObject.class).build();

        adapter = new FirebaseRecyclerAdapter<ServiceObject, ServiceViewHolders>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ServiceViewHolders serviceViewHolders, int i, @NonNull final ServiceObject serviceObject) {

                serviceViewHolders.registrationNumber.setText(serviceObject.getUser());
                serviceViewHolders.phone.setText(serviceObject.getPhone());
                serviceViewHolders.serviceType.setText(serviceObject.getService());
                serviceViewHolders.serviceDescription.setText(serviceObject.getServiceDescription());
                serviceViewHolders.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(TestActivity.this,MapsActivity3.class);
                        intent.putExtra("user",serviceObject.getUser());
                        intent.putExtra("phone",serviceObject.getPhone());
                        intent.putExtra("service",serviceObject.getService());
                        intent.putExtra("id",serviceObject.getId());
                        intent.putExtra("serviceDescription",serviceObject.getServiceDescription());
                        startActivity(intent);
/*
                        Intent intent = new Intent(v.getContext(), HistorySingleActivity.class);
                        Bundle b = new Bundle();
                        b.putString("rideId" ,customerRequestID.getText().toString());
                        intent.putExtras (b);
                        v.getContext().startActivity(intent);
                        */
                    }
                });
            }

            @NonNull
            @Override
            public ServiceViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                return new ServiceViewHolders(LayoutInflater.from(TestActivity.this).inflate(R.layout.item_service,viewGroup,false));
            }
        };

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);
    }
}
  /*
    DatabaseReference reference;
    RecyclerView recyclerView;
    ArrayList<ServiceObject> list;
    MyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        recyclerView = (RecyclerView) findViewById(R.id.serviceRecylerView);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));


        reference = FirebaseDatabase.getInstance().getReference().child("Customers Requests");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    ServiceObject p = dataSnapshot1.getValue(ServiceObject.class);
                    list.add(p);
                }
                adapter = new MyAdapter(TestActivity.this,list);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TestActivity.this, "Opsss.... Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
/*

     myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String value = dataSnapshot.getValue().toString();
                mUserName.add(value);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    private String userId;
    private RecyclerView mServiceRecyclerView;
    private RecyclerView.Adapter mServiceAdapter;
    private RecyclerView.LayoutManager mServiceLayoutManager;
    private Button btnTest;
    ArrayList<ServiceObject> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mServiceRecyclerView = (RecyclerView) findViewById(R.id.serviceRecylerView);
        mServiceRecyclerView.setNestedScrollingEnabled(false);
        mServiceRecyclerView.setHasFixedSize(true);
        mServiceLayoutManager = new LinearLayoutManager(TestActivity.this);
        list = new ArrayList<ServiceObject>();
        mServiceRecyclerView.setLayoutManager(mServiceLayoutManager);
        mServiceAdapter = new ServiceAdapter(getDataSetHistory(),TestActivity.this);
        mServiceRecyclerView.setAdapter(mServiceAdapter);


        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userHistoryDatabase = FirebaseDatabase.getInstance().getReference().child("Customers Requests");
        userHistoryDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ServiceObject sv = dataSnapshot1.getValue(ServiceObject.class);
                    list.add(sv);

                }
                mServiceAdapter = new ServiceAdapter(list, TestActivity.this);
                mServiceRecyclerView.setAdapter(mServiceAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TestActivity.this, "Opps...something qrong", Toast.LENGTH_SHORT).show();
            }
        });
        //getUserHistoryIds();


      //  test=(TextView)findViewById(R.id.textView3);
        //btnTest = (Button) findViewById(R.id.button);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intenthistory = new Intent(TestActivity.this, Main2Activity.class);
               // intenthistory.putExtra("customerOrDriver" ,"Drivers");
                startActivity(intenthistory);

            }
        });


    }

    private void getUserHistoryIds() {
        DatabaseReference userHistoryDatabase = FirebaseDatabase.getInstance().getReference().child("Customers Requests").child(userId);
        userHistoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String customerRequestID = dataSnapshot.getKey();
                    Long timestamp = 0L;

                    if(dataSnapshot.child("timestamp").getValue() != null){
                        timestamp = Long.valueOf(dataSnapshot.child("timestamp").getValue().toString());
                    }

                    ServiceObject obj = new ServiceObject(customerRequestID, getDate(timestamp));
                    resultsService.add(obj);
                    mServiceAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

        private void FetchRideInformation(final String ridekey) {
            DatabaseReference historyDatabaseRef = FirebaseDatabase.getInstance().getReference().child(ridekey);
            historyDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String customerRequestID  = dataSnapshot.getKey();
                        Long timestamp = 0L;
                        String distance="";
                        Double ridePrice=0.0;


                        for(DataSnapshot child: dataSnapshot.getChildren()){
                            if(child.getKey().equals("timestamp")){
                                timestamp = Long.valueOf(child.getValue().toString());
                            }
                        }

                        if(dataSnapshot.child("timestamp")!=null){
                            timestamp = Long.valueOf(dataSnapshot.child("timestamp").getValue().toString());
                        }

                    ServiceObject obj = new ServiceObject(customerRequestID,getDate(timestamp));

                    resultsService.add(obj);
                    mServiceAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void FetchRideInformation(final String ridekey) {
        DatabaseReference historyDatabaseRef = FirebaseDatabase.getInstance().getReference().child("g");
        historyDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String customerRequestID = dataSnapshot.getKey();
                    Long timestamp = 0L;
                    String distance="";
                    Double ridePrice=0.0;


                    for(DataSnapshot child: dataSnapshot.getChildren()){
                        if(child.getKey().equals("timestamp")){
                            timestamp = Long.valueOf(child.getValue().toString());
                        }
                    }

                    if(dataSnapshot.child("timestamp")!=null){
                        timestamp = Long.valueOf(dataSnapshot.child("timestamp").getValue().toString());
                    }
                    ServiceObject obj = new ServiceObject(customerRequestID, getDate(timestamp));
                    resultsService.add(obj);
                    mServiceAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserHistoryIds() {
        DatabaseReference userHistoryDatabase = FirebaseDatabase.getInstance().getReference().child("Customers Requests").child(userId);
        userHistoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String customerRequestID = dataSnapshot.getKey();
                    Long timestamp = 0L;

                    if(dataSnapshot.child("timestamp").getValue() != null){
                        timestamp = Long.valueOf(dataSnapshot.child("timestamp").getValue().toString());
                    }

                    ServiceObject obj = new ServiceObject(customerRequestID, getDate(timestamp));
                    resultsService.add(obj);
                    mServiceAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private String getDate(Long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time*1000);
        String date = DateFormat.format("MM-dd-yyyy hh:mm", cal).toString();
        return date;
    }

    private ArrayList resultsService = new ArrayList<ServiceObject>();
    private ArrayList<ServiceObject> getDataSetHistory() {
        return resultsService;
    }
}
*/