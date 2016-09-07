package com.monash.eric.mytestingdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.monash.eric.mytestingdemo.Entity.Users;

public class UserDetailActivity extends AppCompatActivity {

    private TextView textViewUsername;
    private TextView textViewGender;
    private TextView textViewBirthday;
    private TextView textViewCountry;
    private TextView textViewSports;


    private Button buttonOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        textViewUsername = (TextView)findViewById(R.id.textViewUsername);
        textViewGender = (TextView)findViewById(R.id.textViewGender);
        textViewBirthday = (TextView)findViewById(R.id.textViewBirthday);
        textViewCountry = (TextView)findViewById(R.id.textViewCountry);
        textViewSports = (TextView)findViewById(R.id.textViewSports);
        buttonOk = (Button)findViewById(R.id.buttonCancel);



        Intent i = getIntent();
        Users users = (Users) i.getSerializableExtra("eventObj");

        textViewSports.setText(users.getSports());
        textViewUsername.setText(users.getUsername());
        textViewBirthday.setText(users.getBirthday());
        textViewGender.setText(users.getGender());
        textViewCountry.setText(users.getCountry());

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserDetailActivity.this,MainActivity.class));
            }
        });





    }
}
