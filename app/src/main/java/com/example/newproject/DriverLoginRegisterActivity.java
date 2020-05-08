package com.example.newproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverLoginRegisterActivity extends AppCompatActivity {

    private Button DriverLoginButton;
    private Button DriverRegisterButton;
    private TextView DriverRegisterLink;
    private TextView DriverStatus;

    private EditText EmailDriver;
    private EditText PasswordDriver,DriverName,DriverCar,DriverPhone;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private DatabaseReference DriverDatabaseRef;
    private DatabaseReference DriverDatabaseNameRef;
    private DatabaseReference DriverDatabaseCarRef;
    private DatabaseReference DriverDatabasePhoneRef;
    private String onlineDriverID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login_register);

        mAuth = FirebaseAuth.getInstance();



        DriverLoginButton = (Button) findViewById(R.id.driver_login_btn);
        DriverRegisterButton = (Button) findViewById(R.id.driver_register_btn);
        DriverRegisterLink = (TextView) findViewById(R.id.driver_register_link);
        DriverStatus = (TextView) findViewById(R.id.driver_status);

        EmailDriver = (EditText) findViewById(R.id.email_driver);
        PasswordDriver = (EditText) findViewById(R.id.password_driver);
        DriverCar = (EditText) findViewById(R.id.driver_car);
        DriverName = (EditText) findViewById(R.id.driver_name);
        DriverPhone = (EditText) findViewById(R.id.driver_phone);

        loadingBar = new ProgressDialog(this);

        DriverCar.setVisibility(View.INVISIBLE);
        DriverName.setVisibility(View.INVISIBLE);
        DriverPhone.setVisibility(View.INVISIBLE);
        DriverRegisterButton.setVisibility(View.INVISIBLE);
        DriverRegisterButton.setEnabled(false);

        DriverRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverLoginButton.setVisibility(View.INVISIBLE);
                DriverRegisterLink.setVisibility(View.INVISIBLE);
                DriverCar.setVisibility(View.VISIBLE);
                DriverName.setVisibility(View.VISIBLE);
                DriverPhone.setVisibility(View.VISIBLE);
                DriverStatus.setText("Register Mechanic");

                DriverRegisterButton.setVisibility(View.VISIBLE);
                DriverRegisterButton.setEnabled(true);


            }
        });

        DriverRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailDriver.getText().toString();
                String password = PasswordDriver.getText().toString();
                String name = DriverName.getText().toString();
                String car = DriverCar.getText().toString();
                String phone = DriverPhone.getText().toString();

                RegisterDriver(email,password,name,car,phone);


            }
        });


        DriverLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailDriver.getText().toString();
                String password = PasswordDriver.getText().toString();

                SignInDriver(email,password);
            }
        });
    }

    private void SignInDriver(String email, String password)
    {
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(DriverLoginRegisterActivity.this,"Please Write Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(DriverLoginRegisterActivity.this,"Please Write Password", Toast.LENGTH_SHORT).show();
            return;
        }

        else
        {
            loadingBar.setTitle("Mechanic Login");
            loadingBar.setMessage("Please wait, while we are checking your credentials...");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {
                                Intent driverIntent = new Intent(DriverLoginRegisterActivity.this, DriversMapActivity.class);
                                startActivity(driverIntent);

                                Toast.makeText(DriverLoginRegisterActivity.this,"Mechanic Logged in Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                            else
                            {
                                Toast.makeText(DriverLoginRegisterActivity.this,"Login Unsuccessful, Please Try Again...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }

    }


    private void RegisterDriver(final String email, String password, final String name,final String car,final String phone)
    {
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(DriverLoginRegisterActivity.this,"Please Write Email ", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(DriverLoginRegisterActivity.this,"Please Write Password at least 6 character", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(DriverLoginRegisterActivity.this,"Please Write Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(car))
        {
            Toast.makeText(DriverLoginRegisterActivity.this,"Please Write Car", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(DriverLoginRegisterActivity.this,"Please Write Phone", Toast.LENGTH_SHORT).show();
            return;
        }

        else
        {
            loadingBar.setTitle("Mechanic Registration");
            loadingBar.setMessage("Please wait, while we are register your data...");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                           // String email = EmailDriver.getText().toString();
                            if(task.isSuccessful())
                            {

                                onlineDriverID = mAuth.getCurrentUser().getUid();
                                DriverDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(onlineDriverID);
                                DriverDatabaseRef.setValue(true);
                                DriverDatabaseNameRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(onlineDriverID).child("email");
                                DriverDatabaseNameRef.setValue(email);
                                DriverDatabaseNameRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(onlineDriverID).child("name");
                                DriverDatabaseNameRef.setValue(name);
                                DriverDatabaseCarRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(onlineDriverID).child("car");
                                DriverDatabaseCarRef.setValue(car);
                                DriverDatabasePhoneRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(onlineDriverID).child("phone");
                                DriverDatabasePhoneRef.setValue(phone);

                                Intent driverIntent = new Intent(DriverLoginRegisterActivity.this, DriversMapActivity.class);
                                startActivity(driverIntent);

                                Toast.makeText(DriverLoginRegisterActivity.this,"Mechanic Register Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();




                            }

                            else
                            {
                                Toast.makeText(DriverLoginRegisterActivity.this,"Registration Unsuccessful, Please Try Again...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

}
