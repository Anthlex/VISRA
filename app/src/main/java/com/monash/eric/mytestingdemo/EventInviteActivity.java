package com.monash.eric.mytestingdemo;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.monash.eric.mytestingdemo.Entity.Event;

import java.util.ArrayList;
import java.util.HashMap;

public class EventInviteActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button createEventBtn;
    private Button showAllBtn;
    private Firebase mRootRef;
    private TextView tv_result;
    private ArrayList<String> eventIdList;



    private ListView list_events;

    private ArrayList<String> eventTitle;
    private ArrayAdapter<String> adapter;
    private ArrayList<Event> eventList;


    private String uid;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_invite);

        firebaseAuth = FirebaseAuth.getInstance();

        uid = firebaseAuth.getCurrentUser().getUid();

        Firebase.setAndroidContext(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        eventTitle = new ArrayList<>();
        eventList = new ArrayList<>();
        eventIdList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,eventTitle);
        list_events = (ListView)findViewById(R.id.listView_reqs);

        list_events.setAdapter(adapter);
        firebaseAuth = FirebaseAuth.getInstance();

        mRootRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");
        Firebase mRef = mRootRef.child(uid).child("Event");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot child: dataSnapshot.getChildren()) {


                    Log.d("testingstr",child.getValue().toString());
                    HashMap<String, String> userMap = (HashMap<String, String>) child.getValue();

                    if(userMap == null){
                        Toast.makeText(EventInviteActivity.this,"No invite",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String eventid = child.getKey();
                        String description = userMap.get("Desc");
                        String date = userMap.get("Date");
                        String time = userMap.get("Time");
                        String venue = userMap.get("Venue");
                        String sport = userMap.get("Sport");
                        String hostId = userMap.get("Host");
                        String title = userMap.get("Title");
                        String latitute = userMap.get("Latitude");
                        String longtitue = userMap.get("Longtitue");


//                        if (userMap != null) {
//                            Log.d("usermaptes", userMap.toString());
//                        }
                        //    if (userMap.get("Status").equals("pending")) {
                        if (userMap.containsKey("Status")) {

                            if(userMap.get("Status").equals("pending"))
                            {
                                Event event = new Event(description, date, time, venue, sport, title, hostId, latitute, longtitue);
                                eventList.add(event);
                                eventTitle.add(title);
                                eventIdList.add(eventid);
                            }


                        }

                        //ß}

                    }

                }

                progressDialog.dismiss();

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



        list_events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(EventInviteActivity.this, InviteDetailActivity.class);
                Event selectedEvent = eventList.get(position);
                String selectID = eventIdList.get(position);
                intent.putExtra("eventObj",selectedEvent);
                intent.putExtra("selectID",selectID);
                startActivity(intent);
            }
        });

    }
}
