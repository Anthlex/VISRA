package com.monash.eric.mytestingdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.monash.eric.mytestingdemo.Entity.Event;

import java.util.Map;

public class MyEventDetailActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private TextView textViewName;
    private TextView textViewDate;
    private TextView textViewTime;
    private TextView textViewVenue;
    private TextView textViewSport;
    private TextView textViewDesc;
    private TextView textViewHost;
    private Button back_btn;

    private ProgressDialog progressDialog;


    private Firebase mRef;

    private String uid;

    private String venue;

    private double latitude;
    private double longtitude;


    private FirebaseAuth firebaseAuth;
    private Firebase mRootRef;

    private String curr_uid;


    SharedPreferences sharedPreferences;
    String Username;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event_detail);



        Firebase.setAndroidContext(this);
        firebaseAuth = FirebaseAuth.getInstance();

        curr_uid = firebaseAuth.getCurrentUser().getUid();


        sharedPreferences = getSharedPreferences("userProfile",MODE_PRIVATE);
        Username = sharedPreferences.getString("Username","n/a");


        Log.d("EVENTDETAILS",Username);
        Log.d("EVENTDETAILS",curr_uid);

        textViewDate = (TextView)findViewById(R.id.textViewDate);
        textViewName = (TextView)findViewById(R.id.textViewName);
        textViewTime = (TextView)findViewById(R.id.textViewTime);
        textViewVenue = (TextView)findViewById(R.id.textViewVenue);
        textViewSport = (TextView)findViewById(R.id.textViewSports);
        textViewDesc = (TextView)findViewById(R.id.textViewDesc);
        textViewHost = (TextView)findViewById(R.id.textViewHost);
        back_btn = (Button)findViewById(R.id.back_btn_eventdetial);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent i = new Intent(view.getContext(),MainActivity.class);
//                startActivity(i);
                onBackPressed();
            }
        });

        Intent i = getIntent();
        Event event = (Event) i.getSerializableExtra("eventObj");

        String la = event.getLatitute();
        String lo = event.getLongtitue();


        latitude = Double.parseDouble(la);
        longtitude = Double.parseDouble(lo);



        //Hostid
        uid = event.getHostId();
        venue = event.getVenue();

        textViewDate.setText(event.getDate());
        textViewSport.setText(event.getSport());
        textViewDesc.setText(event.getDescription());
        textViewName.setText(event.getTitle());
        textViewTime.setText(event.getTime());
        textViewVenue.setText(event.getVenue());



        mRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        mRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,String> map = dataSnapshot.getValue(Map.class);
                String username = map.get("Username");
                textViewHost.setText(username);


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.setMinZoomPreference(12.0f);
        mMap.setMaxZoomPreference(20.0f);

        // Add a marker to a location and  move the camera
        LatLng facility = new LatLng(latitude, longtitude);
        mMap.addMarker(new MarkerOptions().position(facility).title(venue));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longtitude),14.0f));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
