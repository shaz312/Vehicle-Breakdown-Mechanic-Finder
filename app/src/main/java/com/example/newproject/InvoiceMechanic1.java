package com.example.newproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class InvoiceMechanic1 extends AppCompatActivity {

    private Button confirm;
    private TextView mechanicNameTxt,serviceTxt,chargeAmountTxt,extraChargeAmountTxt;
    private String userId,serviceType,mechanicName,chargeAmount,customerName,history,mechanicId,extraCharge;
    private EditText extraChargeExt;
    private int serviceTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_mechanic1);

        confirm = (Button) findViewById(R.id.invoice_pay);
        mechanicNameTxt = (TextView) findViewById(R.id.mechanicName);
        serviceTxt = (TextView)findViewById(R.id.service) ;
        chargeAmountTxt = (TextView) findViewById(R.id.chargeAmount);
        extraChargeExt = (EditText) findViewById(R.id.extra_Charge);

        Intent intent = getIntent();
        serviceType = intent.getStringExtra("service");
        customerName = intent.getStringExtra("customer");
        history = intent.getStringExtra("history");
      //  mechanicId = intent.getStringExtra("mechanicID");
        serviceTxt.setText(serviceType);
        mechanicNameTxt.setText(customerName);

       // getExtraCharge();
/*
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent intent = getIntent();
        serviceType = intent.getStringExtra("service");
        mechanicName = intent.getStringExtra("mechanic");
        chargeAmount = intent.getStringExtra("charge");
        serviceTxt.setText(serviceType);
        mechanicNameTxt.setText(mechanicName);
        chargeAmountTxt.setText(chargeAmount);

        extraCharge = extraChargeExt.getText().toString();
        serviceTotal =  Integer.parseInt(extraCharge);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference serviceDescRef = database.getReference();
        serviceDescRef.child("history").child(history).child("extra charge").setValue(serviceTotal);

*/
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(InvoiceMechanic1.this,DriversMapActivity.class);

                startActivity(intent);
            }
        });
    }

    private void getExtraCharge() {

        extraCharge = extraChargeExt.getText().toString();
        serviceTotal =  Integer.parseInt(extraCharge);
        DatabaseReference HistoryRef = FirebaseDatabase.getInstance().getReference().child("history");
        HashMap map = new HashMap();
        map.put("extra charge",serviceTotal);
        HistoryRef.child(history).updateChildren(map);

    }
}
