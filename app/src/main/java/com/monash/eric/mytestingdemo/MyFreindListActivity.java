package com.monash.eric.mytestingdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.monash.eric.mytestingdemo.Entity.Users;

import java.util.ArrayList;
import java.util.HashMap;

public class MyFreindListActivity extends AppCompatActivity {

    private ListView myfriend_lv;
    private ArrayAdapter<String> adapter;

    private ArrayList<String> names;
    private ArrayList<Users> friends;
    private ArrayList<String> friend_ids;

    private TextView tv_frinedstutas;


    private FirebaseAuth firebaseAuth;
    private Firebase mRootRef;

    private String Current_userid;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private Button friendReq_Btn;

    ValueEventListener allfriendListener, friendDetialListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_freind_list);

        myfriend_lv = (ListView) findViewById(R.id.lv_myFriends);

        tv_frinedstutas = (TextView)findViewById(R.id.my_friend_tv);

        names = new ArrayList<>();
        friend_ids = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, names);


        myfriend_lv.setAdapter(adapter);

        //firebase set context
        Firebase.setAndroidContext(this);
        firebaseAuth = FirebaseAuth.getInstance();

        Current_userid = firebaseAuth.getCurrentUser().getUid();

//        mRootRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");

//        getAllFriends(Current_userid);
//        getDetialsFromUidList(friend_ids);


        myfriend_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(view.getContext(), UserDetailActivity.class);
                Users selectedEvent = friends.get(position);

                intent.putExtra("eventObj", selectedEvent);
                startActivity(intent);
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        friendReq_Btn = (Button)findViewById(R.id.button_FriendRequests);
        friendReq_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyFreindListActivity.this,FriendRequestActivity.class);
                startActivity(intent);
            }
        });
    }


    private void getAllFriends(String uid) {


        friends = new ArrayList<>();
        Firebase userAll_node = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        // get a particular user by uid
        Firebase user_node = userAll_node.child(uid);
        final Firebase firend_node = user_node.child("Friends");

        allfriendListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {


                    HashMap<String, String> userMap = (HashMap<String, String>) child.getValue();


                    if (userMap.get("status").equals("1")) {

                        friend_ids.add(userMap.get("Friend_id"));
                    }
                }

                if(allfriendListener!=null)
                {
                    firend_node.removeEventListener(allfriendListener);
                }

                // new added*
                if(friend_ids.size() == 0)
                {
                    tv_frinedstutas.setText("You don't have any friends currently.");
                }
                else
                {
                    tv_frinedstutas.setText("My Friend");
                }

            }



            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };


        firend_node.addValueEventListener(allfriendListener);

    }


    private void getDetialsFromUidList(final ArrayList<String> uidList) {

        final Firebase userAll_node = new Firebase("https://visra-1d74b.firebaseio.com/Users");


        friends = new ArrayList<>();

        friendDetialListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (String uid : uidList) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        //get all the information for a user
                        if (uid.equals(child.getKey())) {
                            Users user = buildUserFromChildNode((HashMap<String, String>) child.getValue(), child.getKey());
                            friends.add(user);

                        }

                    }
                }

//                Log.d("mylist", friend_ids.size() + "**");

                if(friendDetialListener!=null)
                {
                    userAll_node.removeEventListener(friendDetialListener);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        // get a particular user by uid
        userAll_node.addValueEventListener(friendDetialListener);
    }

    private Users buildUserFromChildNode(HashMap<String, String> userMap, String uid) {

        names.add(userMap.get("Username"));
        Log.d("mylist", friend_ids.size() + "*++*");

        Users user = new Users();
        user.setBirthday(userMap.get("Birthday"));
        user.setCountry(userMap.get("Country"));
        user.setGender(userMap.get("Gender"));
        user.setSports(userMap.get("Sports"));
        user.setUsername(userMap.get("Username"));
        user.setUser_id(uid);
        adapter.notifyDataSetChanged();


        return user;
    }


    @Override
    protected void onResume() {

        super.onResume();
        Log.d("myfirned","resume");
        if(friend_ids!=null) {
            friend_ids.clear();
        }
        if(friends!=null) {
            friends.clear();
        }
        if(names!=null) {
            names.clear();
        }
        Log.d("myfirnedlist", names.size()+"");

        adapter.notifyDataSetChanged();
        getAllFriends(Current_userid);
        getDetialsFromUidList(friend_ids);
        Log.d("myfirnedlist1", names.size()+"");

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(friend_ids!=null) {
            friend_ids.clear();
        }
        if(friends!=null) {
            friends.clear();
        }
        if(names!=null) {
            names.clear();
        }
        Log.d("myfirned","pause");

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MyFreindList Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.monash.eric.mytestingdemo/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MyFreindList Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.monash.eric.mytestingdemo/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
