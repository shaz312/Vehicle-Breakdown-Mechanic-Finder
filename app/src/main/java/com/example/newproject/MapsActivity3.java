package com.example.newproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity3 extends FragmentActivity  implements OnMapReadyCallback,

        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
       RoutingListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mgoogleApiClient;
    Location mlastLocation;

    LocationRequest mlocationRequest;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private LatLng pickupLatLng;

    private float jarak;
    private int distance;
    private int work=0;
    private Button mRideStatus,DriverBack,mServiceFinish,messageButton;
    private GeoFire geoFireWorking,geoFireAvailability;

    private DatabaseReference AssignedCustomerRef,AssignedCustomerPickUpRef,AssignedMechanicRef,DriversRef;
    private String driverID,id,customerID,service,mechanicName,mechanicPhone,requestId,serviceTotal,CustomerInvoiceName,serviceID,serviceDescription;

    private int status = 0,serviceCharge,serviceAmount;
    private LinearLayout CustomerInfo;
    private ImageView CustomerProfileImage;
    private TextView CustomerName,CustomerPhone,CustomerRegNumber,CustomerServiceDesription,CustomerVehicle,CustomerServiceMoreDesription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps3);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String customerRequestID = getIntent().getStringExtra("user");
        String time = getIntent().getStringExtra("phone");
        service = getIntent().getStringExtra("service");
        id = getIntent().getStringExtra("id");
        serviceDescription = getIntent().getStringExtra("serviceDescription");


        customerID=id;

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        driverID = mAuth.getCurrentUser().getUid();
        CustomerInfo = (LinearLayout) findViewById(R.id.customer_info);
        CustomerProfileImage = (ImageView) findViewById(R.id.customer_profileImage);
        CustomerName = (TextView) findViewById(R.id.customer_name);
        CustomerPhone = (TextView) findViewById(R.id.customer_phone);
        CustomerRegNumber = (TextView) findViewById(R.id.customer_registration_number);
        CustomerServiceDesription = (TextView) findViewById(R.id.customer_service);
        CustomerServiceMoreDesription = (TextView) findViewById(R.id.customer_service_description);
        CustomerVehicle= (TextView) findViewById(R.id.customer_vehicle);
        mRideStatus = (Button) findViewById(R.id.rideStatus);
        mServiceFinish = (Button) findViewById(R.id.serviceFinish);
        messageButton = (Button) findViewById(R.id.mechanic_message_btn);


        DriverBack = (Button) findViewById(R.id.back);
        DriverBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity3.this,TestActivity.class);
                startActivity(intent);
            }
        });

        if(id!=null)
        {
            GetAssignedCustomerInfo();
            GetAssignedCustomerPickUpLocation();
        }
        else{
            Intent intent = new Intent(MapsActivity3.this,DriversMapActivity.class);
            startActivity(intent);
        }

        mRideStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch(work){
                    case 0:
                       polylines = new ArrayList<>();
                        DriverBack.setVisibility(View.GONE);
                       // GetAssignedCustomerRequest();
                       // GetRecordServiceWorking();
                        GetAssignedMechanicWorking();
                        //getMechanicWorking();
                        mRideStatus.setText("Go to the location");
                        messageButton.setVisibility(View.VISIBLE);
                        messageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MapsActivity3.this,ChatMechanicActivity.class);
                                intent.putExtra("customerOrDriver" ,"Mechanic");
                                intent.putExtra("mechanic",driverID);
                                intent.putExtra("customer",customerID);
                               // intent.putExtra("customer",userId);
                                //intent.putExtra("mechanic",mechanicID);
                                // intent.putExtra("customer",userId);
                                startActivity(intent);
                            }
                        });
                        mServiceFinish.setVisibility(View.GONE);

                        break;
                    case 1:
                        DriverBack.setVisibility(View.GONE);
                        mRideStatus.setText("Service ongoing");
                        messageButton.setVisibility(View.GONE);
                        mServiceFinish.setVisibility(View.VISIBLE);
