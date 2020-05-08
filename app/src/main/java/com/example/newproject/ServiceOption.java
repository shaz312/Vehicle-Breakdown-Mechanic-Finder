package com.example.newproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServiceOption extends AppCompatActivity {
    private EditText DriverNameField,DriverPhoneField,DriverCarField;
    private Button RequestBack,RequestConfirm;

    private ImageView DriverProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference DriverDatabaseRef;
    private String userID;
    private String DriverName;
    private String DriverCar;
    private EditText ServiceProblemField;
    private String DriverService;
    private String ServiceRequest;
    private String ServiceProblem;
    private String DriverProfileImageUrl;

    private Uri resultUri;

    private RadioGroup ServiceRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_option);

        ServiceProblemField = (EditText) findViewById(R.id.problem);

        ServiceRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        RequestBack = (Button) findViewById(R.id.request_back);
        RequestConfirm = (Button) findViewById(R.id.request_confirm);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        DriverDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Customers Requests");

        getUserInfo();

        RequestConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });

        RequestBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });
    }



    private void getUserInfo()
    {
        DriverDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("problem") !=null)
                    {
                        ServiceProblem = map.get("name").toString();
                        ServiceProblemField.setText(ServiceProblem);
                    }
                    if(map.get("service") !=null)
                    {
                        ServiceRequest = map.get("service").toString();
                        switch(ServiceRequest){
                            case"Battery":
                                ServiceRadioGroup.check(R.id.battery);
                                break;

                            case"Tyre":
                                ServiceRadioGroup.check(R.id.tyre);
                                break;

                            case"Towing":
                                ServiceRadioGroup.check(R.id.towing);
                                break;

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void saveUserInformation() {

        ServiceProblem= ServiceProblemField.getText().toString();

        int selectId = ServiceRadioGroup.getCheckedRadioButtonId();
        final RadioButton radioButton = (RadioButton) findViewById(selectId);

        if(radioButton.getText() == null){
            return;
        }

        ServiceRequest = radioButton.getText().toString();
        Map userInfo = new HashMap();
        userInfo.put("service description",ServiceProblem);
        userInfo.put("service",ServiceRequest);

        DriverDatabaseRef.updateChildren(userInfo);

        finish();
        }


    }
