package com.monash.eric.mytestingdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.monash.eric.mytestingdemo.Entity.WeatherForeCast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WeatherForecastActivity extends AppCompatActivity {

    public static final String TAG = "WeatherForecastActivity";

    private ProgressDialog progressDialog;


    private TextView tv_forecastDetail;
    private Button back_btn;

    private double longtitude;
    private double latitude;

    private ListView forcast_Listview;
    private WeatherItemAdapter weatherItemAdapter;

    // variable for weather JSON response
    public JSONObject jsonObj_weather;
    public JSONArray forecast_JSONarray;
    
    private ArrayList<WeatherForeCast> weatherForeCastList;

    String weather_con;
    Double temp;


    //open weather map API KEYS AND URL
    private static final String WEATHER_API_BASE ="http://api.openweathermap.org/data/2.5/forecast/daily?";
    private static final String WEATHER_API_KEY ="0d9fffea542769114f53e78627a6e769";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        forcast_Listview = (ListView)findViewById(R.id.listView_forcast);
        weatherForeCastList = new ArrayList<>();

        weatherItemAdapter = new WeatherItemAdapter(this, weatherForeCastList);
        forcast_Listview.setAdapter(weatherItemAdapter);
        
        Intent intent = getIntent();
        longtitude = intent.getDoubleExtra("lng",0);
        latitude = intent.getDoubleExtra("lat",0);

      //  CallWeatherAPIProcess
        CallWeatherAPIProcess callWeatherAPIProcess =new CallWeatherAPIProcess();
        callWeatherAPIProcess.execute(latitude+"",longtitude+"");


        tv_forecastDetail = (TextView) findViewById(R.id.tv_forecast_details);
        back_btn = (Button) findViewById(R.id.back_btn);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    protected String callWeatherWS(String lat, String lon) throws JSONException {
        URL url = null;
        HttpURLConnection conn = null;
        //Weather API JSON response
        String textResult = "";

        try {

            //convert course entity to string json by calling toJson method
            url = new URL(WEATHER_API_BASE + "lat="+lat+"&lon="+lon+ "&APPID="+WEATHER_API_KEY );
            Log.i(TAG,url.toString());
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
            getForeCastFromJson(jsonObj_weather);
            return "ok";

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return textResult;
    }

    private void getForeCastFromJson(JSONObject weather) throws JSONException {

        forecast_JSONarray = weather.getJSONArray("list");
        Log.d(TAG,forecast_JSONarray.length()+"");

        for(int i=0; i<forecast_JSONarray.length(); i++){

            JSONObject row = forecast_JSONarray.getJSONObject(i);


            Log.d(TAG,row.getJSONObject("temp").getString("min"));
            Log.d(TAG,row.getJSONObject("temp").getString("max"));
            Log.d(TAG,row.getLong("dt")+"");
            Log.d(TAG,convertUTCTodate(row.getLong("dt"))+"");

            Log.d(TAG,row.getJSONArray("weather").getJSONObject(0).getString("description"));
            WeatherForeCast weatherForeCast = new WeatherForeCast();
            weatherForeCast.setTemperatue_min(new Double((row.getJSONObject("temp").getDouble("min")-273.15)).intValue()+"℃");
            weatherForeCast.setTemperatue_max(new Double(row.getJSONObject("temp").getDouble("max")-273.15).intValue()+"℃");
            weatherForeCast.setWeather(row.getJSONArray("weather").getJSONObject(0).getString("description"));
            weatherForeCast.setDate(convertUTCTodate(row.getLong("dt")));

            weatherForeCastList.add(weatherForeCast);



        }

//        Log.i("weathertest",weather.getJSONArray("weather").getJSONObject(0).getString("description").toString());
//        Log.i("weathertest","inside transjosn");

//        JSONObject jsonWeatherMain = weather.getJSONObject("main");
//
//        temp = jsonWeatherMain.getDouble("temp")-273.15;
//        weather_con = weather.getJSONArray("weather").getJSONObject(0).getString("description").toString();
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

            weatherItemAdapter.notifyDataSetChanged();
            progressDialog.dismiss();

//            temperature_tv.setText(temp.intValue()+" ℃");
//            weather_tv.setText(weather_con);

        }
    }


    public String convertUTCTodate(long UTCtime)
    {
        java.util.Date time=new java.util.Date((long)UTCtime*1000);
        String formattedDate = new SimpleDateFormat("yyyy-MM-EEE").format(time);

        return formattedDate;

    }
}
