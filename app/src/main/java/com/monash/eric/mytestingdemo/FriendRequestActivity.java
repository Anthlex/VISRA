package com.monash.eric.mytestingdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.monash.eric.mytestingdemo.Entity.Users;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendRequestActivity extends AppCompatActivity {

    private TextView textView;
    private ListView lv_req;

    private ArrayAdapter<String> adapter;


    public final static String TAG = "FriendRequestActivity";

    private FirebaseAuth firebaseAuth;
    private Firebase mRootRef;

    private String Current_userid;

    private ArrayList<String> requests_uid_list;
    private ArrayList<Users> request_user_list;


    //
    private ArrayList<String> userTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        textView = (TextView)findViewById(R.id.tv_friendRequest);
        lv_req = (ListView)findViewById(R.id.listView_reqs);

        userTitle = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,userTitle);

        lv_req.setAdapter(adapter);

        //firebase set context
        Firebase.setAndroidContext(this);
        firebaseAuth = FirebaseAuth.getInstance();

        Current_userid = firebaseAuth.getCurrentUser().getUid();

        mRootRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");

   //     Log.d(TAG,"infrinedReqactiity");

        getAllRequestsUids(Current_userid);

        getDetialsFromUidList(requests_uid_list);



        lv_req.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(view.getContext(), FriendReqDetailsActivity.class);
                Users selectedEvent =request_user_list.get(position);

//                Log.d(TAG,position+"");
//                Log.d(TAG,similar_interest_users.get(position).getUsername());
                intent.putExtra("eventObj",selectedEvent);
                startActivity(intent);
            }
        });




    }

    //display all requests in a list
    private String getAllRequestsUids(String uid)
    {

        requests_uid_list = new ArrayList<>();

        //TODO only display requests, now displaying all items in friend list,
        //TODO incluoding blocked request and accepted requests.
        Firebase userAll_node = new Firebase("https://visra-1d74b.firebaseio.com/Users");

       // Log.d(TAG,"displayAllRequests");

        // get a particular user by uid
        Firebase user_node = userAll_node.child(uid);
        Firebase firend_node = user_node.child("Friends");

        firend_node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child: dataSnapshot.getChildren()) {


                    HashMap<String, String> userMap = (HashMap<String, String>) child.getValue();

                    String uid_from_map = userMap.get("action_user");
                    //skiiing all the request send by user, only get the request from others
                    if(uid_from_map.equals(Current_userid) || !userMap.get("status").equals("2"))
                    {
                        continue;
                    }

                    requests_uid_list.add(userMap.get("Friend_id"));

                    Log.d(userMap.get("Friend_id"),"N/A");
                    Log.d(userMap.get("action_user"),"N/A");
                    Log.d(userMap.get("status"),"N/A");

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
//
//        Map<String, String> newFriend = new HashMap<String, String>();
//        newFriend.put("Friend_id", other_userid);
//        newFriend.put("status", "2");
//        newFriend.put("action_user", actioner_id);
//        firend_node.push().setValue(newFriend);
        return null;
    }


    private void getDetialsFromUidList(final ArrayList<String> uidList)
    {
        Firebase userAll_node = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        Log.d(TAG,"displayAllRequests");

        request_user_list = new ArrayList<>();

        // get a particular user by uid
        userAll_node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(String uid : uidList)
                {
                    for (DataSnapshot child: dataSnapshot.getChildren()) {

                        //get all the information for a user
                        if(uid.equals(child.getKey())){
                            Users user = buildUserFromChildNode((HashMap<String, String>)child.getValue(),child.getKey());
                            request_user_list.add(user);

                        }



                    }
                }


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    private Users buildUserFromChildNode(HashMap<String,String> userMap,String uid)
    {

        userTitle.add(userMap.get("Username"));
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
//
        Log.d(TAG,userTitle.size()+"");
        userTitle.clear();
        adapter.notifyDataSetChanged();

        Log.d(TAG,request_user_list.size()+"");
        Log.d(TAG,"RESUMED");
    }
}
