package com.example.newproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerLoginRegisterActivity extends AppCompatActivity {

    private Button CustomerLoginButton;
    private Button CustomerRegisterButton;
    private TextView CustomerRegisterLink;
    private TextView CustomerStatus;

    private EditText EmailCustomer;
    private EditText PasswordCustomer;
    private EditText NameCustomer;
    private EditText PhoneCustomer;
    private EditText CarCustomer;

    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference CustomerDatabaseRef;
    private DatabaseReference CustomerDatabaseNameRef;
    private DatabaseReference CustomerDatabasePhoneRef;
    private String onlineCustomerID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login_register);

        mAuth = FirebaseAuth.getInstance();


        CustomerLoginButton = (Button) findViewById(R.id.customer_login_btn);
        CustomerRegisterButton = (Button) findViewById(R.id.customer_register_btn);
        CustomerRegisterLink = (TextView) findViewById(R.id.customer_register_link);
        CustomerStatus = (TextView) findViewById(R.id.customer_status);

        EmailCustomer = (EditText) findViewById(R.id.email_customer);
        PasswordCustomer = (EditText) findViewById(R.id.password_customer);
        NameCustomer = (EditText) findViewById(R.id.name_customer);
        PhoneCustomer = (EditText) findViewById(R.id.phone_customer);
        CarCustomer = (EditText) findViewById(R.id.car_customer);

        loadingBar = new ProgressDialog(this);


        NameCustomer.setVisibility(View.INVISIBLE);
        PhoneCustomer.setVisibility(View.INVISIBLE);
        CarCustomer.setVisibility(View.INVISIBLE);
        CustomerRegisterButton.setVisibility(View.INVISIBLE);
        CustomerRegisterButton.setEnabled(false);

        CustomerRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerLoginButton.setVisibility(View.INVISIBLE);
                CustomerRegisterLink.setVisibility(View.INVISIBLE);
                NameCustomer.setVisibility(View.VISIBLE);
                PhoneCustomer.setVisibility(View.VISIBLE);
                CarCustomer.setVisibility(View.VISIBLE);
                CustomerStatus.setText("Register Customer");

                CustomerRegisterButton.setVisibility(View.VISIBLE);
                CustomerRegisterButton.setEnabled(true);


            }
        });

        CustomerRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailCustomer.getText().toString();
                String password = PasswordCustomer.getText().toString();
                String name = NameCustomer.getText().toString();
                String phone = PhoneCustomer.getText().toString();
                String car = CarCustomer.getText().toString();

                RegisterCustomer(email,password,name,phone,car);


            }
        });

        CustomerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailCustomer.getText().toString();
                String password = PasswordCustomer.getText().toString();

                SignInCustomer(email,password);
            }
        });

    }

    private void SignInCustomer(String email, String password)
    {
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(CustomerLoginRegisterActivity.this,"Please Write Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(CustomerLoginRegisterActivity.this,"Please Write Password", Toast.LENGTH_SHORT).show();
            return;
        }

        else
        {
            loadingBar.setTitle("Customer Login");
            loadingBar.setMessage("Please wait, while we are checking your credentials...");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {
                                Intent customerIntent = new Intent(CustomerLoginRegisterActivity.this, MainActivity.class);
                                startActivity(customerIntent);
                                Toast.makeText(CustomerLoginRegisterActivity.this,"Customer Logged in Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }

                            else
                            {
                                Toast.makeText(CustomerLoginRegisterActivity.this,"Login Unsuccessful, Please Try Again...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void RegisterCustomer(final String email, String password, final String name, final String phone, final String car)
    {
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(CustomerLoginRegisterActivity.this,"Please Write Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(CustomerLoginRegisterActivity.this,"Please Write Password", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(CustomerLoginRegisterActivity.this,"Please Write Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(CustomerLoginRegisterActivity.this,"Please Write Phone Number", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(car))
        {
            Toast.makeText(CustomerLoginRegisterActivity.this,"Please Write Car model", Toast.LENGTH_SHORT).show();
            return;
        }

        else
        {
            loadingBar.setTitle("Customer Registration");
            loadingBar.setMessage("Please wait, while we are register your data...");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {

                                onlineCustomerID = mAuth.getCurrentUser().getUid();
                                CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(onlineCustomerID);
                                CustomerDatabaseRef.setValue(true);
                                CustomerDatabaseNameRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(onlineCustomerID).child("email");
                                CustomerDatabaseNameRef.setValue(email);
                                CustomerDatabaseNameRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(onlineCustomerID).child("name");
                                CustomerDatabaseNameRef.setValue(name);
                                CustomerDatabasePhoneRef  = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(onlineCustomerID).child("phone");
                                CustomerDatabasePhoneRef.setValue(phone);
                                CustomerDatabasePhoneRef  = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(onlineCustomerID).child("vehicle");
                                CustomerDatabasePhoneRef.setValue(car);


                                Intent driverIntent= new Intent(CustomerLoginRegisterActivity.this,MainActivity.class);
                                startActivity(driverIntent);

                                Toast.makeText(CustomerLoginRegisterActivity.this,"Customer Register Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                            else
                            {
                                Toast.makeText(CustomerLoginRegisterActivity.this,"Registration Unsuccessful, Please Try Again...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }
}
