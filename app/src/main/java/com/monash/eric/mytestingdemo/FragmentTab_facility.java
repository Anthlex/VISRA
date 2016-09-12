package com.monash.eric.mytestingdemo;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.Tag;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;


public class FragmentTab_facility extends ListFragment {

    //Google GeoCoding URL Setitings
    private static final String GEO_BASE_URI = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";

    private static final String API_KEY = "AIzaSyCxzmhZsWml6UQUqK_ss8aPvPzBk1u-YrU";

    private static final String TAG = "FragmentTab_facility";

    //url for backend connection
    private static final String BASE_URI = "http://visra9535.cloudapp.net/api/searchall";
    //url for finding playedsports by facilityname
    private static final String URI_FIND_PLAYEDSPORTS = "http://visra9535.cloudapp.net/api/searchByName";


    private Button newSearch;
    private Button showNearby;

    //A list stores the facilities IDs
    ArrayList<Integer> facIdList = null;

    //A list stores the facilities names
    ArrayList<String> facilityList = null;

    //A list stores all the playedsports in a certain facility
    ArrayList<String> playedSportsList = null;
    ArrayAdapter<String> adapter;
    JSONObject jObject = null;
    JSONArray jArray = null;


    //variables for coordinates
    double curr_longtitude_from_main;
    double curr_latitude_from_main;

    ///////tesing inter comm
    OnHeadlineSelectedListener mCallback;


    private String selected_facility;
    private String suburb;
    String temp;

    Intent intentforMap;

    private ProgressDialog progressDialog;


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
        Log.d(TAG, "FragmentTab_facility on create ");


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

