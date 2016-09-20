package com.monash.eric.mytestingdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.core.Tag;
import com.google.firebase.auth.FirebaseAuth;
import com.monash.eric.mytestingdemo.Entity.Users;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IBM on 15/08/2016.
 */
public class FragmentTab_pal extends Fragment{


    public final static String DEF_INTEREST_NULL = "N/A";
    public final static String TAG = "FragmentTab_pal";


    private FirebaseAuth firebaseAuth;
    private Firebase mRootRef;

    private ProgressDialog progressDialog;


    private ListView list_users;
    private Button friendReq_Btn;
    private Button viewFriend_btn;

    private ArrayList<String> userTitle;
    private ArrayAdapter<String> adapter;
    private ArrayList<Users> userList;

    //list of people with similar interest
    private ArrayList<Users> similar_interest_users;

    //arraylist to store current user interests
    private ArrayList<String> curr_user_interest;

    private String userKey;
    //sharedpreference object
    SharedPreferences sharedPreferences;
    // SharedPreferences.Editor editor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout_pal, null);

        friendReq_Btn = (Button)view.findViewById(R.id.button_FriendRequests);
        friendReq_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),FriendRequestActivity.class);
                startActivity(intent);
            }
        });


        viewFriend_btn = (Button)view.findViewById((R.id.button_viewFriends));
        viewFriend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO SATRT NEW ACTIVITY
                Intent intent = new Intent(getActivity(),MyFreindListActivity.class);
                startActivity(intent);
            }
        });
        //get sharedpreferences
        sharedPreferences = getActivity().getSharedPreferences("userProfile",getActivity().MODE_PRIVATE);

        String interests = sharedPreferences.getString("interest",DEF_INTEREST_NULL);


        //init user interest list
        curr_user_interest = new ArrayList<>();
        //convert string to array
        String[] interests_array = interests.split(",");
        //populate arraylist with array
        for(String s :interests_array)
        {
            curr_user_interest.add(s);
        }

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

        userKey = firebaseAuth.getCurrentUser().getUid();

        mRootRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot child: dataSnapshot.getChildren()) {

                    HashMap<String, String> userMap = (HashMap<String, String>) child.getValue();
                    if(child.getKey().equals(userKey))
                    {

                        continue;
                    }

                    String user_id = child.getKey();
                    String username = userMap.get("Username");
                    String gender = userMap.get("Gender");
                    String birthday = userMap.get("Birthday");
                    String country = userMap.get("Country");
                    String sports = userMap.get("Sports");

                    Users users = new Users(username,gender,birthday,country,sports,user_id);
                    userList.add(users);

                }

                getSimilarUser(userList, curr_user_interest);

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
                Users selectedEvent = similar_interest_users.get(position);

                Log.d(TAG,position+"");
                Log.d(TAG,similar_interest_users.get(position).getUsername());
                intent.putExtra("eventObj",selectedEvent);
                startActivity(intent);
            }
        });

        return view;
    }


    private ArrayList<Users> getSimilarUser(ArrayList<Users> allUsers, ArrayList<String> current_user_interest)
    {

        similar_interest_users = new ArrayList<Users>();

        if(allUsers == null)
        {
            Log.d(TAG,"alluser list empty");
            return similar_interest_users;

        }
        for(Users user : allUsers)
        {
            ArrayList<String> interests_from_user = new ArrayList<>();
            if(user.getSports() != null) {
                // store all interst from a user in the list.
                String[] temp_interest = user.getSports().split(",");
                for (String s : temp_interest) {
                    interests_from_user.add(s);

                }

                //get the interests intersection
                interests_from_user.retainAll(current_user_interest);

                if (interests_from_user.size() > 0) {
                    similar_interest_users.add(user);
                    userTitle.add(user.getUsername());
                }
            }



        }
        return similar_interest_users;
    }

    //testing method for list
    public void showAllInList(ArrayList<Users> users)
    {
        for(Users user : users)
        {
            Log.d(TAG,user.getUsername());
        }
    }

}

