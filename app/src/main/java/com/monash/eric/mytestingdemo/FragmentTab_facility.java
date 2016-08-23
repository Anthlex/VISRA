package com.monash.eric.mytestingdemo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class FragmentTab_facility extends ListFragment {

    public ArrayList<Integer> facIdList = null;
    public ArrayList<String> facilityList = null;

    LocationManager locationManager;
    LocationListener locationListener;
    private static final String TAG = "Debug";


    public HashMap<Integer, String> hashmap = null;
    ArrayAdapter<String> adapter;
    JSONObject jObject = null;
    JSONArray jArray = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(getActivity().getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i("permission","deny");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_layout_facilities, null);
//        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,names);
//
//        setListAdapter(adapter);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //((FragmentActivity)getActivity()).getActionBar().setDisplayHomeAsUpEnabled(true);
        Button button = (Button) view.findViewById(R.id.button2_fragmentfac);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //            Log.d("MyApp","I am here");
                SearchFacility();

            }
        });


        return view;
    }

    public void SearchFacility() {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivityForResult(new Intent(getActivity(), SearchActivity.class), 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG,resultCode+"");

        if(resultCode == Activity.RESULT_OK) {
            String JsonResult = data.getStringExtra("result");
            //   Log.i("rESUKT", JsonResult);
            updateList(JsonResult);
        }

    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {


        if (ActivityCompat.checkSelfPermission(getActivity().getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

        int facilityid = facIdList.get(position);
        Intent intent = new Intent(getActivity(), MapsActivity.class);

        for (int i = 0; i < jArray.length(); i++) {

            try {
                jObject = jArray.getJSONObject(i);
                if(facilityid == jObject.getInt("ViSRA_ID") )
                {
                    //FieldSurfaceType
                    Log.i("match",facilityid + "");
                    Log.i("match",jObject.getString("FacilityName") + "");
                    intent.putExtra("SportsPlayed",jObject.getString("SportsPlayed"));
                    intent.putExtra("FacilityName",jObject.getString("FacilityName"));
                    intent.putExtra("Longitude",jObject.getDouble("X"));
                    intent.putExtra("Latitude",jObject.getDouble("Y"));
                    Log.i("age",jObject.getInt("FacilityAge") + " sdasd" );
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

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

            Toast.makeText(
                    getActivity().getBaseContext(),
                    "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                            + loc.getLongitude(), Toast.LENGTH_LONG).show();
            String longitude = "Longitude: " + loc.getLongitude();
            Log.i("opopopo", longitude);
            String latitude = "Latitude: " + loc.getLatitude();
            Log.i("opopopo", latitude);

        /*------- To get city name from coordinates -------- */
            String cityName = null;
            Geocoder gcd = new Geocoder(getActivity().getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                    + cityName;
            //editLocation.setText(s);
            Log.i(TAG,s);
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    public void updateList(String JsonString)
    {
        if(!JsonString.equals("")) {


            facIdList = new ArrayList<>();
            facilityList = new ArrayList<>();


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
                    //System.out.println(i + " id : " + jObj.getInt("ViSRA_ID"));
                   // System.out.println(i + " att1 : " + jObj.getString("FacilityName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, facilityList);

            setListAdapter(adapter);
        }
        else
        {
            facilityList.clear();
            adapter.notifyDataSetChanged();

            Toast.makeText(getContext(),"No Result",Toast.LENGTH_SHORT).show();
        }

    }












}