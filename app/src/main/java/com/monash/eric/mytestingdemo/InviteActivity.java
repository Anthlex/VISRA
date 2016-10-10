package com.monash.eric.mytestingdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.monash.eric.mytestingdemo.Entity.Event;
import com.monash.eric.mytestingdemo.Entity.FriendRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * This class is for friend invitation for a created event
 */

public class InviteActivity extends AppCompatActivity {

    MyFriendRowAdapter dataAdapter = null;

    private FirebaseAuth firebaseAuth;
    private Firebase mRootRef;

    //list for all frineds' names
    private ArrayList<String> names;

    //list for all frineds detial objects
    private ArrayList<FriendRow> friends;

    // list for all friends' uids
    private ArrayList<String> friend_ids;


    private String Current_userid;
    private Event passedEvent;

    private Button inviteok_btn;

    private TextView Friendlist_tv;

    String eventidreceived;
    ArrayList<String> clickedUsers;

    ValueEventListener friendRowEventListener, allFriendidEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        Firebase.setAndroidContext(this);
        firebaseAuth = FirebaseAuth.getInstance();

        Friendlist_tv = (TextView)findViewById(R.id.Friendinfomation_tv);
        inviteok_btn = (Button)findViewById(R.id.invite_btn);
        inviteok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                Log.d("arrayname",clickedUsers.size()+"");
//                Log.d("arrayname",clickedUsers.get(0));

                createEventUnderUser(clickedUsers,passedEvent);

