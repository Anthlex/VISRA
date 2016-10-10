package com.monash.eric.mytestingdemo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    private EditText editTextBirthday;
    private EditText editTextCountry;
    private EditText editTextUsername;

    private Button buttonCon;
    private RadioGroup radioGroup;
    private RadioButton radiobutton1;
    private RadioButton radiobutton2;

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

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextBirthday = (EditText) findViewById(R.id.editTextBirthday);
        editTextCountry = (EditText) findViewById(R.id.editTextCountry);

        buttonCon = (Button) findViewById(R.id.buttonContinue);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radiobutton1 =(RadioButton)findViewById(R.id.Male);
        radiobutton2 =(RadioButton)findViewById(R.id.Female);

        mRootRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        mRootRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String,String> map = dataSnapshot.getValue(Map.class);

                if(map == null){

                    //Toast.makeText(RegisterActivity.this,"Please set your profile!",Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(RegisterActivity.this, RegisterActivity.class));
                }
                else {

                    String username = map.get("Username");
                    String gender = map.get("Gender");
                    String birthday = map.get("Birthday");
                    String sports = map.get("Sports");
                    String country = map.get("Country");

                    editTextUsername.setText(username);
                    editTextBirthday.setText(birthday);
                    editTextCountry.setText(country);

                    //Toast.makeText(RegisterActivity.this,gender,Toast.LENGTH_SHORT).show();

                    if (gender == null){
                        return;
                    }else{
                        if(gender.equals("Male")){

                            radiobutton1.setChecked(true);
                            return;
                        }
                        if(gender.equals("Female")){
                            radiobutton2.setChecked(true);
                            return;
                        }
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



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


                birthday = editTextBirthday.getText().toString().trim();
                country = editTextCountry.getText().toString().trim();
                username = editTextUsername.getText().toString().trim();


                if(TextUtils.isEmpty(username)){

                    //email is empty
                    Toast.makeText(RegisterActivity.this,"Please enter username", Toast.LENGTH_SHORT).show();
                    //stopping the function execution further
                    return;
                }

                if(TextUtils.isEmpty(gender)){

                    //email is empty
                    Toast.makeText(RegisterActivity.this,"Please select gender", Toast.LENGTH_SHORT).show();
                    //stopping the function execution further
                    return;
                }


                if(TextUtils.isEmpty(birthday)){

                    //email is empty
                    Toast.makeText(RegisterActivity.this,"Please enter birthday", Toast.LENGTH_SHORT).show();
                    //stopping the function execution further
                    return;
                }


                if(TextUtils.isEmpty(country)){

                    //email is empty
                    Toast.makeText(RegisterActivity.this,"Please enter country", Toast.LENGTH_SHORT).show();
                    //stopping the function execution further
                    return;
                }



                Firebase childRef = mRootRef.child(uid);

                Firebase grandChildRef1 = childRef.child("Username");
                grandChildRef1.setValue(username);

                Firebase grandChildRef2 = childRef.child("Gender");
                grandChildRef2.setValue(gender);

                Firebase grandChildRef3 = childRef.child("Birthday");
                grandChildRef3.setValue(birthday);

                Firebase grandChildRef4 = childRef.child("Country");
                grandChildRef4.setValue(country);

                Firebase grandChildRef5 = childRef.child("Uid");
                grandChildRef5.setValue(uid);

                startActivity(new Intent(RegisterActivity.this,SportsActivity.class));

            }

        });

    }

}
