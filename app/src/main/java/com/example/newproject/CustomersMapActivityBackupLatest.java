package com.example.newproject;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomersMapActivityBackupLatest extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener//,
       // PopupMenu.OnMenuItemClickListener

{

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    //SearchView searchView;

    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;

    private FusedLocationProviderClient mFusedLocationClient;
    private Button CustomerLogoutButton;
    private Button CallCabCarButton,ServiceDescription;
    private Button back,btnOK;
    private Button NearbyPlace;
    private Button CustomerSettingButton,CustomerHistoryButton;
    private String customerID;
    private LatLng CustomerPickUpLocation;
    private int radius = 1;
    private Boolean driverFound = false;
    private String driverFoundID;

    private Marker pickupMarker;
    private Boolean requestBol = false;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    Marker DriverMarker;
    private String requestService;
    private LatLng destinationLatLng;

    private DatabaseReference CustomerDatabaseRef;
    private DatabaseReference DriverAvailableRef;
    private DatabaseReference DriversRef;

    private LinearLayout DriverInfo;
    private ImageView DriverProfileImage;
    private TextView DriverName,DriverPhone,DriverCar;

    private RadioGroup DriverRadioGroup;
    private String destination;
    private RatingBar mRatingBar;

    private EditText serviceDescription;
    private RadioGroup serviceRadioGroup;
    private RadioButton batteryRadioButton,tyreRadioButton,towingRadioButton;


    //private Button btnService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers_map);

        //searchView = findViewById(R.id.sv_location);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        String apiKey = getString(R.string.api_key);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

// Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);


       /* searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if(location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(CustomersMapActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });*/
        //  mapFragment.getMapAsync(this);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mapFragment.getMapAsync(this);


        destinationLatLng = new LatLng(0.0,0.0);




        back= (Button) findViewById(R.id.back);
        NearbyPlace=(Button) findViewById(R.id.btnWorkshop);
        //CustomerLogoutButton = (Button) findViewById(R.id.customer_logout_btn);
        CallCabCarButton = (Button) findViewById(R.id.customer_call_cab_btn);
        //ServiceDescription = (Button) findViewById(R.id.service_description);

       // btnOK = (Button) findViewById(R.id.btnOk);
       // serviceRadioGroup= (RadioGroup) findViewById(R.id.serviceRadioGroup);
       // batteryRadioButton = (RadioButton)findViewById(R.id.batteryRadioButton);
        //tyreRadioButton = (RadioButton)findViewById(R.id.tyreRadioButton);
       // towingRadioButton = (RadioButton)findViewById(R.id.towingRadioButton);
      //  serviceDescription = (EditText)findViewById(R.id.service_description);

        //CustomerSettingButton = (Button) findViewById(R.id.customer_settings_btn);
       // CustomerHistoryButton = (Button) findViewById(R.id.customer_history_btn);


        DriverRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
       DriverRadioGroup.check(R.id.battery);

        DriverInfo = (LinearLayout) findViewById(R.id.driver_info);
        DriverProfileImage = (ImageView) findViewById(R.id.driver_profileImage);
        DriverName = (TextView) findViewById(R.id.driver_name);
        DriverPhone = (TextView) findViewById(R.id.driver_phone);
        DriverCar = (TextView) findViewById(R.id.driver_car);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        //.findFragmentById(R.id.map);

/*
        CustomerLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mAuth.signOut();
                LogOutCustomer();
            }
        });*/
        CallCabCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(requestBol)
                {

                    endRide();
                    Intent intent = new Intent(CustomersMapActivityBackupLatest.this,Pop.class);
                    startActivity(intent);
                }
                else{

                    showDialog();
                    int selectId = DriverRadioGroup.getCheckedRadioButtonId();
                    final RadioButton radioButton = (RadioButton) findViewById(selectId);

                    if(radioButton.getText() == null){
                        return;
                    }

                    requestService = radioButton.getText().toString();


                    //showDialog();
                    requestBol=true;

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Customers Requests");
                    DatabaseReference refService = FirebaseDatabase.getInstance().getReference().child("Customers Requests").child("Service");
                    refService.setValue(requestService);
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(userId,new GeoLocation(lastLocation.getLatitude(),lastLocation.getLongitude()));

                    CustomerPickUpLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(CustomerPickUpLocation).title("PickUp Customer From Here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));

                    CallCabCarButton.setText("Getting your Driver...");

                    GetClosestDriverCab();

                }


            }
        });
