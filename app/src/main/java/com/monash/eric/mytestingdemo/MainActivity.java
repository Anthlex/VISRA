package com.monash.eric.mytestingdemo;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener,FragmentTab_facility.OnHeadlineSelectedListener {


    private static final String TAG = "MainActivity";


    //Google GeoCoding URL Setitings
    private static final String GEO_BASE_URI = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";

    private static final String API_KEY = "AIzaSyCxzmhZsWml6UQUqK_ss8aPvPzBk1u-YrU";


    //variables for coordinates
    public double curr_longtitude;
    public double curr_latitude;

    //get current location
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters



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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set up view
        setContentView(R.layout.activity_main);

        Log.d(TAG,"MainActivity on create ");

        initView();

        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();

            createLocationRequest();
        }


    }

    //initialize view
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


    //set up tabhost component
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
        Log.d(TAG,"MainActivity on start ");

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPlayServices();
        Log.d(TAG,"MainActivity on resume ");

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"MainActivity on pause ");

        //stopLocationUpdates();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

        Toast.makeText(this, "Location changed!",
                Toast.LENGTH_SHORT).show();

        // Displaying the new location on UI
//        displayLocation();
//
//        Bundle bundle = new Bundle();
//        bundle.putDouble("lng", curr_longtitude);
//        bundle.putDouble("lat", curr_latitude);

        // set Fragmentclass Arguments


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
                //terminate app
                //getActivity().finish();
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
        Log.d(TAG,"buildclient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to display the location on UI
     * */
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
            curr_longtitude = mLastLocation.getLatitude();
            curr_latitude = mLastLocation.getLongitude();
            Log.d(TAG,"displayed log and lat");

            Log.d(TAG,curr_longtitude + ", " + curr_latitude);

        } else {

            Log.d(TAG,"(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }


    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {

        Log.d(TAG,"Starting updates");
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


//    @Override
//    public void saysomething() {
//
//
//        Log.d(TAG,"say sth");
//        FragmentTab_facility fragobj = new FragmentTab_facility();
//
//        Bundle args = new Bundle();
//        args.putString("hello", "mao world");
//        fragobj.setArguments(args);
//    }
}



