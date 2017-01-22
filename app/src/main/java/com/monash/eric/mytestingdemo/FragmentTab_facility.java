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

/**
 * FragmentTab_facility is used to display the user's search result
 * User can click on a particular facility for further information
 *
 * @author   Eric
 * @since  1.0
 */
public class FragmentTab_facility extends ListFragment {

    //Google GeoCoding URL Setitings
    private static final String GEO_BASE_URI = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    //Google API KRY
    private static final String API_KEY = "AIzaSyCxzmhZsWml6UQUqK_ss8aPvPzBk1u-YrU";
    //Debug tag
    private static final String TAG = "FragmentTab_facility";
    //url for backend connection
    private static final String BASE_URI = "http://visra9535.cloudapp.net/api/searchall";
    //url for finding playedsports by facilityname
    private static final String URI_FIND_PLAYEDSPORTS = "http://visra9535.cloudapp.net/api/searchByName";


    //view components
    private Button newSearch;
    private Button showNearby;

    //A list stores the facilities IDs
    ArrayList<Integer> facIdList = null;

    //A list stores the facilities names
    ArrayList<String> facilityList = null;

    //A list stores all the playedsports in a certain facility
    ArrayList<String> playedSportsList = null;
    ArrayAdapter<String> adapter;
    //Define a variable to store a Json Object
    JSONObject jObject = null;
    //Define a variable to store a Json Array
    JSONArray jArray = null;

    //variables for coordinates
    double curr_longtitude_from_main ;
    double curr_latitude_from_main ;

    //interface used for communication between MainActivity and FragmentTab_facility
    OnHeadlineSelectedListener mCallback;


    //define a variable to store the name of a facility
    private String selected_facility;
    //define a varibale to store the name of the suburb
    private String suburb;
    //define a Intent object
    Intent intentforMap;

    //define a progressDialog
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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_layout_facilities, null);




        showNearby = (Button) view.findViewById(R.id.button2_showNearby);
        showNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                curr_longtitude_from_main = mCallback.getLongtitude();
                curr_latitude_from_main = mCallback.getLatiitude();

                Log.d(TAG,"CLICKEDSHOWED");
                Log.d(TAG,"curr_longtitude_from_main  "+curr_longtitude_from_main);
                Log.d(TAG,"curr_longtitude_from_main  "+curr_latitude_from_main);


                CallGeoWS callGeoWS = new CallGeoWS();
                callGeoWS.execute(curr_longtitude_from_main, curr_latitude_from_main);


            }
        });

        newSearch = (Button) view.findViewById(R.id.button2_fragmentfac);
        newSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                curr_longtitude_from_main = mCallback.getLongtitude();
                curr_latitude_from_main = mCallback.getLatiitude();



                SearchFacility();

            }
        });

        return view;
    }

    /**
     *     this method is for a user to perform new search operation
     */
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

    /**
     * This method is used to display the result from JSON response as a list
     * @param JsonString
     */
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
    public void onResume() {
        super.onResume();

        curr_longtitude_from_main = mCallback.getLongtitude();
        curr_latitude_from_main = mCallback.getLatiitude();
    }


    /**
     * This method is used to find facilities base on the suburb
     * @param subrubs
     * @return
     */
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

    /**
     * This method is used to append parameters for web service query using POST
     * @param params for POST query
     * @return the completed query string
     * @throws UnsupportedEncodingException
     */
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

        return result.toString();
    }


    /**
     * This method will call Google Place API with the current longitude and latitude
     * and return a Json output with details of that location
     *
     * @param lng the current longitude of the user
     * @param lat the current latitude of the user
     * @return the JSON String provided by Google Place API
     */
    private String callGeoWS(double lng, double lat) {

        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";

        try {
            StringBuilder sb = new StringBuilder();
            sb.append(GEO_BASE_URI);
            sb.append(lat);
            sb.append(",");
            sb.append(lng);
            sb.append("&key=");
            sb.append(API_KEY);


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


    /**
     *  This AsyncTask is used to call google place API.
     */
    private class CallGeoWS extends AsyncTask<Double, Void, String> {

        @Override
        protected String doInBackground(Double... params) {

            return callGeoWS(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            suburb = getSuburbFromJson(result);
            executeSerachSportAPI();
        }
    }

    /**
     * This method will retrieve the subsurb from the Json output
     * @param originalJson the Json data returned by Google place API
     * @return the name of the suburb, if no suburb found then return null
     */

    private String getSuburbFromJson(String originalJson) {

        String targetSuburb = null;

        Gson gson = new Gson();
        GeoResponse geoResponse = gson.fromJson(originalJson, GeoResponse.class);

        if(geoResponse.results.length>0) {
            GeoResponse.address_component[] element = geoResponse.results[0].address_components;


            for (int i = 0; i < element.length; i++) {

                if (element[i].types[0].equals("locality")) {
                    //assign the name of the suburb to the varibale
                    targetSuburb = element[i].long_name;
                    break;
                }
            }
        }

        return targetSuburb;

    }

    /**
     * This AsyncTask is used to call facility search web service with provided suburb
     * It returns a list of facilities after calling callSearchSportsWS.
     */
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

    /**
     * This AsyncTask is used to call Sport search with a provided facility
     *
     */
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
                    if(i==0) {
                        String playedsportsytemp = jArraySports.getJSONObject(i).getString("SportsPlayed");
                       // Log.d(TAG, playedsportsytemp);
                        playedSportsList.add(playedsportsytemp);
                    }
                    else{
                        String playedsportsytemp = " " +jArraySports.getJSONObject(i).getString("SportsPlayed");
                        playedSportsList.add(playedsportsytemp);
                    }
                }
                intentforMap.putExtra("sportPlayedList",playedSportsList);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            startActivity(intentforMap);

        }


    }

    /**
     * This method is used to find sports in a facility
     * @param facility_name the name of the facility
     * @return the json output
     */

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

}