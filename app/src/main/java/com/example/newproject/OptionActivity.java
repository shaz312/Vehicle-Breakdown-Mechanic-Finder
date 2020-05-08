package com.example.newproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OptionActivity extends AppCompatActivity {

    private Button CustomerBatteryButton;
    private Button CustomerTowingButton;
    private Button CustomerTyreButton;
    private Button CustomerPetrolButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);


        CustomerBatteryButton = (Button) findViewById(R.id.battery_opt_btn);
        CustomerTowingButton = (Button) findViewById(R.id.tow_opt_btn);
        CustomerTyreButton = (Button) findViewById(R.id.tyre_opt_btn);
        CustomerPetrolButton = (Button) findViewById(R.id.petrol_opt_btn);

        CustomerBatteryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent OptionCustomerIntent = new Intent(OptionActivity.this, MapSearchActivity.class);
                startActivity(OptionCustomerIntent);
            }
        });

        CustomerTowingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent OptionCustomerIntent = new Intent(OptionActivity.this, MapSearchActivity.class);
                startActivity(OptionCustomerIntent);
            }
        });

        CustomerTyreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent OptionCustomerIntent = new Intent(OptionActivity.this, MapSearchActivity.class);
                startActivity(OptionCustomerIntent);
            }
        });

        CustomerPetrolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent OptionCustomerIntent = new Intent(OptionActivity.this, MapSearchActivity.class);
                startActivity(OptionCustomerIntent);
            }
        });




    }
}
