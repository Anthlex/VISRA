package com.monash.eric.mytestingdemo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;



public class RegisterActivity extends AppCompatActivity {

    private EditText editTextBirthday;
    private EditText editTextCountry;
    private EditText editTextUsername;

    private Button buttonCon;
    private RadioGroup radioGroup;

    private Firebase mRootRef;
    private FirebaseAuth firebaseAuth;

    private String uid;
    private String gender;
    private String birthday;
    private String country;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //profile activity here
            uid = firebaseAuth.getCurrentUser().getUid();
        }

        Firebase.setAndroidContext(this);

        mRootRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextBirthday = (EditText) findViewById(R.id.editTextBirthday);
        editTextCountry = (EditText) findViewById(R.id.editTextCountry);

        buttonCon = (Button) findViewById(R.id.buttonContinue);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {

                RadioButton radioButton = (RadioButton) findViewById(checkId);
                gender = radioButton.getText().toString();

            }
        });


        editTextBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar c = Calendar.getInstance();

                new DatePickerDialog(RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public  void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){

                                int month = monthOfYear + 1;
                                editTextBirthday.setText(year+"-"+month+"-"+dayOfMonth);
                                birthday = editTextBirthday.getText().toString().trim();
                            }
                        }
                        ,c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        buttonCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(RegisterActivity.this,uid,Toast.LENGTH_SHORT).show();


                username = editTextUsername.getText().toString().trim();
                country = editTextCountry.getText().toString().trim();

                Firebase childRef = mRootRef.child(uid);

                Firebase grandChildRef1 = childRef.child("Username");
                grandChildRef1.setValue(username);

                Firebase grandChildRef2 = childRef.child("Gender");
                grandChildRef2.setValue(gender);

                Firebase grandChildRef3 = childRef.child("Birthday");
                grandChildRef3.setValue(birthday);

                Firebase grandChildRef4 = childRef.child("Country");
                grandChildRef4.setValue(country);

                startActivity(new Intent(RegisterActivity.this,SportsActivity.class));

            }

        });

    }

}
