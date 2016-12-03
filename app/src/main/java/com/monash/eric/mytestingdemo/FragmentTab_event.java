package com.monash.eric.mytestingdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.monash.eric.mytestingdemo.Entity.Event;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * FragmentTab_event is used to display all the public events
 *
 * @author  Eric
 * @since 1.0
 */
public class FragmentTab_event extends Fragment {


    //debug tag
    public static final String TAG ="FragmentTab_event";
    //firebase Event root
    private Firebase mRootRef;
    //define an arraylist to store event id
    private ArrayList<String> eventIdList;
    //define a listview object
    private ListView list_events;
    //define an arraylist to store event title
    private ArrayList<String> eventTitle;
    private ArrayAdapter<String> adapter;
    //define an arraylist to store event objects
    private ArrayList<Event> eventList;

    //define a progressdialog
    private ProgressDialog progressDialog;

    ValueEventListener valueEventListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_layout_events, null);
        Firebase.setAndroidContext(getActivity());

        Log.d("life","oncreateview");


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        eventTitle = new ArrayList<>();
        eventList = new ArrayList<>();
        eventIdList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1,eventTitle);
        list_events = (ListView)view.findViewById(R.id.listView);

        list_events.setAdapter(adapter);

        mRootRef = new Firebase("https://visra-1d74b.firebaseio.com/Event");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot child: dataSnapshot.getChildren()) {

                    HashMap<String, String> userMap = (HashMap<String, String>) child.getValue();

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




                    if (userMap.containsKey("Status")) {

                        if (userMap.get("Status").equals("1")) {

                            Event event = new Event(description, date, time, venue, sport, title, hostId, latitute, longtitue);
                            eventList.add(event);
                            eventTitle.add(title);
                            eventIdList.add(eventid);
                        }
                    }

                }

                if(valueEventListener != null) {
                    mRootRef.removeEventListener(valueEventListener);
                }

                progressDialog.dismiss();

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        mRootRef.addValueEventListener(valueEventListener);


        list_events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(getActivity(), EventDetailActivity.class);
                Event selectedEvent = eventList.get(position);
                String selectID = eventIdList.get(position);
                intent.putExtra("eventObj",selectedEvent);
                intent.putExtra("selectID",selectID);
                startActivity(intent);
            }
        });



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("life","onstart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("life","onresume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("life","onpause");

    }

    @Override
    public void onStop() {
        super.onStop();
        if(valueEventListener !=null) {
            mRootRef.removeEventListener(valueEventListener);
        }
        Log.d("life","onstop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("life","ondestory");
    }

    public void clearList()
    {
        eventTitle.clear();
        eventIdList.clear();
        eventList.clear();

    }
}