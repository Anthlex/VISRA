package com.monash.eric.mytestingdemo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText editTextName;
    private EditText editTextDate;
    private EditText editTextVenue;
    private EditText editTextTime;
    private EditText editTextDesc;

    private TextView tv_weatherForeCast;


    private String uid;
    private String name;
    private String date;
    private String venue;
    private String sport;
    private String desc;
    private String time;

    //coordinates
    private double longtitude;
    private double latitude;

    private Firebase mRootRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private Button buttonCreate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //profile activity here
            //TODO:
            uid = firebaseAuth.getCurrentUser().getUid();

        }else {
            Toast.makeText(CreateEventActivity.this,"You need to login first",Toast.LENGTH_SHORT).show();

            startActivity(new Intent(CreateEventActivity.this,LoginActivity.class));
        }

        Firebase.setAndroidContext(this);

        mRootRef = new Firebase("https://visra-1d74b.firebaseio.com/Event");

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextDate = (EditText) findViewById(R.id.editTextDate);
        editTextVenue = (EditText) findViewById(R.id.editTextVenue);
        editTextDesc = (EditText) findViewById(R.id.editTextDesc);
        editTextTime = (EditText) findViewById(R.id.editTextTime);
		tv_weatherForeCast = (TextView)findViewById(R.id.tv_forecast);
        tv_weatherForeCast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),WeatherForecastActivity.class);
                intent.putExtra("lng",longtitude);
                intent.putExtra("lat",latitude);
                startActivity(intent);

            }
        });

        buttonCreate = (Button) findViewById(R.id.buttonCreate);

        Intent intent = getIntent();
        venue = intent.getStringExtra("facility_name");
        longtitude = intent.getDoubleExtra("lng",0);
        latitude = intent.getDoubleExtra("lat",0);
        editTextVenue.setText(venue);


        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();

                new DatePickerDialog(CreateEventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public  void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){

                                int month = monthOfYear + 1;
                                editTextDate.setText(year+"-"+month+"-"+dayOfMonth);
                                date = editTextDate.getText().toString().trim();
                            }
                        }
                        ,c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        editTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        editTextTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();

            }
        });

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createEvent();
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
        sport = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        //Toast.makeText(parent.getContext(), "Selected: " + sport, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private void createEvent() {

        name = editTextName.getText().toString().trim();
        date = editTextDate.getText().toString().trim();
        venue = editTextVenue.getText().toString().trim();
        desc = editTextDesc.getText().toString().trim();
        time = editTextTime.getText().toString().trim();

        if(TextUtils.isEmpty(name)){

            //email is empty
            Toast.makeText(CreateEventActivity.this,"Please enter username", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if(TextUtils.isEmpty(date)){

            //email is empty
            Toast.makeText(CreateEventActivity.this,"Please enter date", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if(TextUtils.isEmpty(time)){

            //email is empty
            Toast.makeText(CreateEventActivity.this,"Please enter time", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if(TextUtils.isEmpty(venue)){

            //email is empty
            Toast.makeText(CreateEventActivity.this,"Please enter venue", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        Firebase childRef = mRootRef.push();

        Firebase grandChildRef1 = childRef.child("Title");
        grandChildRef1.setValue(name);

        Firebase grandChildRef2 = childRef.child("Date");
        grandChildRef2.setValue(date);

        Firebase grandChildRef3 = childRef.child("Venue");
        grandChildRef3.setValue(venue);

        Firebase grandChildRef4 = childRef.child("Sport");
        grandChildRef4.setValue(sport);

        Firebase grandChildRef5 = childRef.child("Desc");
        grandChildRef5.setValue(desc);

        Firebase grandChildRef6 = childRef.child("Host");
        grandChildRef6.setValue(uid);

        Firebase grandChildRef7 = childRef.child("Time");
        grandChildRef7.setValue(time);

        String la = String.valueOf(latitude);
        String lo = String.valueOf(longtitude);

        Firebase grandChildRef8 = childRef.child("Latitude");
        grandChildRef8.setValue(la);

        Firebase grandChildRef9 = childRef.child("Longtitue");
        grandChildRef9.setValue(lo);

        startActivity(new Intent(CreateEventActivity.this,MainActivity.class));

    }
}
