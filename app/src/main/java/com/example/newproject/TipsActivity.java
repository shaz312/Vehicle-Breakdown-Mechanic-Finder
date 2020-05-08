package com.example.newproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class TipsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);


        ImageButton imgAccBtn = (ImageButton) findViewById(R.id.imageButton);
        ImageButton imgTyreBtn = (ImageButton) findViewById(R.id.imageButton2);
        ImageButton imgBack = (ImageButton) findViewById(R.id.backButton);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TipsActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        imgAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TipsActivity.this,TipsAccident.class);
                startActivity(intent);
            }
        });

        imgTyreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TipsActivity.this,reminderTyre.class);
                startActivity(intent);
            }
        });


    }
}
