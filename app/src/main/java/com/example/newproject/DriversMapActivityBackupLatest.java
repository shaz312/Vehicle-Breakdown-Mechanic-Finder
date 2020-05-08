package com.example.newproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriversMapActivityBackupLatest extends FragmentActivity implements OnMapReadyCallback,

        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        RoutingListener,
        com.google.android.gms.location.LocationListener


{

    private GoogleMap mMap;

    GoogleApiClient googleApiClient;
    Location lastLocation;

    LocationRequest locationRequest;

    private Button LogoutDriverButton;
    private Button SettingsDriverButton,mRideStatus,mHistoryButton;

    private int status = 0;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Boolean currentLogOutDriverStatus = false;
    private float rideDistance;

    private float jarak;

    private DatabaseReference AssignedCustomerRef,AssignedCustomerPickUpRef,DriversRef,ref;
    private String driverID,customerID="",userID,service,serviceDescr;
    private LatLng destinationLatLng,pickupLatLng;

    private LinearLayout CustomerInfo;
    private ImageView CustomerProfileImage;
    private TextView CustomerName,CustomerPhone,CustomerService,CustomerServiceDesription;

    private Switch DriverworkingSwitch;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_map);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        driverID = mAuth.getCurrentUser().getUid();

        LogoutDriverButton = (Button) findViewById(R.id.driver_logout_btn);
        mRideStatus = (Button) findViewById(R.id.rideStatus);
        mHistoryButton = (Button) findViewById(R.id.history);
        mRideStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(status){
                    case 1:
                        status=2;
                        erasePolylines();
                        if(destinationLatLng.latitude!=0.0 && destinationLatLng.longitude!=0.0){
                            getRouteToMarker(destinationLatLng);
                        }
                        mRideStatus.setText("Service completed");
                        break;
                    case 2:
                        recordRide();
                        endRide();
                        break;
                }
            }
        });
        SettingsDriverButton = (Button)findViewById(R.id.driver_settings_btn);
        polylines = new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        CustomerInfo = (LinearLayout) findViewById(R.id.customer_info);
        CustomerProfileImage = (ImageView) findViewById(R.id.customer_profileImage);
        CustomerName = (TextView) findViewById(R.id.customer_name);
        CustomerPhone = (TextView) findViewById(R.id.customer_phone);
        CustomerService = (TextView) findViewById(R.id.customer_destination);
        CustomerServiceDesription = (TextView) findViewById(R.id.service_description);
        DriverworkingSwitch = (Switch) findViewById(R.id.workingSwitch);
        DriverworkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    connectDriver();
                }else{
                    DisconnectTheDriver();
                }
            }
        });

        LogoutDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentLogOutDriverStatus = true;
                DisconnectTheDriver();
                mAuth.signOut();
                LogOutDriver();
                return;
            }
        });

        SettingsDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DriversMapActivityBackupLatest.this,DriverSettingActivity.class);
                startActivity(intent);
                return;
            }
        });
        mHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intenthistory = new Intent(DriversMapActivityBackupLatest.this, HistoryActivity.class);
                intenthistory.putExtra("customerOrDriver" ,"Drivers");
                startActivity(intenthistory);
            }
        });

        GetAssignedCustomerRequest();

    }

    private void GetAssignedCustomerRequest()
    {
        AssignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child("Drivers").child(driverID).child("Customers Requests").child("CustomerRideID");

        AssignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    status=1;
                    customerID = dataSnapshot.getValue().toString();
                    GetAssignedCustomerPickUpLocation();
                    GetAssignedCustomerDestination();
                    GetAssignedCustomerInfo();
                }
                else{
                    endRide();

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

    private void getRouteToMarker(LatLng pickupLatLng) {
       if(pickupLatLng !=null && lastLocation !=null) {
           Routing routing = new Routing.Builder()
                   .key("AIzaSyBzCvtyodrMddypilnGr_UkYkvrYnoYa5E")
                   .travelMode(AbstractRouting.TravelMode.DRIVING)
                   .withListener(this)
                   .alternativeRoutes(false)
                   .waypoints(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), pickupLatLng)
                   .build();
           routing.execute();
       }
    }


        private void GetAssignedCustomerDestination()
        {
            AssignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child("Drivers").child(driverID).child("Customers Requests");

            AssignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if(map.get("service") != null){
                            service = map.get("service").toString();
                            CustomerService.setText("service:" + service);
                        }

                        if(map.get("service description") !=null)
                        {
                            serviceDescr = map.get("service description").toString();
                            CustomerServiceDesription.setText("Problem:" + serviceDescr);
                        }
/*
                        else if(map.get("service description")==null)
                        {
                            serviceDescr = map.get("service description").toString();
                            CustomerServiceDesription.setText("Problem:" + serviceDescr);
                        }
*/
                        else{
                          // CustomerDestination.setText("service");
                        }
                        Double destinationLng =0.0;
                        Double destinationLat = 0.0;

                        if(map.get("destinationLat") !=null){
                            destinationLat = Double.valueOf(map.get("destinationLat").toString());
                        }
                        if(map.get("destinationLng") !=null){
                            destinationLng = Double.valueOf(map.get("destinationLng").toString());
                            destinationLatLng = new LatLng(destinationLat,destinationLng);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    private void GetAssignedCustomerInfo()
    {
        CustomerInfo.setVisibility(View.VISIBLE);
        DatabaseReference CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerID);
        CustomerDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name") !=null)
                    {

                        CustomerName.setText(map.get("name").toString());
                    }
                    if(map.get("phone") !=null)
                    {

                        CustomerPhone.setText(map.get("phone").toString());
                    }

                    if(map.get("profileimagesUrl") !=null)
                    {
                        Glide.with(getApplication()).load(map.get("profileimagesUrl").toString()).into(CustomerProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void endRide(){
        mRideStatus.setText("Start service");
        erasePolylines();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DriversRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("Customers Requests");
        DriversRef.removeValue();

        DatabaseReference CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Customers Requests");
        GeoFire geoFire = new GeoFire(CustomerDatabaseRef);
        geoFire.removeLocation(customerID);
        customerID="";
        rideDistance= 0;
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
        CustomerService.setText("");
        CustomerServiceDesription.setText("");
        CustomerProfileImage.setImageResource(R.mipmap.ic_default_user);

    }


    private void recordRide(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference DriversRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("history");
        DatabaseReference CustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerID).child("history");
        DatabaseReference HistoryRef = FirebaseDatabase.getInstance().getReference().child("history");
        String requestId = HistoryRef.push().getKey();

        DriversRef.child(requestId).setValue(true);
        CustomerRef.child(requestId).setValue(true);
        HashMap map = new HashMap();
        map.put("driver" , userId);
        map.put("customer" , customerID);
        map.put("rating" , 0);
        map.put("timestamp" , getCurrentTimestamp());
        map.put("service" , service);
        map.put("service description" ,serviceDescr);
        map.put("location/from/lat", pickupLatLng.latitude);
        map.put("location/from/lng", pickupLatLng.longitude);
        map.put("jarak",jarak);
        //map.put("location/to/lat", destinationLatLng.latitude);
      //  map.put("location/to/lng", destinationLatLng.longitude);
        //map.put("distance", rideDistance);

        HistoryRef.child(requestId).updateChildren(map);
    }

    private Long getCurrentTimestamp() {
        Long timestamp = System.currentTimeMillis()/1000;
        return timestamp;
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        buildGoogleApiClient();
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            return;
        }
        mMap.setMyLocationEnabled(true);

    }
    /*
       @Override
     protected void onResume(){
           super.onResume();

           if(!!currentLogOutDriverStatus)
           {
               DisconnectTheDriver();
           }

       }*/
    @Override
    public void onConnected(@Nullable Bundle bundle)
    {

    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void connectDriver(){
        // if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION))
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
    }

    //every second got new location
    @Override
    public void onLocationChanged(Location location)
    {
        if(getApplicationContext() != null)
        {
            if(!customerID.equals("")){
                rideDistance += lastLocation.distanceTo(location)/10;
            }
            lastLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            //References to driver availability
            DatabaseReference DriverAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
            GeoFire geoFireAvailability = new GeoFire(DriverAvailabilityRef);

            DatabaseReference DriverWorkingRef = FirebaseDatabase.getInstance().getReference().child("Drivers Working");
            GeoFire geoFireWorking = new GeoFire(DriverWorkingRef);

            switch(customerID)
            {
                case "":
                    geoFireWorking.removeLocation(userID);
                    geoFireAvailability.setLocation(userID,new GeoLocation(location.getLatitude(),location.getLongitude()));
                    break;
                default:
                    geoFireAvailability.removeLocation(userID);
                    geoFireWorking.setLocation(userID,new GeoLocation(location.getLatitude(),location.getLongitude()));
                    break;

            }
        }


    }

    protected synchronized void buildGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }


    private void DisconnectTheDriver()
    {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference DriverAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");

        GeoFire geoFire = new GeoFire(DriverAvailabilityRef);
        geoFire.removeLocation(userID);
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
    }


    private void LogOutDriver()
    {
        Intent welcomeIntent = new Intent(DriversMapActivityBackupLatest.this,WelcomeActivity.class);
        welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(welcomeIntent);
        finish();

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

        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

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
            jarak = route.get(i).getDistanceValue();
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
