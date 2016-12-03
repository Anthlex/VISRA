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
/**
 * AccountActivity is used to save user's personal information
 *
 * @author  Anthony, Eric
 * @since 1.0
 */
public class AccountActivity extends AppCompatActivity {

    //firebase authentication variables
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    //firebase root element
    private Firebase mRef;

    // view components
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


        //initial Components
        initComponents();

        //set listeners for components
        setListenersForComponents();

        //get fireauth instance
        firebaseAuth = FirebaseAuth.getInstance();


        //set authentication listener
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null){

                    startActivity(new Intent(AccountActivity.this,MainActivity.class));

                }
            }
        };

        // initalize firebase library
        Firebase.setAndroidContext(this);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        String uid = user.getUid();

        mRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");


        // retriving user's details and display them
        getCurrentUserDetails(uid);

    }


    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }


    /**
     *  This method initials all components
     */
    private void initComponents()
    {

        textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        textViewGender = (TextView) findViewById(R.id.textViewGender);
        textViewBirthday = (TextView) findViewById(R.id.textViewBirthday);
        textViewSports = (TextView) findViewById(R.id.textViewSports);
        textViewCountry = (TextView) findViewById(R.id.textViewCountry);

        buttonEdit = (Button) findViewById(R.id.buttonEdit);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);

    }

    /**
     * This method set listeners for components
     *
     */

    private void setListenersForComponents()
    {
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(AccountActivity.this, RegisterActivity.class));
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    /**
     *  This method retrieves user's details and display them
     *  If the user profile is empty, it will bring user to RegisterActivity
     *  to fill his/her personal information
     *
     *  @param uid current user's id
     */
    private void getCurrentUserDetails(String uid)
    {
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


                    if (birthday == null) {
                        textViewBirthday.setText("Birthday : Not Set");
                    } else {
                        textViewBirthday.setText("Birthday : " + birthday);
                    }

                    if (gender == null) {
                        textViewGender.setText("Gender : Not Set");
                    } else {
                        textViewGender.setText("Gender : " + gender);
                    }

                    if (username == null) {
                        textViewUsername.setText("Name : Not Set");
                    } else {
                        textViewUsername.setText("Name : " + username);
                    }

                    if (sports == null) {
                        textViewSports.setText("Sport : Not Set");
                    } else {
                        textViewSports.setText("Sport : " + sports);
                    }

                    if (country == null) {
                        textViewCountry.setText("Country : Not Set");
                    } else {
                        textViewCountry.setText("Country : " + country);
                    }
                }



            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