        showNearby = (Button) view.findViewById(R.id.button2_showNearby);
        showNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"shownearbyClicked");
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                curr_longtitude_from_main = mCallback.getLongtitude();
                curr_latitude_from_main = mCallback.getLatiitude();

                CallGeoWS callGeoWS = new CallGeoWS();
                callGeoWS.execute(curr_longtitude_from_main, curr_latitude_from_main);


            }
        });

        newSearch = (Button) view.findViewById(R.id.button2_fragmentfac);
        newSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "facility page on create view ON CLICK");
                curr_longtitude_from_main = mCallback.getLongtitude();
                curr_latitude_from_main = mCallback.getLatiitude();

                Log.d(TAG, curr_longtitude_from_main + " abc " + curr_latitude_from_main);


                SearchFacility();

            }
        });


        Log.d(TAG, "facility page on create view");

        return view;
    }

    public void SearchFacility() {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        intent.putExtra("lng", curr_longtitude_from_main);
        intent.putExtra("lat", curr_latitude_from_main);
        startActivityForResult(intent, 1);
    }


    //receive the Json Response
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            String JsonResult = data.getStringExtra("result");
            Log.d(TAG,JsonResult);
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


        intentforMap = new Intent(getActivity(), MapsActivity.class);
        playedSportsList = new ArrayList<>();
        int facilityid = facIdList.get(position);

        for (int i = 0; i < jArray.length(); i++) {

            try {
                jObject = jArray.getJSONObject(i);
                if (facilityid == jObject.getInt("ViSRA_ID")) {

                    selected_facility = jObject.getString("FacilityName");
                    intentforMap.putExtra("FacilityName", jObject.getString("FacilityName"));
                    intentforMap.putExtra("Longitude", jObject.getDouble("X"));
                    intentforMap.putExtra("Latitude", jObject.getDouble("Y"));
                    intentforMap.putExtra("FacilityAge", jObject.getInt("FacilityAge"));
                    intentforMap.putExtra("FieldSurfaceType", jObject.getString("FieldSurfaceType"));
                    intentforMap.putExtra("Changerooms", jObject.getInt("Changerooms"));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        executeFindPlayedSport();


    }

    //display the result from JSON response
    public void updateList(String JsonString) {
        facIdList = new ArrayList<>();
        facilityList = new ArrayList<>();
        if (!JsonString.equals("")) {



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
        else {
            facilityList.clear();

            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, facilityList);

            setListAdapter(adapter);

            adapter.notifyDataSetChanged();



            Toast.makeText(getContext(), "No facilities found for your search criteria", Toast.LENGTH_SHORT).show();
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


    protected String callSearchSportsWS(String subrubs) {
        HashMap<String, String> postDataParams = new HashMap<>();
        postDataParams.put("suburbs",subrubs);
        URL url;
        String response = "";
        try {
            url = new URL(BASE_URI);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;

    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        Log.d(TAG,result.toString());

        return result.toString();
    }


    private String callGeoWS(double lng, double lat) {

        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";

        try {
            StringBuilder sb = new StringBuilder();
            sb.append(GEO_BASE_URI);
            sb.append(lng);
            sb.append(",");
            sb.append(lat);
            sb.append("&key=");
            sb.append(API_KEY);


            // Gson gson = new Gson();
            //convert course entity to string json by calling toJson method
            //   String stringRegistrationJson = gson.toJson(registration);
            //  Log.i("EricTestRegJSON", stringRegistrationJson);
//            url = new URL ("https://maps.googleapis.com/maps/api/geocode/json?address=" +
//                    URLEncoder.encode(address + " Australia ", "utf8")+"&region=au"+"&key="+APIKEY);

            Log.d(TAG, sb.toString());

            url = new URL(sb.toString());

            conn = (HttpURLConnection) url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to POST
            conn.setRequestMethod("GET");
            //set the output to true
            conn.setDoOutput(true);
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return textResult;
    }


    private class CallGeoWS extends AsyncTask<Double, Void, String> {

        @Override
        protected String doInBackground(Double... params) {

            return callGeoWS(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            getSuburbFromJson(result);
            Log.i(TAG, result);
            executeSerachSportAPI();
        }
    }

    private String getSuburbFromJson(String originalJson) {


        Gson gson = new Gson();
        GeoResponse geoResponse = gson.fromJson(originalJson, GeoResponse.class);

        GeoResponse.address_component[] element = geoResponse.results[0].address_components;


        for (int i = 0; i < element.length; i++) {

            if (element[i].types[0].equals("locality")) {
                suburb = element[i].long_name;
                Log.d(TAG, suburb);

            }
        }

        return null;
    }

    private class CallSearchSprostAPI extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            return callSearchSportsWS(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {


            updateList(s);
            progressDialog.dismiss();

        }


    }


    private class CallSearchSprostAPIFindSports extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            return callFindSportsListWS(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONArray jArraySports = new JSONArray(s);
                for(int i = 0 ; i<jArraySports.length();i++)
                {
                    String playedsportsytemp = jArraySports.getJSONObject(i).getString("SportsPlayed");
                    Log.d(TAG,playedsportsytemp);
                    playedSportsList.add(playedsportsytemp);
                }
                intentforMap.putExtra("sportPlayedList",playedSportsList);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            startActivity(intentforMap);

            //updateList(s);

        }


    }


    protected String callFindSportsListWS(String facility_name) {
        HashMap<String, String> postDataParams = new HashMap<>();
        postDataParams.put("facility_name",facility_name);
        URL url;
        String response = "";
        try {
            url = new URL(URI_FIND_PLAYEDSPORTS);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG,response);
        return response;

    }

    public void executeSerachSportAPI()
    {
        CallSearchSprostAPI callSearchSprostAPI = new CallSearchSprostAPI();
        callSearchSprostAPI.execute(suburb);
    }

    public void executeFindPlayedSport()
    {
        CallSearchSprostAPIFindSports callSearchSprostAPIFindSports = new CallSearchSprostAPIFindSports();
        callSearchSprostAPIFindSports.execute(selected_facility);
    }

    public void populateSportsListFromJson(String jsonResponse)
    {

    }
}