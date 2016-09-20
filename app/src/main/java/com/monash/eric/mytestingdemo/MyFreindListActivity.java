package com.monash.eric.mytestingdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.monash.eric.mytestingdemo.Entity.Users;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class MyFreindListActivity extends AppCompatActivity {

    private ListView myfriend_lv;
    private ArrayAdapter<String> adapter;

    private ArrayList<String> names;
    private ArrayList<Users> friends;
    private ArrayList<String> friend_ids;


    private FirebaseAuth firebaseAuth;
    private Firebase mRootRef;

    private String Current_userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_freind_list);

        myfriend_lv = (ListView)findViewById(R.id.lv_myFriends);

        names = new ArrayList<>();
        friend_ids = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,names);


        myfriend_lv.setAdapter(adapter);

        //firebase set context
        Firebase.setAndroidContext(this);
        firebaseAuth = FirebaseAuth.getInstance();

        Current_userid = firebaseAuth.getCurrentUser().getUid();

//        mRootRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        getAllFriends(Current_userid);


        getDetialsFromUidList(friend_ids);


        myfriend_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(view.getContext(), UserDetailActivity.class);
                Users selectedEvent =friends.get(position);


                intent.putExtra("eventObj",selectedEvent);
                startActivity(intent);
            }
        });
    }


    private void getAllFriends(String uid)
    {

        friends = new ArrayList<>();
        Firebase userAll_node = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        // get a particular user by uid
        Firebase user_node = userAll_node.child(uid);
        final Firebase firend_node = user_node.child("Friends");

        firend_node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child: dataSnapshot.getChildren()) {


                    HashMap<String, String> userMap = (HashMap<String, String>) child.getValue();


                    if(userMap.get("status").equals("1"))
                    {

                        friend_ids.add(userMap.get("Friend_id"));
                    }


                }

               // Log.d("mylist",friend_ids.size()+"");

            }




            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    private void getDetialsFromUidList(final ArrayList<String> uidList)
    {
        Firebase userAll_node = new Firebase("https://visra-1d74b.firebaseio.com/Users");


        friends = new ArrayList<>();

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
                            friends.add(user);

                        }

                    }
                }

                Log.d("mylist",friend_ids.size()+"**");

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private Users buildUserFromChildNode(HashMap<String,String> userMap,String uid)
    {

        names.add(userMap.get("Username"));
        Log.d("mylist",friend_ids.size()+"*++*");

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
        names.clear();
        adapter.notifyDataSetChanged();

    }
}
