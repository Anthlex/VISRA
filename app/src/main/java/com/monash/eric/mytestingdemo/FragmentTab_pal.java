package com.monash.eric.mytestingdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.google.firebase.auth.FirebaseAuth;
import com.monash.eric.mytestingdemo.Entity.Users;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IBM on 15/08/2016.
 */
public class FragmentTab_pal extends Fragment{



    private FirebaseAuth firebaseAuth;
    private Firebase mRootRef;

    private ProgressDialog progressDialog;


    private ListView list_users;

    private ArrayList<String> userTitle;
    private ArrayAdapter<String> adapter;
    private ArrayList<Users> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout_pal, null);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        Firebase.setAndroidContext(getActivity());


        userTitle = new ArrayList<>();
        userList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1,userTitle);
        list_users = (ListView)view.findViewById(R.id.listView);

        list_users.setAdapter(adapter);
        firebaseAuth = FirebaseAuth.getInstance();



        mRootRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot child: dataSnapshot.getChildren()) {

                    HashMap<String, String> userMap = (HashMap<String, String>) child.getValue();

                    String username = userMap.get("Username");
                    String gender = userMap.get("Gender");
                    String birthday = userMap.get("Birthday");
                    String country = userMap.get("Country");
                    String sports = userMap.get("Sports");

                    Users users = new Users(username,gender,birthday,country,sports);
                    userList.add(users);
                    userTitle.add(username);


                }

                progressDialog.dismiss();

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        list_users.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(getActivity(), UserDetailActivity.class);
                Users selectedEvent = userList.get(position);
                intent.putExtra("eventObj",selectedEvent);
                startActivity(intent);
            }
        });

        return view;
    }

}

