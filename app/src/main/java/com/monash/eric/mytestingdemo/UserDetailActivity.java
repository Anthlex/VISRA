package com.monash.eric.mytestingdemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.monash.eric.mytestingdemo.Entity.Users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserDetailActivity extends AppCompatActivity {

    private static final String TAG = "UserDetailActivity";

    private TextView textViewUsername;
    private TextView textViewGender;
    //private TextView textViewBirthday;
    private TextView textViewCountry;
    private TextView textViewSports;

    // fire base user credentials
    private FirebaseAuth firebaseAuth;
    //firebase object
    private Firebase mRootRef;

    private ArrayList<String> friend_ids;


    private String curr_uid;

    private String addfriend_id;

    private Button buttonOk;
    private Button addFriend_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        Firebase.setAndroidContext(this);

        firebaseAuth = FirebaseAuth.getInstance();

        friend_ids = new ArrayList<>();


        textViewUsername = (TextView)findViewById(R.id.friendReqDetails_tvUsername);
        textViewGender = (TextView)findViewById(R.id.friendReqDetails_tvGender);
        //textViewBirthday = (TextView)findViewById(R.id.friendReqDetails_tvBirthday);
        textViewCountry = (TextView)findViewById(R.id.friendReqDetails_tvCountry);
        textViewSports = (TextView)findViewById(R.id.friendReqDetails_tVSports);
        buttonOk = (Button)findViewById(R.id.ok_btn_user_detail_activity);
        addFriend_btn = (Button)findViewById(R.id.addfriend_btn_user_detail_act);

        curr_uid = firebaseAuth.getCurrentUser().getUid();

        Intent i = getIntent();
        Users users = (Users) i.getSerializableExtra("eventObj");

        addfriend_id = users.getUser_id();

        textViewSports.setText(users.getSports());
        textViewUsername.setText(users.getUsername());
        //textViewBirthday.setText(users.getBirthday());
        textViewGender.setText(users.getGender());
        textViewCountry.setText(users.getCountry());

        addFriend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFriend(curr_uid,addfriend_id);

            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserDetailActivity.this,MainActivity.class));
            }
        });

        checkFriend();
    }

    private void checkFriend() {

        mRootRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");
        Firebase currentUser = mRootRef.child(curr_uid);
        final Firebase currentFriend = currentUser.child("Friends");

        currentFriend.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {


                    HashMap<String, String> userMap = (HashMap<String, String>) child.getValue();


                    if (userMap.get("status").equals("1")) {

                        friend_ids.add(userMap.get("Friend_id"));
                        //Log.d(TAG,userMap.get("Friend_id") );
                    }

                    Log.d(TAG,friend_ids.toString() );

                    if(friend_ids.contains(addfriend_id)){

                        addFriend_btn.setText("Already Friend");
                        addFriend_btn.setEnabled(false);
                        addFriend_btn.setBackgroundColor(Color.LTGRAY);
                        addFriend_btn.setTextColor(Color.WHITE);
                    }

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

    private void addFriend(String actioner_id, String other_userid)
    {

        // adding record in actioner node
        Firebase userAll_node = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        Firebase user_node = userAll_node.child(actioner_id);
        Firebase firend_node = user_node.child("Friends");

        Map<String, String> newFriend = new HashMap<String, String>();
        newFriend.put("Friend_id", other_userid);
        newFriend.put("status", "2");
        newFriend.put("action_user", actioner_id);
        firend_node.child(other_userid).setValue(newFriend);


        // adding record in other user node

        user_node = userAll_node.child(other_userid);
        firend_node = user_node.child("Friends");

        newFriend = new HashMap<String, String>();
        newFriend.put("Friend_id", actioner_id);
        newFriend.put("status", "2");
        newFriend.put("action_user", actioner_id);
        firend_node.child(actioner_id).setValue(newFriend);

        Toast.makeText(UserDetailActivity.this,"Request has sent!",Toast.LENGTH_SHORT).show();

        //TODO prevent adding firends who are already firends


    }
}
