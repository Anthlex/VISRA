package com.monash.eric.mytestingdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        firebaseAuth = FirebaseAuth.getInstance();

        Button buttonRegister;
        Button buttonLogin;
        Button buttonCancel;

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        progressDialog = new ProgressDialog(this);

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

                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }
        });

    }

    private void login() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){

            //email is empty
            Toast.makeText(this,"Please enter email", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if(TextUtils.isEmpty(password)){

            //password is empty
            Toast.makeText(this,"Please enter password", Toast.LENGTH_SHORT).show();
            //stopping the function execution further

            return;
        }

        //if validation is okw
        //show the progress bar

        progressDialog.setMessage("Login...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressDialog.dismiss();

                if(task.isSuccessful()){
                    //start profile activity

                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                }else{
                    Toast.makeText(LoginActivity.this,"Email/Password is not correct.",Toast.LENGTH_SHORT).show();
                }



            }
        });



    }

    private void register() {

        String email = editTextEmail.getText().toString().trim();

        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){

            //email is empty
            Toast.makeText(this,"Please enter email", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if(TextUtils.isEmpty(password)){

            //password is empty
            Toast.makeText(this,"Please enter password", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        //if validation is ok
        //show the progress bar

        progressDialog.setMessage("Registering...");
        progressDialog.show();

        //create a user
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressDialog.dismiss();

                if(task.isSuccessful()){
                    //user is successfully registered and logged in
                    //we will start the profile activity here
                    //right now lets display a toast only
                    startActivity(new Intent(LoginActivity.this,RegisterActivity.class));

                }else{
                    Toast.makeText(LoginActivity.this,"Can't register.. Please try again",Toast.LENGTH_SHORT).show();
                }
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

}