                Intent i = new Intent(view.getContext(),MainActivity.class);
                startActivity(i);


            }
        });

        //getintent
        Intent i = getIntent();
        eventidreceived = i.getStringExtra("eventid");
        passedEvent = (Event) i.getSerializableExtra("eventobj1");

        Log.d("initent",eventidreceived);
        Log.d("initent",passedEvent.displayAll());


        //firebase set context
        Firebase.setAndroidContext(this);
        firebaseAuth = FirebaseAuth.getInstance();

        Current_userid = firebaseAuth.getCurrentUser().getUid();

        friends = new ArrayList<>();

        names = new ArrayList<>();
        friend_ids = new ArrayList<>();
        clickedUsers=new ArrayList<>();
        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyFriendRowAdapter(this,
                R.layout.friendrows, friends);
        ListView listView = (ListView) findViewById(R.id.lv_friendrows);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                FriendRow friendRow = (FriendRow) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + friendRow.getFriend_id(),
                        Toast.LENGTH_LONG).show();
            }
        });

        getAllFriends(Current_userid);
        getDetialsFromUidList(friend_ids);

    }


    // get all user' friends
    // with frined status equals 1
    private void getAllFriends(String uid) {

        Firebase userAll_node = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        // get a particular user by uid
        Firebase user_node = userAll_node.child(uid);
        final Firebase firend_node = user_node.child("Friends");

        firend_node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    HashMap<String, String> userMap = (HashMap<String, String>) child.getValue();

                    if (userMap.get("status").equals("1")) {
                        friend_ids.add(userMap.get("Friend_id"));
                    }

                }

                if(friend_ids.size()==0)
                {
                    Friendlist_tv.setText("You don't have any friends right now");
               }

            }




            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    //display friend list
    private void getDetialsFromUidList(final ArrayList<String> uidList) {
        final Firebase userAll_node = new Firebase("https://visra-1d74b.firebaseio.com/Users");


        // get a particular user by uid
        friendRowEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (String uid : uidList) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        //get all the information for a user
                        if (uid.equals(child.getKey())) {
                            FriendRow newfriendrow = buildUserFromChildNode((HashMap<String, String>) child.getValue(), child.getKey());
                            friends.add(newfriendrow);

                            dataAdapter.updateReceiptsList(friends);


                        }

                    }
                }

                // userAll_node.removeEventListener(this);
                if(friendRowEventListener!=null)
                {
                    userAll_node.removeEventListener(friendRowEventListener);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };


        userAll_node.addValueEventListener(friendRowEventListener);




    }



    private FriendRow buildUserFromChildNode(HashMap<String, String> userMap, String uid) {

        names.add(userMap.get("Username"));
//        Log.d("mylist", friend_ids.size() + "*++c*");


        FriendRow newfriendrow = new FriendRow();
        newfriendrow.setFriend_id(uid);
        newfriendrow.setFriend_name(userMap.get("Username"));
        newfriendrow.setSelected(false);
        //  dataAdapter.notifyDataSetChanged();


        return newfriendrow;
    }







    public class MyFriendRowAdapter extends ArrayAdapter<FriendRow> {

        private ArrayList<FriendRow> friendRowList;


        public MyFriendRowAdapter(Context context, int textViewResourceId, ArrayList<FriendRow> friendRowList) {
            super(context, textViewResourceId, friendRowList);
            this.friendRowList = new ArrayList<FriendRow>();
            this.friendRowList.addAll(friendRowList);
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
        }


        public void updateReceiptsList(List<FriendRow> newlist) {
            friendRowList.clear();
            friendRowList.addAll(newlist);
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return friendRowList.size();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.friendrows, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.tV_friendrow);
                holder.name = (CheckBox) convertView.findViewById(R.id.cb_friendrow);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        FriendRow friendRow = (FriendRow) cb.getTag();

                        if(cb.isChecked())
                        {
                            clickedUsers.add(friendRow.getFriend_id());
                        }
                        else if(cb.isChecked() == false)
                        {
                            clickedUsers.remove(friendRow.getFriend_id());
                        }
//                        Toast.makeText(getApplicationContext(),
//                                "Clicked on Checkbox: " + cb.getText() +
//                                        " is " + cb.isChecked(),
//                                Toast.LENGTH_LONG).show();
                        friendRow.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            FriendRow friendRow = friendRowList.get(position);
            holder.code.setText("");
            holder.name.setText(friendRow.getFriend_name());
            holder.name.setChecked(friendRow.isSelected());
            holder.name.setTag(friendRow);

            return convertView;
        }
    }


    private void createEventUnderUser(ArrayList<String> userid,Event partiEvent)
    {
        Log.d("INVITATION",userid.toString());
        Log.d("INVITATION",partiEvent.toString());
        Firebase allusernode = new Firebase("https://visra-1d74b.firebaseio.com/Users");
        for(String uideach :userid)
        {

            Firebase usernode = allusernode.child(uideach);

            Firebase userEventnd = usernode.child("Event");

            Firebase userEventnode = userEventnd.child(eventidreceived);



            Firebase grandChildRef1 = userEventnode.child("Title");
            grandChildRef1.setValue(partiEvent.getTitle());
            Firebase grandChildRef2 = userEventnode.child("Venue");
            grandChildRef2.setValue(partiEvent.getVenue());
            Firebase grandChildRef3 = userEventnode.child("Date");
            grandChildRef3.setValue(partiEvent.getDate());
            Firebase grandChildRef4 = userEventnode.child("Time");
            grandChildRef4.setValue(partiEvent.getTime());
            Firebase grandChildRef5 = userEventnode.child("Sport");
            grandChildRef5.setValue(partiEvent.getSport());
            Firebase grandChildRef6 = userEventnode.child("Desc");
            grandChildRef6.setValue(partiEvent.getDescription());
            Firebase grandChildRef7 = userEventnode.child("Host");
            grandChildRef7.setValue(partiEvent.getHostId());
            Firebase grandChildRef8 = userEventnode.child("Latitude");
            grandChildRef8.setValue(partiEvent.getLatitute());
            Firebase grandChildRef9 = userEventnode.child("Longtitue");
            grandChildRef9.setValue(partiEvent.getLongtitue());
            Firebase grandChildRef10 = userEventnode.child("Status");
            grandChildRef10.setValue("pending");



        }
    }



}