/*
        ServiceDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
*/
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(CustomersMapActivityBackupLatest.this,MainActivity.class);
                    startActivity(intent);
                    //return;


            }
        });

        NearbyPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomersMapActivityBackupLatest.this,MapsActivity.class);
                startActivity(intent);
            }
        });
/*
        CustomerSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomersMapActivity.this,CustomerSettingActivity.class);
                startActivity(intent);
                return;
            }
        });*/
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

         autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                destination = place.getName();
                destinationLatLng = place.getLatLng();

            }

            @Override
            public void onError(Status status) {


            }
        });
/*
        CustomerHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomersMapActivity.this, HistoryActivity.class);
                intent.putExtra("customerOrDriver" ,"Customers");
                startActivity(intent);
                return;
            }
        });
*/
    }

    private void showDialog() {
/*
        serviceRadioGroup= (RadioGroup) findViewById(R.id.serviceRadioGroup);
        batteryRadioButton = (RadioButton)findViewById(R.id.batteryRadioButton);
        tyreRadioButton = (RadioButton)findViewById(R.id.tyreRadioButton);

        towingRadioButton = (RadioButton)findViewById(R.id.towingRadioButton);
 */
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);


        //AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CustomersMapActivity.this,R.style.dialogTheme);
        dialog.setTitle("Service Description");
        dialog.setMessage("Provide the service description");

        Dialog customDialog = new Dialog(CustomersMapActivityBackupLatest.this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View ser_layout = inflater.inflate(R.layout.service_description,null);
        final EditText serviceDesc=ser_layout.findViewById(R.id.service_description);

        dialog.setView(ser_layout);
/*


        int selectId = serviceRadioGroup.getCheckedRadioButtonId();
        final RadioButton radioButton = (RadioButton) findViewById(selectId);

        if(radioButton.getText() == null){
            return;
        }
*/
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (TextUtils.isEmpty(serviceDesc.getText().toString())){
                    Toast.makeText(CustomersMapActivityBackupLatest.this, "Describe your problem ", Toast.LENGTH_SHORT).show();
                    return;
                }
                /*
                int selectId = serviceRadioGroup.getCheckedRadioButtonId();
                final RadioButton radioButton = (RadioButton) findViewById(selectId);

                if(radioButton.getText() == null){
                    return;
                }
                 */
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference serviceDescRef = database.getReference();
                serviceDescRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Object value = dataSnapshot.getValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(CustomersMapActivityBackupLatest.this,"Failed to read value",Toast.LENGTH_SHORT).show();
                    }

                });
                //String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                //DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Customers Requests");
                serviceDescRef.child("Customers Requests").child("service description").setValue(serviceDesc.getText().toString());
            }
        });

        dialog.show();
        /*

        final Dialog customDialog = new Dialog(CustomersMapActivity.this);
        customDialog.setContentView(R.layout.service_description);



        Window window =customDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        btnOK = (Button) customDialog.findViewById(R.id.btnOk);
        serviceRadioGroup= (RadioGroup) customDialog.findViewById(R.id.serviceRadioGroup);
        batteryRadioButton = (RadioButton)customDialog.findViewById(R.id.batteryRadioButton);
        tyreRadioButton = (RadioButton)customDialog.findViewById(R.id.tyreRadioButton);
        towingRadioButton = (RadioButton)customDialog.findViewById(R.id.towingRadioButton);
        serviceDescription = (EditText) customDialog.findViewById(R.id.service_description);

        int selectId = serviceRadioGroup.getCheckedRadioButtonId();
        final RadioButton radioButton = (RadioButton) findViewById(selectId);

        if(radioButton.getText() == null){
            return;
        }

        requestService = radioButton.getText().toString();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(serviceDescription.getText().toString())){
                    Toast.makeText(CustomersMapActivity.this, "Describe your problem ", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference serviceDescRef = database.getReference();
                serviceDescRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Object value = dataSnapshot.getValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(CustomersMapActivity.this,"Failed to read value",Toast.LENGTH_SHORT).show();

                    }

                });
                //String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                //DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Customers Requests");
                serviceDescRef.child("Customers Requests").child("service description").setValue(serviceDescription.getText().toString());
                customDialog.dismiss();
            }
        });
        customDialog.show();

         */
    }


    GeoQuery geoQuery;
    private void GetClosestDriverCab()
    {
        DriverAvailableRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
        GeoFire geoFire = new GeoFire(DriverAvailableRef);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(CustomerPickUpLocation.latitude,CustomerPickUpLocation.longitude),radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!driverFound && requestBol)
                {
                    DatabaseReference CustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(key);
                    CustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() >0){
                                Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();

                                if(driverFound){
                                    return;
                                }

                                if(driverMap.get("service").equals(requestService)){
                                    driverFound = true;
                                    driverFoundID = dataSnapshot.getKey();

                                    DriversRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("Customers Requests");
                                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    HashMap map = new HashMap();
                                    map.put("CustomerRideID",customerId);
                                    map.put("destination",destination);
                                    map.put("service",requestService);
                                    map.put("destinationLat",destinationLatLng.latitude);
                                    map.put("destinationLng",destinationLatLng.longitude);
                                    DriversRef.updateChildren(map);

                                    GettingDriverLocation();
                                    GetDriverInfo();
                                    getHasRideEnded();
                                    CallCabCarButton.setText("Looking for Driver Location");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {


            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

                if(!driverFound)
                {
                    radius = radius + 1;
                    GetClosestDriverCab();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }


    private void GetDriverInfo()
    {
        DriverInfo.setVisibility(View.VISIBLE);
        DatabaseReference CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID);
        CustomerDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name") !=null)
                    {

                        DriverName.setText(map.get("name").toString());
                    }
                    if(map.get("phone") !=null)
                    {

                        DriverPhone.setText(map.get("phone").toString());
                    }
                    if(map.get("car") !=null)
                    {

                        DriverCar.setText(map.get("car").toString());
                    }
                    if(map.get("profileimagesUrl") !=null)
                    {
                        Glide.with(getApplication()).load(map.get("profileimagesUrl").toString()).into(DriverProfileImage);
                    }
                    int ratingSum = 0;
                    float ratingsTotal = 0;
                    float ratingAvg=0;
                    for(DataSnapshot child: dataSnapshot.child("rating") .getChildren()){
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    if(ratingsTotal!=0){
                        ratingAvg = ratingSum/ratingsTotal;
                        mRatingBar.setRating(ratingAvg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private DatabaseReference DriverLocationRef;
    private ValueEventListener DriverLocationRefListener;
    private void GettingDriverLocation()
    {
        DriverLocationRef = FirebaseDatabase.getInstance().getReference().child("Drivers Working").child(driverFoundID).child("l");
        DriverLocationRefListener = DriverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && requestBol)
                {
                    List<Object> driverLocationMap = (List<Object>) dataSnapshot.getValue();
                    double LocationLat = 0;
                    double LocationLng = 0;
                    CallCabCarButton.setText("Mechanic Found");

                    if(driverLocationMap.get(0) != null)
                    {
                        LocationLat = Double.parseDouble(driverLocationMap.get(0).toString());
                    }
                    if(driverLocationMap.get(1) != null)
                    {
                        LocationLng = Double.parseDouble(driverLocationMap.get(1).toString());
                    }

                    LatLng DriverLatLng = new LatLng(LocationLat,LocationLng);
                    if(DriverMarker != null)
                    {
                        DriverMarker.remove();

                    }

                    Location location1 = new Location("");
                    location1.setLatitude(CustomerPickUpLocation.latitude);
                    location1.setLongitude(CustomerPickUpLocation.longitude);

                    Location location2 = new Location("");
                    location2.setLatitude(DriverLatLng.latitude);
                    location2.setLongitude(DriverLatLng.longitude);

                    float Distance = location1.distanceTo(location2);
                    if (Distance<100){
                        CallCabCarButton.setText("Mechanic's Here");
                    }else{
                        CallCabCarButton.setText("Mechanic Found ");
                    }
                    //CallCabCarButton.setText("Driver Found" + String.valueOf(Distance));


                    DriverMarker = mMap.addMarker(new MarkerOptions().position(DriverLatLng).title("Your Mechanic is HERE!!!").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;
    private void getHasRideEnded()
    {
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child("Drivers").child(driverFoundID).child("Customers Requests").child("CustomerRideID");

        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {

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

    private void endRide(){
        requestBol = false;
        geoQuery.removeAllListeners();
        DriverLocationRef.removeEventListener(DriverLocationRefListener);
        driveHasEndedRef.removeEventListener(driveHasEndedRefListener);


        DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference().child("Customers Requests").child("Service");
        serviceRef.removeValue();

        DatabaseReference serviceDescRef = FirebaseDatabase.getInstance().getReference().child("Customers Requests").child("service description");
        serviceDescRef.removeValue();

        if(driverFoundID != null)
        {
            DriversRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("Customers Requests");
            DriversRef.removeValue();
            driverFoundID = null;
        }
        driverFound = false;
        radius=1;
        String customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Customers Requests");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(customerID);
        if(pickupMarker !=null && DriverMarker !=null)
        {
            pickupMarker.remove();
            DriverMarker.remove();
        }
        CallCabCarButton.setText("Call Mechanic");

        DriverInfo.setVisibility(View.GONE);
        DriverName.setText("");
        DriverPhone.setText("");
        DriverCar.setText("");

        DriverProfileImage.setImageResource(R.mipmap.ic_default_user);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            return;
        }
        buildGoogleApiClient();
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);

    }


    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations()){
                if(getApplicationContext()!=null){
                    lastLocation = location;

                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                    if(!getDriversAroundStarted)
                        getDriversAround();

                }
            }
        }

    };



    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        // if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION))
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);

    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        lastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

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

    protected void onStop()
    {
        super.onStop();
    }


    private void LogOutCustomer()
    {
        Intent welcomeIntent = new Intent(CustomersMapActivityBackupLatest.this,WelcomeActivity.class);
        welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(welcomeIntent);
        finish();
    }


    boolean getDriversAroundStarted = false;
    List<Marker> markers = new ArrayList<Marker>();
    private void getDriversAround(){
        getDriversAroundStarted = true;
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
        GeoFire geoFire = new GeoFire(driverLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lastLocation.getLongitude(), lastLocation.getLatitude()), 999999999);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                for(Marker markerIt :markers){
                    if(markerIt.getTag().equals(key))
                        return;
                }
                LatLng driverLocation = new LatLng(location.latitude,location.longitude);
                Marker mMechanicMarker = mMap.addMarker(new MarkerOptions().position(driverLocation).title(key).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
                mMechanicMarker.setTag(key);

                markers.add(mMechanicMarker);
            }

            @Override
            public void onKeyExited(String key) {
                for(Marker markerIt :markers){
                    if(markerIt.getTag().equals(key))
                        markerIt.remove();

                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

                for(Marker markerIt :markers){
                    if(markerIt.getTag().equals(key))
                       markerIt.setPosition(new LatLng(location.latitude,location.longitude));
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
/*
    public void showpopup(View view) {
        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.setOnMenuItemClickListener(this);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu,popupMenu.getMenu());
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_move:
                displayMessage("Move to option selected");
                return true;

            case R.id.action_label:
                displayMessage("Change label option selected");
                return true;
                default:
                    return false;
        }

    }

    private void displayMessage(String message){
        Snackbar.make(findViewById(R.id.rootView),message,Snackbar.LENGTH_SHORT).show();
    }*/
}
