package com.monash.eric.mytestingdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

/**
 * EventDetialActivity display all the event details.
 * User can participate in any of them if they'er interested in.
 * The events a user have join will be added into a user's Myevent list
 *
 * @author Eric
 * @since 1.0
 */
public class EventDetailActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    //view components
    private TextView textViewName;
    private TextView textViewDate;
    private TextView textViewTime;
    private TextView textViewVenue;
    private TextView textViewSport;
    private TextView textViewDesc;
    private TextView textViewHost;
    private Button back_btn;
    private Button join_btn;

    //firebase user root
    private Firebase mRef;

    //event host id
    private String uid;

    //venue name
    private String venue;

    //define variables to store coordinates
    private double latitude;
    private double longtitude;


    private FirebaseAuth firebaseAuth;

    //firebase event root
    private Firebase mRootRef;

    //current user id
    private String curr_uid;
    //selected event id
    private String selectedEventId;


    SharedPreferences sharedPreferences;
    //current user name
    String Username;

    //indicator for joining a event
    boolean joined = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Firebase.setAndroidContext(this);
        firebaseAuth = FirebaseAuth.getInstance();

        //get the data passed from fragment_event
        Intent i = getIntent();
        Event event = (Event) i.getSerializableExtra("eventObj");
        //get passed selected ID
        selectedEventId = i.getStringExtra("selectID");

        //get current user's name
        sharedPreferences = getSharedPreferences("userProfile", MODE_PRIVATE);
        Username = sharedPreferences.getString("Username", "n/a");


        //check if the user has already join the event.
        if(firebaseAuth.getCurrentUser() != null){
            curr_uid = firebaseAuth.getCurrentUser().getUid();
            isJoin(selectedEventId);
        }

        //initial view components
        initComponents();

        //get the coordinates from event object
        String la = event.getLatitute();
        String lo = event.getLongtitue();

        //convert string to double
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
                Map<String, String> map = dataSnapshot.getValue(Map.class);
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longtitude), 14.0f));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }


    /**
     * This method is used to join a selected event by a user
     * @param uid user id
     * @param selectedeventIdPara select event id
     */
    private void joinEvent(final String uid, String selectedeventIdPara) {
        mRootRef = new Firebase("https://visra-1d74b.firebaseio.com/Event");

        Firebase selectedeventNode = mRootRef.child(selectedeventIdPara);

        Firebase participantNode = selectedeventNode.child("Participant");

        HashMap<String, String> participantMap = new HashMap<>();
        participantMap.put("Userid", curr_uid);
        participantMap.put("Username", Username);
        participantMap.put("Status","1");

        participantNode.child(curr_uid).setValue(participantMap);


        mRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        Intent i = getIntent();
        Event event = (Event) i.getSerializableExtra("eventObj");

        Firebase childRef = mRef.child(curr_uid);

        Firebase grandChildRe = childRef.child("Event");
        Firebase grandChildRef = grandChildRe.push();
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
    }


    // a method to check weather the user has joined the event
    private void isJoin(String eventid) {

        Log.d("EVENTDETAILS", "----");

        // if(isJoin(uid))
        mRootRef = new Firebase("https://visra-1d74b.firebaseio.com/Event");

        Firebase selectedEvent = mRootRef.child(eventid);

        Firebase participant = selectedEvent.child("Participant");

        participant.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    HashMap<String, String> participantMap = (HashMap<String, String>) child.getValue();

                    if (participantMap.get("Userid").equals(curr_uid)) {

                        join_btn.setText("Joined");
                        join_btn.setEnabled(false);
                        join_btn.setBackgroundColor(Color.LTGRAY);
                        join_btn.setTextColor(Color.WHITE);
                        joined = true;
                    }


                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //return flag;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void initComponents()
    {
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        textViewVenue = (TextView) findViewById(R.id.textViewVenue);
        textViewSport = (TextView) findViewById(R.id.textViewSports);
        textViewDesc = (TextView) findViewById(R.id.textViewDesc);
        textViewHost = (TextView) findViewById(R.id.textViewHost);
        back_btn = (Button) findViewById(R.id.back_btn_eventdetial);
        join_btn = (Button) findViewById(R.id.join_btn_eventdetial);


        //set listener for back button
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //set listener for join button
        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(view.getContext(), LoginActivity.class);
                    Toast.makeText(view.getContext(), "You need to login first!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                } else {

                    curr_uid = firebaseAuth.getCurrentUser().getUid();

                    joinEvent(curr_uid, selectedEventId);
                    join_btn.setText("Joined");
                    join_btn.setEnabled(false);
                }
            }
        });

    }

}


