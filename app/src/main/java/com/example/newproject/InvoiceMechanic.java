package com.example.newproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InvoiceMechanic extends AppCompatActivity {

    private Button submit;
    private TextView customerNameTxt,serviceTxt,ExtrachargeAmountTxt;
    private EditText service_ExtraCharge;
    private String userId,serviceType,customerName,chargeAmount,history,mechanicId,serviceTotal;
    private int serviceCharge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_mechanic);


        submit = (Button) findViewById(R.id.invoice_receive);
        customerNameTxt = (TextView) findViewById(R.id.customerName);
        serviceTxt = (TextView)findViewById(R.id.service) ;
        ExtrachargeAmountTxt = (EditText) findViewById(R.id.service_ExtraCharge);
/*
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent intent = getIntent();
        serviceType = intent.getStringExtra("service");
        customerName = intent.getStringExtra("customer");
       // history = intent.getStringExtra("history");
        mechanicId = intent.getStringExtra("mechanicID");

          serviceTxt.setText(serviceType);
          customerNameTxt.setText(customerName);

/*
        serviceTotal = ExtrachargeAmountTxt.getText().toString();
        serviceCharge =  Integer.parseInt(serviceTotal);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference serviceDescRef = database.getReference();
        serviceDescRef.child("history").child(history).child("extra charge").setValue(serviceCharge);
*/

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InvoiceMechanic.this,DriversMapActivity.class);
                startActivity(intent);
            }
        });
      //  textView2.setText("" + number);
/*
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("history").child("Customers")
                .child(userId).child("history");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("MechanicWorkID")) {

                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if(map.get("MechanicWorkID") !=null)
                    {

                        mechanicID = (map.get("MechanicWorkID").toString());

                    }
                    //mechanicID = snapshot.getValue().toString();
                    //GetAssignedCustomerPickUpLocation();
                    //GetAssignedCustomerDestination();
                    polylines = new ArrayList<>();
                    CancelRequest.setVisibility(View.GONE);
                    GettingDriverLocation();
                    GetDriverInfo();
                    getHasRideEnded();
                    //GetAssignedCustomerInfo();
                    CallCabCarButton.setText("Looking for Mechanic Location");
                }
                else {
                    getMechanicWorking();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



*/

    }
}
