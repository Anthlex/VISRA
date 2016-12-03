package com.monash.eric.mytestingdemo;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

/**
 * MainActivity is the entry activity of the applications.
 * It holds four fragments for four sections(Facility, Pal, Event and User).
 *
 * It also keeps tracks of the current location of the user with google location service.
 *
 * @author  Eric
 * @since 1.0
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener,FragmentTab_facility.OnHeadlineSelectedListener {

    private static final String TAG = "MainActivity";
    //Google GeoCoding URL Setitings
    private static final String GEO_BASE_URI = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    //Google API KEY
    private static final String API_KEY = "AIzaSyCxzmhZsWml6UQUqK_ss8aPvPzBk1u-YrU";
    //variables for coordinates
    public double curr_longtitude;
    public double curr_latitude;
    //play service resolution request
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    //variable to store user Lost Know location
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    //variable for location request
    private LocationRequest mLocationRequest;
    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    //Firebase authentication variable
    private FirebaseAuth firebaseAuth;
    //Firebase User root
    private Firebase mRootRef;

    //Define a sharedpreference editor object
    SharedPreferences.Editor editor;

    //Current user id
    private String currUid;

    //Define a FragmentTabHost Object
    private FragmentTabHost mTabHost;

    //Define a LayoutInflater
    private LayoutInflater layoutInflater;

    //Define a array for all fragments
    private Class fragmentArray [] = {FragmentTab_facility.class,FragmentTab_event.class,FragmentTab_pal.class, FragmentTab_user.class};

    //Define a array of images for tabs
    private int imageArray[] = {R.drawable.tab_facilities_btn,R.drawable.tab_event_btn,R.drawable.tab_pal_btn,R.drawable.tab_user_btn};

    //Define a array of words for tabs
    private String textArray[] = {"Facilities","Events","Pals","Me"};

    //VauleListener
    ValueEventListener sportValueListener,updatesportListner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set up view
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null) {
            currUid = firebaseAuth.getCurrentUser().getUid();
            //get the user sports preference if the current user is not null
            if (currUid != null || !currUid.equals("")) {
                updateUserSportsPreference(currUid);
            }
        }


        initView();

        // First we need to check availability of play services
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }

    }

    /**
     *    initialize view, adding fragments in tabhost
     */
    private void initView()
    {
        layoutInflater = LayoutInflater.from(this);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realContent);

        int count = fragmentArray.length;

        for(int i = 0; i < count; i++){
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(textArray[i]).setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, fragmentArray[i], null);

        }

    }


    /**
     *     get view from tabhost accroding to the index
     *     @param index the index of the tab
     *     @return View
     */
    private View getTabItemView(int index){
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(imageArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(textArray[index]);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPlayServices();
            if(mGoogleApiClient != null) {
                if (mGoogleApiClient.isConnected()) {
                    startLocationUpdates();
                }
            }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
        displayLocation();


    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;
        curr_longtitude = location.getLongitude();
        curr_latitude = location.getLatitude();

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(this.getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
            }
            return false;
        }
        return true;
    }


    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to display the location on the view
     *
     */
    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            curr_longtitude = mLastLocation.getLongitude();
            curr_latitude = mLastLocation.getLatitude();


        } else {
            Log.d(TAG,"(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }


    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public double getLongtitude() {
        return curr_longtitude;
    }

    @Override
    public double getLatiitude() {
        return curr_latitude;
    }

    private void updateUserInterest(String uid)
    {

        SharedPreferences sharedPreferences = getSharedPreferences("userProfile",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mRootRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        Firebase usernode = mRootRef.child(uid);
        final Firebase sportnode = usernode.child("Sports");


        updatesportListner = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                editor.putString("interest",dataSnapshot.getValue().toString());
                editor.commit();
                if(updatesportListner != null )
                {
                    sportnode.removeEventListener(sportValueListener);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        sportnode.addValueEventListener(updatesportListner);

        if(updatesportListner != null )
        {
            sportnode.removeEventListener(sportValueListener);
        }

    }

    /**
     * This method will get the user sports preferences
     * @param uid current user's id
     */
    private void updateUserSportsPreference(final String uid)
    {

        final Firebase mroot = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        sportValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot child: dataSnapshot.getChildren())
                {
                    HashMap<String, String> userMap = (HashMap<String, String>) child.getValue();
                    if(userMap.containsKey("Sports"))
                    {
                        // sportIsEmptybool = false;
                        if(sportValueListener != null)
                        {
                            mroot.removeEventListener(sportValueListener);
                        }

                        updateUserInterest(uid);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        if(sportValueListener != null)
        {
            mroot.removeEventListener(sportValueListener);
        }

    }

    //disable back button
    @Override
    public void onBackPressed() {

    }

}



