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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.monash.eric.mytestingdemo.Entity.Event;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IBM on 15/08/2016.
 */
public class FragmentTab_event extends Fragment {


    public static final String TAG ="FragmentTab_event";
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

    private long eventCount;

    ValueEventListener valueEventListener;






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_layout_events, null);
        Firebase.setAndroidContext(getActivity());

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        eventTitle = new ArrayList<>();
        eventList = new ArrayList<>();
        eventIdList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1,eventTitle);
        list_events = (ListView)view.findViewById(R.id.listView);

        list_events.setAdapter(adapter);
        firebaseAuth = FirebaseAuth.getInstance();

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
    public void onPause() {
        super.onPause();
        if(valueEventListener !=null) {
            mRootRef.removeEventListener(valueEventListener);
        }

        Log.d("listsize(events)",eventTitle.size()+"");
        //  clearList();

    }


    public void clearList()
    {
        eventTitle.clear();
        eventIdList.clear();
        eventList.clear();

    }
}