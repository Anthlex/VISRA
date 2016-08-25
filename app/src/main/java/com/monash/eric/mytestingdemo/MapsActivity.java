package com.monash.eric.mytestingdemo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static final String WEATHER_API_BASE ="http://api.openweathermap.org/data/2.5/weather?";
    private static final String WEATHER_API_KEY ="0d9fffea542769114f53e78627a6e769";

    public JSONObject jsonObj_weather;

    double longtitude;
    double latitude;
    String facility_name;
    String SportsPlayed;
    String FieldSurfaceType;
    int facility_age;
    String weather;
    Double temp;



    private TextView temperature_tv;
    private TextView field_tv;
    private TextView facility_name_tv;
    private TextView weather_tv;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        facility_name_tv = (TextView) findViewById(R.id.tv_mapact_facility_name);
        field_tv = (TextView)findViewById(R.id.field_type);
        weather_tv=(TextView)findViewById(R.id.weather_name);
        temperature_tv=(TextView)findViewById(R.id.temperature);


        Intent intent = getIntent();
        longtitude = intent.getDoubleExtra("Longitude",0);
        latitude = intent.getDoubleExtra("Latitude",0);
        facility_name = intent.getStringExtra("FacilityName");
       // SportsPlayed = intent.getStringExtra("SportsPlayed");
        FieldSurfaceType =intent.getStringExtra("FieldSurfaceType");
        //facility_age = intent.getIntExtra("FacilityAge",0);

        facility_name_tv.setText(facility_name);
        field_tv.setText(FieldSurfaceType);
        weather_tv.setText("Not avilable now");

        temperature_tv.setText("Not avilable now");
        //sport.setText(SportsPlayed);
       // field.setText(FieldSurfaceType);
       // age.setText(facility_age+"");

        CallWeatherAPIProcess callWeatherAPIProcess = new CallWeatherAPIProcess();
        callWeatherAPIProcess.execute(new String[]{latitude+"", longtitude+""});
        //sport.setText(SportsPlayed);
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

        mMap.setMinZoomPreference(12.0f);
        mMap.setMaxZoomPreference(20.0f);

        // Add a marker in Sydney and move the camera
        LatLng facility = new LatLng(latitude, longtitude);
        mMap.addMarker(new MarkerOptions().position(facility).title(facility_name));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longtitude),14.0f));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//
//                mMap.getUiSettings().setMapToolbarEnabled(true);
//                // return true will prevent any further map action from happening
//                return false;
//
//            }
//        });


    }


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

            Log.i("degug","onpost");
            temperature_tv.setText(temp.intValue()+" ℃");
            weather_tv.setText(weather);

            //proximity shower rain

        }
    }


    protected String callWeatherWS(String lat, String lon) throws JSONException {
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        Log.i("debug","heoolo");


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
            //set length of the data you are sending
            //add HTTP headers to set your respond type to json
            conn.setRequestProperty("Content-Type", "application/json");
            Scanner inStream = new Scanner(conn.getInputStream());
            //  Log.i("EricTestReg---", "aa2");
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            Log.i("EricTestWEATHERAPI---", textResult);


            jsonObj_weather = new JSONObject(textResult.toString());
            //JSONObject jsonWeatherMain = jsonObj_weather.getJSONObject("main");

            weather =  getWeatherFromJson(jsonObj_weather);


            Log.i("debug",weather);
            weather_tv.setText(weather);
            temperature_tv.setText(temp+" ℃");

            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }


        return textResult;
    }

    private String getWeatherFromJson(JSONObject weather) throws JSONException {

//        Log.i("weathertest","sss");
//        Log.i("weathertest",weather.toString());

        Log.i("weathertest",weather.getJSONArray("weather").getJSONObject(0).getString("description").toString());
        Log.i("weathertest","inside transjosn");

        JSONObject jsonWeatherMain = weather.getJSONObject("main");

        temp = jsonWeatherMain.getDouble("temp")-273.15;
        Log.i("weathertest",temp+"");

        //weather.getJSONObject("weather");
        return weather.getJSONArray("weather").getJSONObject(0).getString("description").toString();
    }
}
