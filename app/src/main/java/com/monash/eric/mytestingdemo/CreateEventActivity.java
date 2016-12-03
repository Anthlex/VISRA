package com.monash.eric.mytestingdemo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.monash.eric.mytestingdemo.Entity.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CreateEventActivity is used to save user's personal information
 *
 * @author  Anthony, Eric
 * @since 1.0
 */

public class CreateEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // view components
    private EditText editTextName;
    private EditText editTextDate;
    private EditText editTextVenue;
    private EditText editTextTime;
    private EditText editTextDesc;
    private TextView tv_weatherForeCast;
    private Switch aSwitch;
    private Button buttonCreate;
    private Spinner spinner;


    //User id
    private String uid;
    //user name
    private String name;
    //event date
    private String date;
    //event venue
    private String venue;
    //event sport
    private String sport;
    //event description
    private String desc;
    //event time
    private String time;

    //Current user's name
    private String Username;

    //sports can be played in the particular venue
    private ArrayList<String> sportList;

    // Spinner Drop down elements
    private List<String> categories;

    //coordinates
    private double longtitude;
    private double latitude;

    //Firebase event root
    private Firebase mRootRef;
    //Firebase user root
    private Firebase mRef;

    //FireAuth varible
    private FirebaseAuth firebaseAuth;
    //FireAuth authentication listener
    private FirebaseAuth.AuthStateListener authStateListener;

    //SharedPreference for store and retrieve user's name
    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);


        // retrieve venue information from MapAcivity
        Intent intent = getIntent();
        venue = intent.getStringExtra("facility_name");
        longtitude = intent.getDoubleExtra("lng", 0);
        latitude = intent.getDoubleExtra("lat", 0);
        sportList = intent.getStringArrayListExtra("sportList");

        categories = new ArrayList<String>();

        if(sportList != null)
        {
            categories = new ArrayList<String>();
            categories = sportList;
        }
        else
        {
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
        }


        initComponents();

        setListenersForComponents();

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {

            uid = firebaseAuth.getCurrentUser().getUid();

        } else {
            Toast.makeText(CreateEventActivity.this, "You need to login first", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(CreateEventActivity.this, LoginActivity.class));
        }


        sharedPreferences = getSharedPreferences("userProfile", MODE_PRIVATE);
        Username = sharedPreferences.getString("Username", "n/a");

        Firebase.setAndroidContext(this);
        mRootRef = new Firebase("https://visra-1d74b.firebaseio.com/Event");



    }

    /**
     *  This method initials all components
     */
    private void initComponents()
    {

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextDate = (EditText) findViewById(R.id.editTextDate);
        editTextVenue = (EditText) findViewById(R.id.editTextVenue);
        editTextDesc = (EditText) findViewById(R.id.editTextDesc);
        editTextTime = (EditText) findViewById(R.id.editTextTime);
        tv_weatherForeCast = (TextView) findViewById(R.id.tv_forecast);
        buttonCreate = (Button) findViewById(R.id.buttonCreate);
        aSwitch = (Switch) findViewById(R.id.buttonSw);
        spinner = (Spinner) findViewById(R.id.spinner);

        // set the venue name frome the Inetent object
        editTextVenue.setText(venue);



    }

    /**
     * This method set listeners for components
     *
     */
    private void setListenersForComponents()
    {

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createEvent();
            }
        });


        // Spinner click listener
        spinner.setOnItemSelectedListener(this);


        tv_weatherForeCast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), WeatherForecastActivity.class);
                intent.putExtra("lng", longtitude);
                intent.putExtra("lat", latitude);
                startActivity(intent);

            }
        });


        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();

                new DatePickerDialog(CreateEventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                int month = monthOfYear + 1;
                                editTextDate.setText(year + "-" + month + "-" + dayOfMonth);
                                date = editTextDate.getText().toString().trim();
                            }
                        }
                        , c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
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
                        editTextTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();

            }
        });

        //attach a listener to check for changes in state
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    buttonCreate.setText("Invite friends");
                } else {
                    buttonCreate.setText("Create");
                }

            }
        });

        //set the switch to ON
        aSwitch.setChecked(false);

        //check the current state before we display the screen
        if (aSwitch.isChecked()) {
            buttonCreate.setText("Invite friends");
        } else {
            buttonCreate.setText("Create");
        }


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

    }

    /**
     * This method is used to create event
     * with all details entered by the user.
     */
    private void createEvent() {

        name = editTextName.getText().toString().trim();
        date = editTextDate.getText().toString().trim();
        venue = editTextVenue.getText().toString().trim();
        desc = editTextDesc.getText().toString().trim();
        time = editTextTime.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {

            //email is empty
            Toast.makeText(CreateEventActivity.this, "Please enter username", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if (TextUtils.isEmpty(date)) {

            //email is empty
            Toast.makeText(CreateEventActivity.this, "Please enter date", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if (TextUtils.isEmpty(time)) {

            //email is empty
            Toast.makeText(CreateEventActivity.this, "Please enter time", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if (TextUtils.isEmpty(venue)) {

            //email is empty
            Toast.makeText(CreateEventActivity.this, "Please enter venue", Toast.LENGTH_SHORT).show();
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

        Firebase grandChildRef10 = childRef.child("Participant");

        Map<String, String> newParticipant = new HashMap<String, String>();
        newParticipant.put("Userid", uid);
        newParticipant.put("Username", Username);
        newParticipant.put("Status", "1");
        //TODO STORE USER NAME;
        grandChildRef10.push().setValue(newParticipant);



        //Add to my event

        mRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        Firebase mchildRef = mRef.child(uid);

        Firebase mgrandChildRe = mchildRef.child("Event");
        Firebase mgrandChildRef = mgrandChildRe.push();
        Firebase mgrandChildRef1 = mgrandChildRef.child("Title");
        mgrandChildRef1.setValue(name);
        Firebase mgrandChildRef2 = mgrandChildRef.child("Venue");
        mgrandChildRef2.setValue(venue);
        Firebase mgrandChildRef3 = mgrandChildRef.child("Date");
        mgrandChildRef3.setValue(date);
        Firebase mgrandChildRef4 = mgrandChildRef.child("Time");
        mgrandChildRef4.setValue(time);
        Firebase mgrandChildRef5 = mgrandChildRef.child("Sport");
        mgrandChildRef5.setValue(sport);
        Firebase mgrandChildRef6 = mgrandChildRef.child("Desc");
        mgrandChildRef6.setValue(desc);
        Firebase mgrandChildRef7 = mgrandChildRef.child("Host");
        mgrandChildRef7.setValue(uid);
        Firebase mgrandChildRef8 = mgrandChildRef.child("Latitude");
        mgrandChildRef8.setValue(la);
        Firebase mgrandChildRef9 = mgrandChildRef.child("Longtitue");
        mgrandChildRef9.setValue(lo);
        Firebase mgrandChildRef10 = mgrandChildRef.child("Status");
        mgrandChildRef10.setValue("joined");

        //create a event object to be passed
        Event newEvent = new Event();
        newEvent.setDate(date);
        newEvent.setDescription(desc);
        newEvent.setHostId(uid);
        newEvent.setLatitute(la);
        newEvent.setLongtitue(lo);
        newEvent.setSport(sport);
        newEvent.setTitle(name);
        newEvent.setTime(time);
        newEvent.setVenue(venue);





        Firebase keynode = grandChildRef10.push();


        //eventid to be passed
        String eventidtobepassed = childRef.getKey();
        Log.d("keynode",childRef.toString());
        Log.d("keynode",childRef.getKey());
        keynode.setValue(newParticipant);



        if(aSwitch.isChecked()){
            Firebase grandChildRef11 = childRef.child("Status");
            grandChildRef11.setValue("2");

            Intent i  = new Intent(CreateEventActivity.this, InviteActivity.class);
            i.putExtra("eventobj1",newEvent);
            i.putExtra("eventid",eventidtobepassed);
            startActivity(i);

        }else{
            Firebase grandChildRef11 = childRef.child("Status");
            grandChildRef11.setValue("1");
            startActivity(new Intent(CreateEventActivity.this, MainActivity.class));
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        sport = parent.getItemAtPosition(position).toString();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}
