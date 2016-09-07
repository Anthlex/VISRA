package com.monash.eric.mytestingdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class AccountActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Firebase mRef;

    private TextView textViewUsername;
    private TextView textViewGender;
    private TextView textViewBirthday;
    private TextView textViewSports;
    private TextView textViewCountry;

    private Button buttonCancel;
    private Button buttonEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);


        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null){

                    startActivity(new Intent(AccountActivity.this,MainActivity.class));

                }
            }
        };

        Firebase.setAndroidContext(this);


        FirebaseUser user = firebaseAuth.getCurrentUser();

        String uid = user.getUid();

        textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        textViewGender = (TextView) findViewById(R.id.textViewGender);
        textViewBirthday = (TextView) findViewById(R.id.textViewBirthday);
        textViewSports = (TextView) findViewById(R.id.textViewSports);
        textViewCountry = (TextView) findViewById(R.id.textViewCountry);

        mRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");


        mRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String,String> map = dataSnapshot.getValue(Map.class);

                if(map == null){

                    Toast.makeText(AccountActivity.this,"You have not set any profile yet, please set it!",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AccountActivity.this, RegisterActivity.class));
                }
                else {

                    String username = map.get("Username");
                    String gender = map.get("Gender");
                    String birthday = map.get("Birthday");
                    String sports = map.get("Sports");
                    String country = map.get("Country");

                    if (birthday == "") {
                        textViewBirthday.setText("Birthday : ");
                    } else {
                        textViewBirthday.setText("Birthday : " + birthday);
                    }

                    if (gender == "") {
                        textViewGender.setText("Gender : ");
                    } else {
                        textViewGender.setText("Gender : " + gender);
                    }

                    textViewUsername.setText("Name : " + username);
                    textViewSports.setText("Sport : " + sports);
                    textViewCountry.setText("Country : " + country);
                }



            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        buttonEdit = (Button) findViewById(R.id.buttonEdit);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(AccountActivity.this, SportsActivity.class));
            }
        });




        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(AccountActivity.this, MainActivity.class));

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
