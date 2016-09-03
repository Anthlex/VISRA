package com.monash.eric.mytestingdemo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextDate;
    private EditText editTextVenue;
    private EditText editTextTime;
    private EditText editTextSportType;
    private EditText editTextDesc;

    private String uid;
    private String name;
    private String date;
    private String venue;
    private String sport;
    private String desc;
    private String time;

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
        editTextSportType = (EditText) findViewById(R.id.editTextSportType);
        editTextDesc = (EditText) findViewById(R.id.editTextDesc);
        editTextTime = (EditText) findViewById(R.id.editTextTime);

        buttonCreate = (Button) findViewById(R.id.buttonCreate);

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




    }

    private void createEvent() {

        name = editTextName.getText().toString().trim();
        date = editTextDate.getText().toString().trim();
        venue = editTextVenue.getText().toString().trim();
        sport = editTextSportType.getText().toString().trim();
        desc = editTextDesc.getText().toString().trim();
        time = editTextTime.getText().toString().trim();

        Firebase childRef = mRootRef.push();

        Firebase grandChildRef1 = childRef.child("Name");
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

        startActivity(new Intent(CreateEventActivity.this,MainActivity.class));

    }
}
