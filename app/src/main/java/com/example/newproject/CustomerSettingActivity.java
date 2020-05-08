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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomerSettingActivity extends AppCompatActivity {

    private EditText CustomerNameField,CustomerPhoneField,CustomerVehicleField,CustomerRegistrationNumberField;
    private TextView CustomerEmailText;
    private Button SettingBack,SettingConfirm;

    private ImageView CustomerProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference CustomerDatabaseRef;
    private String userID;
    private String CustomerName,CustomerEmail;
    private String CustomerPhone;
    private String CustomerVehicle;
    private String CustomerRegistrationNumber;
    private String CustomerProfileImageUrl;

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_setting);

        CustomerNameField = (EditText) findViewById(R.id.customer_name);
        CustomerPhoneField = (EditText) findViewById(R.id.customer_phone);
        CustomerVehicleField = (EditText) findViewById(R.id.customer_vehicle);
        CustomerRegistrationNumberField = (EditText) findViewById(R.id.customer_registration_number);

        SettingBack = (Button) findViewById(R.id.setting_back);
        SettingConfirm = (Button) findViewById(R.id.setting_confirm);
        CustomerProfileImage = (ImageView) findViewById(R.id.customer_profileImage);
        CustomerEmailText = (TextView) findViewById(R.id.customer_email);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID);

        getUserInfo();

        CustomerProfileImage.setOnClickListener(new View.OnClickListener() {
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
        CustomerDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("email") !=null)
                    {
                        CustomerEmail = map.get("email").toString();
                        CustomerEmailText.setText(CustomerEmail);
                    }
                    if(map.get("name") !=null)
                    {
                        CustomerName = map.get("name").toString();
                        CustomerNameField.setText(CustomerName);
                    }
                    if(map.get("phone") !=null)
                    {
                        CustomerPhone = map.get("phone").toString();
                        CustomerPhoneField.setText(CustomerPhone);
                    }
                    if(map.get("vehicle") !=null)
                    {
                        CustomerVehicle = map.get("vehicle").toString();
                        CustomerVehicleField.setText(CustomerVehicle);
                    }
                    if(map.get("registrationNumber") !=null)
                    {
                        CustomerRegistrationNumber = map.get("registrationNumber").toString();
                        CustomerRegistrationNumberField.setText(CustomerRegistrationNumber);
                    }
                    if(map.get("profileimagesUrl") !=null)
                    {
                        CustomerProfileImageUrl = map.get("profileimagesUrl").toString();
                        Glide.with(getApplication()).load(CustomerProfileImageUrl).into(CustomerProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void saveUserInformation() {

        CustomerName= CustomerNameField.getText().toString();
        CustomerPhone= CustomerPhoneField.getText().toString();
        CustomerVehicle= CustomerVehicleField.getText().toString();
        CustomerRegistrationNumber= CustomerRegistrationNumberField.getText().toString();
        Map userInfo = new HashMap();
        userInfo.put("name",CustomerName);
        userInfo.put("phone",CustomerPhone);
        userInfo.put("vehicle",CustomerVehicle);
        userInfo.put("registrationNumber",CustomerRegistrationNumber);

        CustomerDatabaseRef.updateChildren(userInfo);

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
                    CustomerDatabaseRef.updateChildren(newImage);

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
        if(requestCode == 1 && resultCode==Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            CustomerProfileImage.setImageURI(resultUri);

        }
    }
}
