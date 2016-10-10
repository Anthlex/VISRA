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

public class InviteDetailActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private TextView textViewName;
    private TextView textViewDate;
    private TextView textViewTime;
    private TextView textViewVenue;
    private TextView textViewSport;
    private TextView textViewDesc;
    private TextView textViewHost;
    private Button buttonAccept;
    private Button buttonReject;

    private ProgressDialog progressDialog;


    private Firebase mRef;

    private String uid;

    private String venue;

    private double latitude;
    private double longtitude;


    private FirebaseAuth firebaseAuth;
    private Firebase mRootRef;

    private String curr_uid;
    private String eventid;


    SharedPreferences sharedPreferences;
    String Username;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_detail);



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

        buttonAccept = (Button)findViewById(R.id.buttonAccept);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Accept();
            }
        });

        buttonReject = (Button)findViewById(R.id.buttonReject);
        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Reject();

            }
        });

        Intent i = getIntent();
        Event event = (Event) i.getSerializableExtra("eventObj");
        eventid = i.getStringExtra("selectID");

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

    private void Reject() {

        mRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        Intent i = getIntent();
        Event event = (Event) i.getSerializableExtra("eventObj");

        Firebase childRef = mRef.child(curr_uid);

        Firebase grandChildRe = childRef.child("Event");
        Firebase grandChildRef = grandChildRe.child(eventid);

        grandChildRef.removeValue();

        onBackPressed();



    }

    private void Accept() {

        mRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        Intent i = getIntent();
        Event event = (Event) i.getSerializableExtra("eventObj");

        Firebase childRef = mRef.child(curr_uid);

        Firebase grandChildRe = childRef.child("Event");
        Firebase grandChildRef = grandChildRe.child(eventid);
        Firebase grandChildRef1 = grandChildRef.child("Title");
        grandChildRef1.setValue(event.getTitle());
        Firebase grandChildRef2 = grandChildRef.child("Venue");
        grandChildRef2.setValue(event.getVenue());
        Firebase grandChildRef3 = grandChildRef.child("Date");
        grandChildRef3.setValue(event.getDate());
        Firebase grandChildRef4 = grandChildRef.child("Time");
        grandChildRef4.setValue(event.getTime());
        Firebase grandChildRef5 = grandChildRef.child("Sport");
        grandChildRef5.setValue(event.getSport());
        Firebase grandChildRef6 = grandChildRef.child("Desc");
        grandChildRef6.setValue(event.getDescription());
        Firebase grandChildRef7 = grandChildRef.child("Host");
        grandChildRef7.setValue(event.getHostId());
        Firebase grandChildRef8 = grandChildRef.child("Latitude");
        grandChildRef8.setValue(event.getLatitute());
        Firebase grandChildRef9 = grandChildRef.child("Longtitue");
        grandChildRef9.setValue(event.getLongtitue());
        Firebase grandChildRef10 = grandChildRef.child("Status");
        grandChildRef10.setValue("joined");

        startActivity(new Intent(InviteDetailActivity.this, MainActivity.class));



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
