package com.monash.eric.mytestingdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    private SignInButton buttonGoogle;

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LoginActivity";
    private GoogleApiClient mGoogleApiClient;

    private String username;

    private String uid;
    private TextView resetPassword;

    private Firebase mRootRef;

    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // init sharedpreference editor
        SharedPreferences sharedPreferences = getSharedPreferences("userProfile",MODE_PRIVATE);
        editor = sharedPreferences.edit();



        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {

            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();


        }

        Firebase.setAndroidContext(this);


        mRootRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");


        Button buttonRegister;
        Button buttonLogin;
        Button buttonCancel;

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);

        buttonGoogle = (SignInButton) findViewById(R.id.buttonGoogle);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        resetPassword = (TextView) findViewById(R.id.resetPassword);

        progressDialog = new ProgressDialog(this);


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(LoginActivity.this, "You got a error", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(AppIndex.API).build();


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login();

            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                register();

            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });


        buttonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signIn();
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });


    }

    private void resetPassword() {

        startActivity(new Intent(LoginActivity.this, ResetActivity.class));
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void login() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {

            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if (TextUtils.isEmpty(password)) {

            //password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            //stopping the function execution further

            return;
        }

        //if validation is okw
        //show the progress bar

        progressDialog.setMessage("Login...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressDialog.dismiss();

                if (task.isSuccessful()) {
                    //start profile activity
                    getUserName(firebaseAuth.getCurrentUser().getUid());

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "Email/Password is not correct.", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();


                String email = account.getEmail();

                username = account.getGivenName();


                if (email.matches("(\\W|^)[\\w.+\\-]{0,25}@(student.monash|monash)\\.edu(\\W|$)") == false) {

                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(Status status) {
                                    // ...
                                }
                            });

                    startActivity(new Intent(this, LoginActivity.class));

                    Toast.makeText(this, "Please use Monash Email", Toast.LENGTH_SHORT).show();

                    return;

                }


                firebaseAuthWithGoogle(account);
            } else {

                Toast.makeText(this, "Google Sign In failed", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(this, LoginActivity.class));

                // Google Sign In failed, update UI appropriately
                // ...
            }

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        progressDialog.setMessage("Login...");
        progressDialog.show();


        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential)

                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressDialog.dismiss();

                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());


                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        uid = firebaseAuth.getCurrentUser().getUid();

                        Firebase childRef = mRootRef.child(uid);

                        Firebase grandChildRef1 = childRef.child("Username");
                        grandChildRef1.setValue(username);

                        Firebase grandChildRef2 = childRef.child("Url");
                        grandChildRef2.setValue("");

                        getUserName(firebaseAuth.getCurrentUser().getUid());

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        // ...
                    }
                });
    }


    private void register() {

        String email = editTextEmail.getText().toString().trim();

        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {

            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if (TextUtils.isEmpty(password)) {

            //password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if (email.matches("(\\W|^)[\\w.+\\-]{0,25}@(student.monash|monash)\\.edu(\\W|$)") == false) {

            Toast.makeText(this, "Please use Monash Email", Toast.LENGTH_SHORT).show();

            return;

        }

        //if validation is ok
        //show the progress bar

        progressDialog.setMessage("Registering...");
        progressDialog.show();

        //create a user
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressDialog.dismiss();

                if (task.isSuccessful()) {
                    //user is successfully registered and logged in
                    //we will start the profile activity here
                    //right now lets display a toast only
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

                } else {
                    Toast.makeText(LoginActivity.this, "Can't register.. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void updateUserName(String username) {

        SharedPreferences sharedPreferences = getSharedPreferences("userProfile", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putString("Username", username);
        editor.commit();
    }

    private void getUserName(final String uid) {


        Firebase userAll_node = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        userAll_node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {


                    String loopUid = child.getKey();

                    //skiiing all the request send by user, only get the request from others
                    if (loopUid.equals(uid)) {
                        HashMap<String, String> userMap = (HashMap<String, String>) child.getValue();
                        username = userMap.get("Username");
                        updateUserName(username);

                    }

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    private void updateUserInterest(String uid)
    {

        SharedPreferences sharedPreferences = getSharedPreferences("userProfile",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mRootRef = new Firebase("https://visra-1d74b.firebaseio.com/Users");

        Firebase usernode = mRootRef.child(uid);
        Firebase sportnode = usernode.child("Sports");



        sportnode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child: dataSnapshot.getChildren()) {

                    HashMap<String, String> userMap = (HashMap<String, String>) child.getValue();

                    String sports = userMap.get("Sports");

                    Log.d(TAG,sports + "**");

                    editor.putString("interest",sports);
                    editor.commit();


                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
}
