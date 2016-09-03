package com.monash.eric.mytestingdemo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;


public class FragmentTab_facility extends ListFragment {


    private static final String TAG = "FragmentTab_facility aa";

    //A list stores the facilities IDs
    ArrayList<Integer> facIdList = null;

    //A list stores the facilities names
    ArrayList<String> facilityList = null;
    ArrayAdapter<String> adapter;
    JSONObject jObject = null;
    JSONArray jArray = null;


    //variables for coordinates
    double curr_longtitude_from_main;
    double curr_latitude_from_main;

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


    ///////tesing inter comm
    OnHeadlineSelectedListener mCallback;

    String temp;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public double getLongtitude();

        public double getLatiitude();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d(TAG,"FragmentTab_facility on create ");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_layout_facilities, null);





//        curr_longtitude_from_main = getArguments().getDouble("lng");
//        curr_latitude_from_main = getArguments().getDouble("lat");
//
//


        //callGeoWS(curr_longtitude_from_main,curr_latitude_from_main)

        Button button = (Button) view.findViewById(R.id.button2_fragmentfac);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG,"facility page on create view ON CLICK");
                curr_longtitude_from_main = mCallback.getLongtitude();
                curr_latitude_from_main = mCallback.getLatiitude();

                Log.d(TAG,curr_longtitude_from_main + " abc " + curr_latitude_from_main);



                SearchFacility();

            }
        });




        Log.d(TAG,"facility page on create view");

        return view;
    }

    public void SearchFacility() {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        intent.putExtra("lng",curr_longtitude_from_main);
        intent.putExtra("lat",curr_latitude_from_main);
        startActivityForResult(intent, 1);
    }


    //receive the Json Response
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Activity.RESULT_OK) {
            String JsonResult = data.getStringExtra("result");
            updateList(JsonResult);
        }

    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    // send the data of a clicked item to map activity
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {


        int facilityid = facIdList.get(position);
        Intent intent = new Intent(getActivity(), MapsActivity.class);

        for (int i = 0; i < jArray.length(); i++) {

            try {
                jObject = jArray.getJSONObject(i);
                if(facilityid == jObject.getInt("ViSRA_ID") )
                {

                    intent.putExtra("SportsPlayed",jObject.getString("SportsPlayed"));
                    intent.putExtra("FacilityName",jObject.getString("FacilityName"));
                    intent.putExtra("Longitude",jObject.getDouble("X"));
                    intent.putExtra("Latitude",jObject.getDouble("Y"));
                    intent.putExtra("FacilityAge",jObject.getInt("FacilityAge"));
                    intent.putExtra("FieldSurfaceType",jObject.getString("FieldSurfaceType"));
                    intent.putExtra("Changerooms",jObject.getInt("Changerooms"));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        startActivity(intent);

    }

    //display the result from JSON response
    public void updateList(String JsonString)
    {
        if(!JsonString.equals("")) {

            facIdList = new ArrayList<>();
            facilityList = new ArrayList<>();

            //convert string to JSONArray
            try {
                jArray = new JSONArray(JsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObj = null;
                try {
                    jObj = jArray.getJSONObject(i);
                    facIdList.add(jObj.getInt("ViSRA_ID"));
                    facilityList.add(jObj.getString("FacilityName"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, facilityList);

            setListAdapter(adapter);
        }
        //No matching result, clear the list and pop up a message
        else
        {
            facilityList.clear();
            adapter.notifyDataSetChanged();

            Toast.makeText(getContext(),"No Result",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onStart() {
        super.onStart();


    }



    @Override
    public void onResume() {
        super.onResume();

        curr_longtitude_from_main = mCallback.getLongtitude();
        curr_latitude_from_main = mCallback.getLatiitude();
        Log.d(TAG,curr_longtitude_from_main + " egf " + curr_latitude_from_main);
        //  checkPlayServices();
//        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
//            startLocationUpdates();
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }



}