/*
                        work=2;
                        //showDialog();
                        recordRide();

                      //  endRide();
                        endService();

                        mRideStatus.setText("Service Done");
                        openActivity2();
                        //Intent intent = new Intent(MapsActivity3.this,MapsActivity4.class);
                        //startActivity(intent);
                        //Intent intent = new Intent(MapsActivity3.this,DriversMapActivity.class);
                        //startActivity(intent);

                        break;
*/
                    case 2:
                        //recordRide();

                        break;
                }

            }
        });

        mServiceFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch(work){
                    // endService();
                    case 1:
                        work=2;
                        mServiceFinish.setVisibility(View.VISIBLE);
                        //showDialog();

                        recordRide();
                        //  endRide();
                        endService();
                        mRideStatus.setText("Service completed");
                        openActivity2();
                        //Intent intent = new Intent(MapsActivity3.this,DriversMapActivity.class);
                        // startActivity(intent);
                        break;

                }
               // endRide();

            }
        });
    }


    private void showDialog() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);


        LayoutInflater inflater = LayoutInflater.from(this);
        View ser_layout = inflater.inflate(R.layout.service_charge,null);
        final TextView serviceCharge2 = (TextView) ser_layout.findViewById(R.id.service_charge);
        final TextView serviceCharge3 = (TextView) ser_layout.findViewById(R.id.title_chargeAmount);
        final EditText serviceExtraCharge = (EditText) ser_layout.findViewById(R.id.service_ExtraCharge);
        serviceCharge3.setText("Service Charge:");
        if(service .equals("Battery"))
        {
            serviceCharge2.setText("RM 50");
            serviceAmount = 50;
        }
        if(service.equals("Tyre"))
        {
            serviceCharge2.setText("RM 20");
            serviceAmount = 20;
        }
        if(service.equals("Others"))
        {
            serviceCharge2.setText("RM 10");
            serviceAmount = 10;
        }
        dialog.setTitle("Invoice");
        dialog.setMessage("Total Amount Service Charge:");
        dialog.setView(ser_layout);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (TextUtils.isEmpty(serviceExtraCharge.getText().toString())){
                    Toast.makeText(MapsActivity3.this, "Extra charge if have...", Toast.LENGTH_SHORT).show();
                    return;
                }
                String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference serviceDescRef = database.getReference();
                serviceDescRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Object value = dataSnapshot.getValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MapsActivity3.this,"Failed to read value",Toast.LENGTH_SHORT).show();
                    }

                });

                serviceTotal =serviceExtraCharge.getText().toString();
                serviceCharge =  Integer.parseInt(serviceTotal);
                serviceDescRef.child("history").child(requestId).child("extra charge").setValue(serviceCharge);
               // DatabaseReference ChargeRef = FirebaseDatabase.getInstance().getReference().child("history");
              //  ChargeRef.child(driverID).setValue(serviceCharge);
                //DatabaseReference ChargeRef = FirebaseDatabase.getInstance().getReference().child("history");
                //ChargeRef.child(requestId).child("amount").setValue(serviceCharge+serviceAmount);
                // FirebaseDatabase database = FirebaseDatabase.getInstance();
                //  DatabaseReference serviceDescRef = database.getReference();
                //serviceDescRef.child("Customers Requests").child(currentUserID).child("service description").setValue(serviceCharge);
                //  serviceDescRef.child("Customers Requests").child(currentUserID).child("Service").setValue(requestService);

            }
        });

        dialog.show();
        Intent intent = new Intent(MapsActivity3.this,DriversMapActivity.class);
        startActivity(intent);
    }

    private void GetAssignedCustomerRequest()
    {
        AssignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child("Drivers").child(driverID).child("Customers Requests").child("CustomerRideID");

        AssignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    work=1;
                    AssignedCustomerRef.setValue(id);

                    //status=1;
                    //customerID = dataSnapshot.getValue().toString();
                    GetAssignedCustomerPickUpLocation();
                    GetAssignedCustomerInfo();
                    GetRecordServiceWorking();

                    //GetAssignedCustomerDestination();
                    //  endRide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void GetDriverInfo() {
        DatabaseReference DriverDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverID);
        DriverDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name") !=null)
                    {
                        mechanicName = map.get("name").toString();
                    }
                    if(map.get("phone") !=null)
                    {
                        mechanicPhone = map.get("phone").toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Long getCurrentTimestamp() {
        Long timestamp = System.currentTimeMillis()/1000;
        return timestamp;
    }

    /*
    private void getMechanicWorking() {
        //CustomerInfo.setVisibility(View.VISIBLE);
        DatabaseReference MechanicDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Drivers Working");
        MechanicDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    DatabaseReference refMechanic = FirebaseDatabase.getInstance().getReference().child("Mechanic Working");
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("Mechanic Working") !=null)
                    {
                        mechanicWorking = map.get("Mechanic Working").toString();
                        refMechanic.child(userId).child("user").setValue(mechanicWorking);
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

     */

    private void GetAssignedMechanicWorking()
    {
        AssignedMechanicRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child("Customers").child(id).child("MechanicWorkID");

        AssignedMechanicRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                work = 1;
                AssignedMechanicRef.setValue(driverID);
                GetRecordServiceWorking();
                //status=1;
                //customerID = dataSnapshot.getValue().toString();
                GetAssignedCustomerPickUpLocation();
                GetAssignedCustomerInfo();
                //GetAssignedCustomerDestination();
                //  endRide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
               /* addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    work = 1;
                    AssignedMechanicRef.setValue(driverID);
                    //status=1;
                    //customerID = dataSnapshot.getValue().toString();
                    GetAssignedCustomerPickUpLocation();
                    GetAssignedCustomerInfo();
                    //GetAssignedCustomerDestination();
                    //  endRide();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

                */
    }

    private void GetRecordServiceWorking() {
/*
        DatabaseReference ServiceRef = FirebaseDatabase.getInstance().getReference().child("service");
        serviceID = ServiceRef.push().getKey();

        HashMap map = new HashMap();
        map.put("mechanic" , driverID);
        map.put("customer" , customerID);
        map.put("service" , service);
        //map.put("service description" ,serviceDescr);
        map.put("timestamp" , getCurrentTimestamp());
        map.put("jarak",jarak);
        //map.put("location/to/lat", destinationLatLng.latitude);
        //  map.put("location/to/lng", destinationLatLng.longitude);
        //map.put("distance", rideDistance);

        ServiceRef.child(serviceID).updateChildren(map);

        */

        // GetDriverInfo();
        AssignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Service").child(customerID);
        AssignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //  work=1;
                //AssignedCustomerRef.setValue(id);
                AssignedCustomerRef.child("mechanic").setValue(driverID);
                AssignedCustomerRef.child("mechanic name").setValue(mechanicName);
                AssignedCustomerRef.child("mechanic phone").setValue(mechanicPhone);
                AssignedCustomerRef.child("service").setValue(service);
                AssignedCustomerRef.child("serviceDescription").setValue(serviceDescription);
                AssignedCustomerRef.child("timestamp").setValue(getCurrentTimestamp());
                AssignedCustomerRef.child("jarak").setValue(distance);


                //status=1;
                //customerID = dataSnapshot.getValue().toString();
                //  GetAssignedCustomerPickUpLocation();
                //  GetAssignedCustomerInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*
        String requestId = HistoryRef.push().getKey();
        HashMap map = new HashMap();
        map.put("mechanic" , driverID);
       // map.put("customer" , customerID);
        map.put("service" , service);
        map.put("timestamp" , getCurrentTimestamp());

        map.put("jarak",jarak);
        //map.put("location/to/lat", destinationLatLng.latitude);
        //  map.put("location/to/lng", destinationLatLng.longitude);
        //map.put("distance", rideDistance);

        HistoryRef.child().updateChildren(map);

        */
    }
    private void GetAssignedCustomerInfo()
    {
        CustomerInfo.setVisibility(View.VISIBLE);
        CustomerServiceDesription.setText(service);
        CustomerServiceMoreDesription.setText(serviceDescription);
        DatabaseReference CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(id);
        CustomerDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name") !=null)
                    {
                        CustomerName.setText(map.get("name").toString());
                        CustomerInvoiceName=map.get("name").toString();
                    }
                    if(map.get("phone") !=null)
                    {
                        CustomerPhone.setText(map.get("phone").toString());
                    }
                    if(map.get("profileimagesUrl") !=null)
                    {
                        Glide.with(getApplication()).load(map.get("profileimagesUrl").toString()).into(CustomerProfileImage);
                    }
                    if(map.get("vehicle") !=null)
                    {
                        CustomerVehicle.setText(map.get("vehicle").toString());
                    }
                    if(map.get("registrationNumber") !=null)
                    {
                        CustomerRegNumber.setText(map.get("registrationNumber").toString());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private Marker pickupMarker;
    private ValueEventListener AssignedCustomerPickUpRefListener;
    private void GetAssignedCustomerPickUpLocation()
    {
        AssignedCustomerPickUpRef = FirebaseDatabase.getInstance().getReference().child("Customers Requests").child(customerID).child("l");

        AssignedCustomerPickUpRefListener = AssignedCustomerPickUpRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists() && !customerID.equals(""))
                {
                    List<Object> customerLocationMap = (List<Object>) dataSnapshot.getValue();

                    double LocationLat = 0;
                    double LocationLng = 0;

                    if(customerLocationMap.get(0) != null)
                    {
                        LocationLat = Double.parseDouble(customerLocationMap.get(0).toString());
                    }
                    if(customerLocationMap.get(1) != null)
                    {
                        LocationLng = Double.parseDouble(customerLocationMap.get(1).toString());
                    }

                    pickupLatLng = new LatLng(LocationLat,LocationLng);
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("PickUp Location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));
                    getRouteToMarker (pickupLatLng);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void recordRide(){
/*
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference DriversRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("history");
        DatabaseReference CustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerID).child("history");
        DatabaseReference HistoryRef = FirebaseDatabase.getInstance().getReference().child("history");
        requestId = HistoryRef.push().getKey();

        DriversRef.child(requestId).setValue(true);
        CustomerRef.child(requestId).setValue(true);
        HashMap map = new HashMap();
        map.put("driver" , userId);
        map.put("customer" , customerID);
        map.put("rating" , 0);
        map.put("timestamp" , getCurrentTimestamp());
        map.put("service" , service);
       // map.put("service description" ,serviceDescr);
        map.put("location/from/lat", pickupLatLng.latitude);
        map.put("location/from/lng", pickupLatLng.longitude);
        map.put("jarak",distance);
        //map.put("location/to/lat", destinationLatLng.latitude);
        //  map.put("location/to/lng", destinationLatLng.longitude);
        //map.put("distance", rideDistance);

        HistoryRef.child(requestId).updateChildren(map);

 */

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference DriversRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("history");
        DatabaseReference CustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerID).child("history");
        DatabaseReference HistoryRef = FirebaseDatabase.getInstance().getReference().child("history");
        requestId = HistoryRef.push().getKey();

        DriversRef.child(requestId).setValue(true);
        CustomerRef.child(requestId).setValue(true);
        HashMap map = new HashMap();
        map.put("driver" , userId);
        map.put("customer" , customerID);
        map.put("rating" , 0);
        map.put("timestamp" , getCurrentTimestamp());
        map.put("service" , service);
        // map.put("service description" ,serviceDescr);
        map.put("location/from/lat", pickupLatLng.latitude);
        map.put("location/from/lng", pickupLatLng.longitude);
        map.put("jarak",distance);
        if(service .equals("Battery"))
        {
            serviceAmount = 50;
        }
        if(service.equals("Tyre"))
        {
            serviceAmount = 20;
        }
        if(service.equals("Others"))
        {
            serviceAmount = 10;
        }
        map.put("amount",serviceAmount);
       // map.put("charge",serviceCharge);
        //map.put("location/to/lat", destinationLatLng.latitude);
        //  map.put("location/to/lng", destinationLatLng.longitude);
        //map.put("distance", rideDistance);

        HistoryRef.child(requestId).updateChildren(map);
    }

    private void endService(){

        erasePolylines();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ServiceRef = FirebaseDatabase.getInstance().getReference().child("Service").child(id);
        ServiceRef.removeValue();

        DatabaseReference CustomersRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(id).child("MechanicWorkID");
        CustomersRef.removeValue();
        geoFireWorking.removeLocation(driverID);
        /*String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ServiceRef = FirebaseDatabase.getInstance().getReference().child("Service").child(userId);
        ServiceRef.removeValue();
        DriversRef = FirebaseDatabase.getInstance().getReference();
        Query DriversQuery = DriversRef.child("Service").child(customerID).orderByChild("mechanic").equalTo(driverID);
        DriversQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot driverSnapshot: dataSnapshot.getChildren()){
                    driverSnapshot.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Log.e(TAG,"OnCancelled",databaseError.toException());
            }
        });
         */
        if(pickupMarker !=null )
        {
            pickupMarker.remove();

        }
        if(AssignedCustomerPickUpRefListener != null)
        {
            AssignedCustomerPickUpRef.removeEventListener(AssignedCustomerPickUpRefListener);

        }

        CustomerInfo.setVisibility(View.GONE);
        CustomerName.setText("");
        CustomerPhone.setText("");
        CustomerRegNumber.setText("");
        CustomerVehicle.setText("");
        CustomerServiceDesription.setText("");
        CustomerProfileImage.setImageResource(R.mipmap.ic_default_user);

    }

    public void openActivity2() {
       Intent intent = new Intent(this, InvoiceMechanic1.class);
        intent.putExtra("customer",CustomerInvoiceName);
        intent.putExtra("service", service);
        intent.putExtra("history",requestId);
       // intent.putExtra("mechanicID",driverID);
        //intent.putExtra("charge",estimatedCost);
        startActivity(intent);

        /*
        Intent intent = new Intent(this, InvoiceMechanic.class);
        intent.putExtra("customer",CustomerInvoiceName);
        intent.putExtra("service", service);
       // intent.putExtra("history",requestId);
        intent.putExtra("mechanicID",driverID);
        //intent.putExtra("charge",estimatedCost);
        startActivity(intent);

         */
    }
    private void endRide(){
        work=2;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ServiceRef = FirebaseDatabase.getInstance().getReference().child("Service").child(userId);
        ServiceRef.removeValue();

        geoFireWorking.removeLocation(driverID);
        //geoFireAvailability.setLocation(driverID,new GeoLocation(mlastLocation.getLatitude(),mlastLocation.getLongitude()));
        DatabaseReference AfterserviceRef = FirebaseDatabase.getInstance().getReference().child("Customers Requests").child(id);
        AfterserviceRef.removeValue();
        mRideStatus.setText("End service");
        erasePolylines();

        DatabaseReference CustomersRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(id).child("MechanicWorkID");
        CustomersRef.removeValue();

        work=2;
        //rideDistance= 0;
        if(pickupMarker !=null )
        {
            pickupMarker.remove();

        }
        if(AssignedCustomerPickUpRefListener != null)
        {
            AssignedCustomerPickUpRef.removeEventListener(AssignedCustomerPickUpRefListener);

        }


        CustomerInfo.setVisibility(View.GONE);
        CustomerName.setText("");
        CustomerPhone.setText("");
        CustomerRegNumber.setText("");
        CustomerVehicle.setText("");
        CustomerServiceDesription.setText("");
        CustomerProfileImage.setImageResource(R.mipmap.ic_default_user);

    }

    private void getRouteToMarker(LatLng pickupLatLng) {
        if(pickupLatLng !=null && mlastLocation !=null) {
            Routing routing = new Routing.Builder()
                    .key("AIzaSyBzCvtyodrMddypilnGr_UkYkvrYnoYa5E")
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(new LatLng(mlastLocation.getLatitude(), mlastLocation.getLongitude()), pickupLatLng)
                    .build();
            routing.execute();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient()
    {
        mgoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mgoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(1000);
       mlocationRequest.setFastestInterval(1000);
        mlocationRequest.setPriority(mlocationRequest.PRIORITY_HIGH_ACCURACY);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleApiClient,mlocationRequest,this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(getApplicationContext() != null) {
            mlastLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
           mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //GeoFire geoFire = new GeoFire(DriverAvailabilityRef);
            //geoFireAvailability.setLocation(userID,new GeoLocation(location.getLatitude(),location.getLongitude()));

            DatabaseReference DriverWorkingRef = FirebaseDatabase.getInstance().getReference().child("Drivers Working");
            geoFireWorking = new GeoFire(DriverWorkingRef);

            DatabaseReference DriverAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
            geoFireAvailability = new GeoFire(DriverAvailabilityRef);
          //  geoFireAvailability.setLocation(userID,new GeoLocation(location.getLatitude(),location.getLongitude()));

            switch(work) {
                case 1:
                    geoFireAvailability.removeLocation(userID);
                    //DriverWorkingRef.child(userID).child("customerID").setValue(id);
                    geoFireWorking.setLocation(userID,new GeoLocation(location.getLatitude(),location.getLongitude()));
                    DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference().child("Customers Requests").child(id);
                    serviceRef.removeValue();
                    break;
                default:
                     geoFireWorking.removeLocation(userID);
                    //geoFireAvailability.setLocation(userID,new GeoLocation(location.getLatitude(),location.getLongitude()));

                    break;
            }

        }

    }



    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    @Override
    public void onRoutingFailure(RouteException e) {

        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
/*
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }
*/
        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
           distance = route.get(i).getDistanceValue();
        }
    }

    @Override
    public void onRoutingCancelled() {

    }

    private  void erasePolylines(){
        for (Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }

}
