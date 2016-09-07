package com.monash.eric.mytestingdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;


/**
 * Created by IBM on 15/08/2016.
 */
public class FragmentTab_user extends Fragment {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Firebase mRef;

    private Button buttonLogout;
    private Button buttonProfile;
    private TextView textViewHello;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout_account, null);


        firebaseAuth = FirebaseAuth.getInstance();

        Firebase.setAndroidContext(this.getContext());

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {

                    startActivity(new Intent(getActivity(), LoginActivity.class));

                }
                else{
                    successful();
                }
            }
        };

        buttonLogout = (Button) view.findViewById(R.id.buttonLogout);
        buttonProfile = (Button) view.findViewById(R.id.buttonProfile);
        textViewHello = (TextView) view.findViewById(R.id.hello);

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));

            }
        });

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), AccountActivity.class));
            }
        });

        return view;
    }

    private void successful() {



        FirebaseUser user = firebaseAuth.getCurrentUser();


        String uid = user.getUid();

        mRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");


        mRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Map<String,String> map = dataSnapshot.getValue(Map.class);

                if(map == null){

                    textViewHello.setText("Hello !");

                }else {


                    String username = map.get("Username");

                    if (username == "") {

                        textViewHello.setText("Hello !");

                    }

                    textViewHello.setText("Hello " + username + "!");
                }


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

}
