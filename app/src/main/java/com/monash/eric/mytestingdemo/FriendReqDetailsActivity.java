package com.monash.eric.mytestingdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.core.Tag;
import com.google.firebase.auth.FirebaseAuth;
import com.monash.eric.mytestingdemo.Entity.Users;

import java.util.HashMap;

public class FriendReqDetailsActivity extends AppCompatActivity {



    private static final String ACCEPT = "1";
    private static final String REJECT = "0";

    public final static String TAG = "FriendReqDetailsAct";

    private FirebaseAuth firebaseAuth;
    private Firebase mRootRef;


    private Button accept_btn;
    private Button reject_btn;

    private TextView textViewUsername;
    private TextView textViewGender;
    private TextView textViewBirthday;
    private TextView textViewCountry;
    private TextView textViewSports;

    private String curr_uid;

    Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_req_details);

        Firebase.setAndroidContext(this);
        firebaseAuth = FirebaseAuth.getInstance();

        curr_uid = firebaseAuth.getCurrentUser().getUid();



        accept_btn = (Button)findViewById(R.id.btn_acceptReq);
        reject_btn = (Button)findViewById(R.id.btn_rejectReq);


        //the id is the same as user detail
        textViewUsername = (TextView)findViewById(R.id.textViewUsername);
        textViewGender = (TextView)findViewById(R.id.textViewGender);
        textViewBirthday = (TextView)findViewById(R.id.textViewBirthday);
        textViewCountry = (TextView)findViewById(R.id.textViewCountry);
        textViewSports = (TextView)findViewById(R.id.textViewSports);

        Intent intent = getIntent();

        users = (Users) intent.getSerializableExtra("eventObj");


        textViewSports.setText(users.getSports());
        textViewUsername.setText(users.getUsername());
        textViewBirthday.setText(users.getBirthday());
        textViewGender.setText(users.getGender());
        textViewCountry.setText(users.getCountry());


        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO change status to 1
//                Log.d(TAG,users.getUser_id());
                changeReqStatus(users.getUser_id(),ACCEPT);
                changeSendReqStatus(users.getUser_id(),ACCEPT);
                onBackPressed();
            }
        });

        reject_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeReqStatus(users.getUser_id(),REJECT);
                onBackPressed();


                //TODO change status to 0
            }
        });



    }

    private void changeReqStatus(final String requid, final String response)
    {

        mRootRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        Firebase user_node = mRootRef.child(curr_uid);
        Firebase firend_node = user_node.child("Friends");

   //     Log.d(TAG,firend_node.getKey());
   //     Firebase reqnodes = firend_node.

        firend_node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot child: dataSnapshot.getChildren()) {


                    HashMap<String, String> userMap = (HashMap<String, String>) child.getValue();

//                  Firebase uidnode = firend_node.child()

                    String uid_from_map = userMap.get("action_user");
                    //skiiing all the request send by user, only get the request from others
                    if(uid_from_map.equals(curr_uid))
                    {
                        continue;
                    }
                    if(userMap.get("Friend_id").equals(requid))
                    {

                        Log.d(TAG,curr_uid);
                        Log.d(TAG,"----------");
                        Log.d(TAG,requid);

                        // store the key of user who is going to change the status
                        String userkey = child.getKey();
                        Firebase friendReqRecNode = mRootRef.child(curr_uid).child("Friends").child(userkey);
                        friendReqRecNode.child("status").setValue(response);

//                        Firebase firendSendNode = mRootRef.child(requid).child("Friends").child(curr_uid);
//                        firendSendNode.child("status").setValue(response);

                    }


                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    public void changeSendReqStatus(final String respone_uid, final String response){

        mRootRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        Firebase user_node = mRootRef.child(respone_uid);
        Firebase firend_node = user_node.child("Friends");


        firend_node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

               for (DataSnapshot child: dataSnapshot.getChildren()) {

                    HashMap<String, String> userMap = (HashMap<String, String>) child.getValue();

                    String uid_from_map = userMap.get("action_user");
//                    //skiiing all the request send by user, only get the request from others
                    if(uid_from_map.equals(respone_uid))
                    {
                        String userkey = child.getKey();
                        Firebase friendReqRecNode = mRootRef.child(respone_uid).child("Friends").child(userkey);
                        friendReqRecNode.child("status").setValue(response);

                    }

               }
//
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
}
