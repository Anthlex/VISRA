package com.monash.eric.mytestingdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Map;


/**
 * Created by IBM on 15/08/2016.
 */
public class FragmentTab_user extends Fragment {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Firebase mRef;

    private Button buttonLogout;
    private Button buttonProfile;
    private Button viewFriend_btn;
    private Button viewEvent_btn;

    private TextView textViewHello;

    private ImageButton imageView;
    private Button upload;

    private GoogleApiClient mGoogleApiClient;

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_INTENT = 2;

    private StorageReference mStorage;

    private ProgressDialog progressDialog;

    private String uid;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout_account, null);

        Firebase.setAndroidContext(this.getContext());


        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getActivity());



        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {

                    startActivity(new Intent(getActivity(), LoginActivity.class));

                }
                else{
                    successful();

                    uid = firebaseAuth.getCurrentUser().getUid();

                    mStorage.child("Photos").child(uid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {


                            Picasso.with(getContext()).load(uri).fit().rotate(90f).centerCrop().into(imageView);
                            //Picasso.with(getContext()).load(uri).rotate(90f).centerCrop().resize(360,240).into(imageView);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            //Toast.makeText(getContext(),"You haven't set your avatar",Toast.LENGTH_SHORT).show();
                        }
                    });

                    mGoogleApiClient.connect();

                }
            }
        };

        progressDialog.setMessage("Loading...");
        progressDialog.show();

        mStorage = FirebaseStorage.getInstance().getReference();

        buttonLogout = (Button) view.findViewById(R.id.buttonLogout);
        buttonProfile = (Button) view.findViewById(R.id.buttonProfile);
        textViewHello = (TextView) view.findViewById(R.id.hello);

        imageView = (ImageButton) view.findViewById(R.id.imageView);
        upload = (Button) view.findViewById(R.id.upload);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent,CAMERA_REQUEST_CODE);


            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                Intent intent = new Intent(Intent.ACTION_PICK);

                intent.setType("image/*");

                startActivityForResult(intent, GALLERY_INTENT);

            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(getActivity(), "You got a error", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(AppIndex.API).build();

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();

                Auth.GoogleSignInApi.signOut(mGoogleApiClient);

                startActivity(new Intent(getActivity(), LoginActivity.class));

            }
        });

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), AccountActivity.class));
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

        viewEvent_btn = (Button)view.findViewById(R.id.button_viewEvent);
        viewEvent_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),MyEventActivity.class);
                startActivity(intent);
            }
        });

        progressDialog.dismiss();

        return view;
    }

    private void successful() {



        FirebaseUser user = firebaseAuth.getCurrentUser();


        String uid = user.getUid();

        mRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");


        mRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Map<String,String> map = dataSnapshot.getValue(Map.class);

                if(map == null){

                    textViewHello.setText("Hello !");

                }else {


                    String username = map.get("Username");

                    if (username == "") {

                        textViewHello.setText("Hello !");

                    }

                    textViewHello.setText("Hello " + username + "!");
                }


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == CAMERA_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {


            progressDialog.setMessage("Uploading");
            progressDialog.show();

            Uri uri = data.getData();

            StorageReference filepath = mStorage.child("Photos").child(uid);

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    uid = firebaseAuth.getCurrentUser().getUid();

                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    String url = downloadUri.toString();

                    Picasso.with(getContext()).load(downloadUri).fit().centerCrop().rotate(90f).into(imageView);
                   // Picasso.with(getContext()).load(downloadUri).rotate(90f).centerCrop().resize(360,240).into(imageView);

                    Firebase childRef = mRef.child(uid);

                    Firebase grandChildRef1 = childRef.child("Url");
                    grandChildRef1.setValue(url);

                    mGoogleApiClient.connect();

                    progressDialog.dismiss();

                    Toast.makeText(getContext(),"Upload finished",Toast.LENGTH_SHORT).show();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {


                    mGoogleApiClient.connect();

                }
            });
        }

        if(requestCode == GALLERY_INTENT && resultCode == getActivity().RESULT_OK){

            progressDialog.setMessage("Uploading");
            progressDialog.show();

            Uri uri = data.getData();

            StorageReference filePath = mStorage.child("Photos").child(uid);

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    uid = firebaseAuth.getCurrentUser().getUid();

                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    String url = downloadUri.toString();

                    Picasso.with(getContext()).load(downloadUri).fit().centerCrop().rotate(90f).into(imageView);
                   // Picasso.with(getContext()).load(downloadUri).resize(360,240).centerCrop().rotate(90f).into(imageView);

                    Firebase childRef = mRef.child(uid);

                    Firebase grandChildRef1 = childRef.child("Url");
                    grandChildRef1.setValue(url);

                    mGoogleApiClient.connect();

                    progressDialog.dismiss();

                    Toast.makeText(getActivity(),"Upload Done", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    mGoogleApiClient.connect();

                    progressDialog.dismiss();

                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

}
