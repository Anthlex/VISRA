package com.monash.eric.mytestingdemo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
//import com.monash.eric.mytestingdemo.WorkaroundMapFragment;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {


    public static final String TAG = "MapsActivity";
    //open weather map API KEYS AND URL
    private static final String WEATHER_API_BASE ="http://api.openweathermap.org/data/2.5/weather?";
    private static final String WEATHER_API_KEY ="0d9fffea542769114f53e78627a6e769";

    private static final String PLACE_API_BASE="https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String PLACE_API_KEY="&key=AIzaSyCxzmhZsWml6UQUqK_ss8aPvPzBk1u-YrU";

    private String placeId;
    private GoogleApiClient mGoogleApiClient;
    private String phoneNumber;
    private Button buttonCall;


    // variable for weather JSON response
    public JSONObject jsonObj_weather;
    public JSONObject jsonObj_detail;

    double longtitude;
    double latitude;

    //variable for values from search Activity
    String facility_name;
    String SportsPlayed;
    String FieldSurfaceType;
    int facility_age;
    String weather_con="";
    Double temp =-1110.0;
    int changeRoom_num;

    ArrayList<String> playedSportsList;

    ArrayList<String> changeroomType;


    private TextView temperature_tv;
    private TextView field_tv;
    private TextView facility_name_tv;
    private TextView weather_tv;
    private TextView changroom_tv;
    private Button createEventBtn;

    private GoogleMap mMap;
    private ScrollView mScrollView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        changeroomType = new ArrayList<>();
        changeroomType.add("Male");
        changeroomType.add("Female");
        changeroomType.add("UniSex");
        changeroomType.add("Umpire/Officials");
        changeroomType.add("Not Available");



        facility_name_tv = (TextView) findViewById(R.id.tv_mapact_facility_name);
        field_tv = (TextView)findViewById(R.id.field_type);
        weather_tv=(TextView)findViewById(R.id.weather_name);
        temperature_tv=(TextView)findViewById(R.id.temperature);
        changroom_tv = (TextView)findViewById(R.id.changeroom);
        createEventBtn=(Button)findViewById(R.id.create_event_btn);

        createEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),CreateEventActivity.class);
                intent.putExtra("facility_name",facility_name_tv.getText().toString());
                intent.putExtra("lng",longtitude);
                intent.putExtra("lat",latitude);
                intent.putExtra("sportList",playedSportsList);
                startActivity(intent);

            }
        });

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        mGoogleApiClient.connect();

        buttonCall = (Button) findViewById(R.id.call);


        // get values passed from Search Activity
        Intent intent = getIntent();
        longtitude = intent.getDoubleExtra("Longitude",0);
        latitude = intent.getDoubleExtra("Latitude",0);
        facility_name = intent.getStringExtra("FacilityName");
        FieldSurfaceType =intent.getStringExtra("FieldSurfaceType");
        playedSportsList = intent.getStringArrayListExtra("sportPlayedList");
        changeRoom_num = intent.getIntExtra("Changerooms",4);




        facility_name_tv.setText(facility_name);
        field_tv.setText(TextUtils.join(",",playedSportsList));
        weather_tv.setText("Not avilable now");
        temperature_tv.setText("Not avilable now");
        changroom_tv.setText(changeroomType.get(changeRoom_num));






        //call weather API
        CallWeatherAPIProcess callWeatherAPIProcess = new CallWeatherAPIProcess();
        callWeatherAPIProcess.execute(new String[]{latitude+"", longtitude+""});

        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (phoneNumber != null) {

                    Uri number = Uri.parse("tel:" + phoneNumber.substring(1));
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(callIntent);
                }
                else {Toast.makeText(MapsActivity.this,"This facility can not be booked currently", Toast.LENGTH_SHORT).show();}

            }
        });

        //call google place Api
        CallPlaceAPIProcess callPlaceAPIProcess = new CallPlaceAPIProcess();
        callPlaceAPIProcess.execute(new String[]{latitude+"", longtitude+"", facility_name+""});

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
              .findFragmentById(R.id.frag_map);
        mapFragment.getMapAsync(this);

     //   MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.frag_map);
     //   mapFragment.getMapAsync(this);

        mScrollView = (ScrollView) findViewById(R.id.sv_container);

        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag_map)).setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                mScrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMinZoomPreference(12.0f);
        mMap.setMaxZoomPreference(20.0f);

        // Add a marker to a location and  move the camera
        LatLng facility = new LatLng(latitude, longtitude);
        mMap.addMarker(new MarkerOptions().position(facility).title(facility_name));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longtitude),14.0f));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);


    }

    private class CallPlaceAPIProcess extends AsyncTask<String,Void,Void>
    {

        @Override
        protected Void doInBackground(String... params) {
            try {
                callPlaceWS(params[0],params[1],params[2]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {



        }
    }

    protected String callPlaceWS(String lat, String lon, String facility_name) throws JSONException {
        URL url = null;
        HttpURLConnection conn = null;
        //Weather API JSON response
        String textResult = "";
        String name = facility_name.replaceAll(" ", "+");
        Log.i("GooglePlaceAPI",name);




        try {
            //convert course entity to string json by calling toJson method
            url = new URL(PLACE_API_BASE+"location="+lat+","+lon+"&radius=500&name="+name+PLACE_API_KEY );
            Log.i("GooglePlaceAPI",url.toString());
            //open the connection
            conn = (HttpURLConnection) url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to POST
            conn.setRequestMethod("GET");
            //set the output to true
            conn.setDoOutput(true);
            //add HTTP headers to set your respond type to json
            conn.setRequestProperty("Content-Type", "application/json");
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
                //Log.i("GooglePlaceAPI",textResult);

            }

            jsonObj_detail = new JSONObject(textResult.toString());

            Log.i("GooglePlaceAPI",jsonObj_detail.toString());

            //get the main section from the Json object
            getPlaceFromJson(jsonObj_detail);
            return "ok";

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return textResult;
    }

    private void getPlaceFromJson(JSONObject place) throws JSONException {


        JSONArray arr = place.getJSONArray("results");

        String placeId = arr.getJSONObject(0).getString("place_id");
        Log.i("GooglePlaceAPI",placeId);

        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
                            Log.i("GooglePlaceAPI",myPlace.getPhoneNumber().toString());
                            phoneNumber = myPlace.getPhoneNumber().toString();

                        } else {
                            Log.e(TAG, "Place not found");
                        }
                        places.release();
                    }
                });


        //weather_con = place.getJSONArray("weather").getJSONObject(0).getString("description").toString();
    }


    //asyn task for calling weather API
    private class CallWeatherAPIProcess extends AsyncTask<String,Void,Void>
    {

        @Override
        protected Void doInBackground(String... params) {
            try {
                callWeatherWS(params[0],params[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (temp != 1110.0 && !weather_con.equals(""))
            temperature_tv.setText(temp.intValue()+" ℃");
            weather_tv.setText(weather_con);

        }
    }




    protected String callWeatherWS(String lat, String lon) throws JSONException {
        URL url = null;
        HttpURLConnection conn = null;
        //Weather API JSON response
        String textResult = "";

        try {

            //convert course entity to string json by calling toJson method
            url = new URL(WEATHER_API_BASE + "lat="+lat+"&lon="+lon+ "&APPID="+WEATHER_API_KEY );
            Log.i("EricTestWEATHERAPI",url.toString());
            //open the connection
            conn = (HttpURLConnection) url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to POST
            conn.setRequestMethod("GET");
            //set the output to true
            conn.setDoOutput(true);
            //add HTTP headers to set your respond type to json
            conn.setRequestProperty("Content-Type", "application/json");
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }

            jsonObj_weather = new JSONObject(textResult.toString());
            //get the main section from the Json object
            getWeatherFromJson(jsonObj_weather);
            return "ok";

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return textResult;
    }

    private void getWeatherFromJson(JSONObject weather) throws JSONException {



        JSONObject jsonWeatherMain = weather.getJSONObject("main");



        if(jsonWeatherMain.has("temp")) {
            temp = jsonWeatherMain.getDouble("temp") - 273.15;
            //Log.d("weatherapi",temp+"");
        }
        if(weather.getJSONArray("weather").getJSONObject(0).has("description")) {
            weather_con = weather.getJSONArray("weather").getJSONObject(0).getString("description").toString();
            //Log.d("weatherapi",weather_con+"");

        }
    }


}