package com.example.newproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.TextView;

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

public class DriverSettingActivity extends AppCompatActivity {
    private EditText DriverNameField,DriverPhoneField,DriverCarField,DriverRegistrationNumberField;
    private TextView DriverEmailText;
    private Button SettingBack,SettingConfirm;

    private ImageView DriverProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference DriverDatabaseRef;
    private String userID;
    private String DriverName,DriverRegistrationNumber,DriverEmail;
    private String DriverCar;
    private String DriverPhone;
    private String DriverService;
    private String DriverProfileImageUrl;

    private Uri resultUri;

    private RadioGroup DriverRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_setting);

        DriverNameField = (EditText) findViewById(R.id.driver_name);
        DriverPhoneField = (EditText) findViewById(R.id.driver_phone);
        DriverCarField = (EditText) findViewById(R.id.driver_car);
        DriverRegistrationNumberField = (EditText) findViewById(R.id.driver_registration_number);
        DriverEmailText = (TextView) findViewById(R.id.Driver_email);
      //  DriverRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        SettingBack = (Button) findViewById(R.id.setting_back);
        SettingConfirm = (Button) findViewById(R.id.setting_confirm);
        DriverProfileImage = (ImageView) findViewById(R.id.driver_profileImage);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        DriverDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID);

        getUserInfo();

        DriverProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });
        SettingConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });

        SettingBack.setOnClickListener(new View.OnClickListener() {
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
                    if(map.get("email") !=null)
                    {
                        DriverEmail = map.get("email").toString();
                        DriverEmailText.setText(DriverEmail);
                    }
                    if(map.get("name") !=null)
                    {
                        DriverName = map.get("name").toString();
                        DriverNameField.setText(DriverName);
                    }
                    if(map.get("phone") !=null)
                    {
                        DriverPhone = map.get("phone").toString();
                        DriverPhoneField.setText(DriverPhone);
                    }
                    if(map.get("car") !=null)
                    {
                        DriverCar = map.get("car").toString();
                        DriverCarField.setText(DriverCar);
                    }
                    /*
                    if(map.get("service") !=null)
                    {
                        DriverService = map.get("service").toString();
                        switch(DriverService){
                            case"Battery":
                                DriverRadioGroup.check(R.id.battery);
                                break;

                            case"Tyre":
                                DriverRadioGroup.check(R.id.tyre);
                                break;

                            case"Towing":
                                DriverRadioGroup.check(R.id.towing);
                                break;

                        }
                    }

                     */
                    if(map.get("registrationNumber") !=null)
                    {
                        DriverRegistrationNumber = map.get("registrationNumber").toString();
                        DriverRegistrationNumberField.setText(DriverRegistrationNumber);
                    }
                    if(map.get("profileimagesUrl") !=null)
                    {
                        DriverProfileImageUrl = map.get("profileimagesUrl").toString();
                        Glide.with(getApplication()).load(DriverProfileImageUrl).into(DriverProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void saveUserInformation() {

        DriverName= DriverNameField.getText().toString();
        DriverPhone= DriverPhoneField.getText().toString();
        DriverCar=DriverCarField.getText().toString();
        DriverRegistrationNumber= DriverRegistrationNumberField.getText().toString();
/*
        int selectId = DriverRadioGroup.getCheckedRadioButtonId();
        final RadioButton radioButton = (RadioButton) findViewById(selectId);

        if(radioButton.getText() == null){
            return;
        }
*/
  //      DriverService = radioButton.getText().toString();
        Map userInfo = new HashMap();
        userInfo.put("name",DriverName);
        userInfo.put("phone",DriverPhone);
        userInfo.put("car",DriverCar);
        userInfo.put("registrationNumber",DriverRegistrationNumber);

        DriverDatabaseRef.updateChildren(userInfo);

        if(resultUri != null){
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile images").child(userID);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }


            ByteArrayOutputStream baos= new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                    while(!uri.isComplete());
                    Uri downloadUrl = uri.getResult();



                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Map newImage = new HashMap();
                    newImage.put("profileimagesUrl",downloadUrl.toString());
                    DriverDatabaseRef.updateChildren(newImage);

                    finish();
                    return;
                }
            });
        }
        else{
            finish();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode== Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            DriverProfileImage.setImageURI(resultUri);

        }
    }
}
