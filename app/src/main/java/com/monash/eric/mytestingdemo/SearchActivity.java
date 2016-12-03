package com.monash.eric.mytestingdemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    //Google GeoCoding URL Setitings
    private static final String GEO_BASE_URI = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";

    //GeoCoding Response
    String geoCodingResponse;

    private static final String API_KEY = "AIzaSyCxzmhZsWml6UQUqK_ss8aPvPzBk1u-YrU";

    private static final String TAG = "SearchActivity";

    public double lng = 0;
    public double lat = 0;


    private static final String BASE_URI = "http://visra9535.cloudapp.net/api/searchall";
    private EditText et_suburb;
    private EditText et_sprot;
    private CheckBox cb_indoor;
    private CheckBox cb_outdoor;

    private String intentStr ="";

    private String suburb;
    private String sports;

    JSONObject jObject = null;
    JSONArray jArray = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        et_suburb = (EditText)findViewById(R.id.searchact_editText_suburb);
        cb_indoor = (CheckBox)findViewById(R.id.searchact_checkBox_indoor);
        cb_outdoor = (CheckBox)findViewById(R.id.searchact_checkBox_outdoor);



        Intent intent = getIntent();
        lng = intent.getDoubleExtra("lng",0);
        lat = intent.getDoubleExtra("lat",0);

        // for testing
//        CallGeoWS callGeoWS = new CallGeoWS();
//        callGeoWS.execute(lng,lat);



        Button button = (Button) this.findViewById(R.id.button2_actsearch);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_suburb.getText().toString().equals(""))
                {
                    Log.i("aa", "empty");
                }
                Log.i("aa", et_suburb.getText().toString());


                CallSearchSprostAPI callSearchSprostAPI = new CallSearchSprostAPI();
                callSearchSprostAPI.execute("aa");




            }
        });


        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Soccer");
        categories.add("Basketball");
        categories.add("Table Tennis");
        categories.add("Swimming");
        categories.add("Badminton");
        categories.add("Volleyball");
        categories.add("Cricket");
        categories.add("Snooker");
        categories.add("Cycling");
        categories.add("Hockey");
        categories.add("Tennis");
        categories.add("Rugby Union");
        categories.add("Rugby Legue");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);




    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        sports = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        //Toast.makeText(parent.getContext(), "Selected: " + sports, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    private class CallSearchSprostAPI extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... params) {

            return  callSearchSportsWS(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {


            intentStr = s;

            Intent intent = new Intent();
            Log.i("TESTBACK",intentStr);
            intent.putExtra("result", intentStr);
            SearchActivity.this.setResult(RESULT_OK, intent);
            //关闭Activity
            SearchActivity.this.finish();
        }
    }



    protected String callSearchSportsWS(String subrubs)
    {
        HashMap<String, String> postDataParams = new HashMap<>();
        if(!et_suburb.getText().toString().equals(""))
        {
            postDataParams.put("suburbs",et_suburb.getText().toString());

        }

        if(sports != null)
        {
            postDataParams.put("sports",sports);

        }

        if (cb_indoor.isChecked() && cb_outdoor.isChecked())
        {
            Log.i("a","nothing");
        }
        else if(!cb_indoor.isChecked() && !cb_outdoor.isChecked())
        {

        }
        else if(cb_indoor.isChecked())
        {
            postDataParams.put("indoor","1");

        }
        else
        {
            Log.i("a","outdoor clicked");

            postDataParams.put("indoor","0");

        }

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
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                    System.out.println(response);
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;

    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        System.out.println("RequestPara : " + result.toString());

        return result.toString();
    }


    private String callGeoWS(double lng, double lat)
    {

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

            Log.d(TAG,sb.toString());

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


    private class CallGeoWS extends AsyncTask<Double,Void,String>
    {

        @Override
        protected String doInBackground(Double... params) {

            return callGeoWS(params[0],params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            getSuburbFromJson(result);
            et_suburb.setText(suburb);


        }
    }


    private String getSuburbFromJson(String originalJson)
    {


        Gson gson = new Gson();
        GeoResponse geoResponse = gson.fromJson(originalJson,GeoResponse.class);

        GeoResponse.address_component[] element = geoResponse.results[0].address_components;

        Log.d(TAG,element[0].long_name);

        for(int i = 0 ; i<element.length; i++)
        {

            if(element[i].types[0].equals("locality"))
            {
                suburb = element[i].long_name;


            }
        }

        return null;
    }
}
