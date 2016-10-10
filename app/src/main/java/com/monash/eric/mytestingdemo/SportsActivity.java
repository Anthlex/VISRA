package com.monash.eric.mytestingdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class SportsActivity extends AppCompatActivity {

    public static final String TAG = "SportsActivity";

    private CheckBox checkBox1,checkBox2,checkBox3,checkBox4,checkBox5,checkBox6,checkBox7,checkBox8,checkBox9,
            checkBox10;

    private Button buttonSave;

    private Firebase mRootRef;
    private FirebaseAuth firebaseAuth;

    private String uid;

    private String result="";

    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //profile activity here
            uid = firebaseAuth.getCurrentUser().getUid();
        }

        Firebase.setAndroidContext(this);

        mRootRef = new Firebase("https://visra-1d74b.firebaseio.com/Users/"+uid);

        addListenerOnButton();

        //store init a sharedpreference object
        SharedPreferences sharedPreferences = getSharedPreferences("userProfile",MODE_PRIVATE);
        editor = sharedPreferences.edit();



    }

    public void addListenerOnButton(){



        checkBox1 = (CheckBox) findViewById(R.id.checkbox1);
        checkBox2 = (CheckBox) findViewById(R.id.checkbox2);
        checkBox3 = (CheckBox) findViewById(R.id.checkbox3);
        checkBox4 = (CheckBox) findViewById(R.id.checkbox4);
        checkBox5 = (CheckBox) findViewById(R.id.checkbox5);
        checkBox6 = (CheckBox) findViewById(R.id.checkbox6);
        checkBox7 = (CheckBox) findViewById(R.id.checkbox7);
        checkBox8 = (CheckBox) findViewById(R.id.checkbox8);
        checkBox9 = (CheckBox) findViewById(R.id.checkbox9);
        checkBox10 = (CheckBox) findViewById(R.id.checkbox10);
        if(checkBox10.isChecked()){
            checkBox10.setButtonDrawable(R.drawable.bicycle);
            return;
        }



        buttonSave = (Button) findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkBox1.isChecked()){

                    setResult(getResult()+","+checkBox1.getText().toString());
                }

                if(checkBox2.isChecked()){

                    setResult(getResult()+","+checkBox2.getText().toString());
                }

                if(checkBox3.isChecked()){

                    setResult(getResult()+","+checkBox3.getText().toString());
                }

                if(checkBox4.isChecked()){

                    setResult(getResult()+","+checkBox4.getText().toString());
                }

                if(checkBox5.isChecked()){

                    setResult(getResult()+","+checkBox5.getText().toString());
                }

                if(checkBox6.isChecked()){

                    setResult(getResult()+","+checkBox6.getText().toString());
                }

                if(checkBox7.isChecked()){

                    setResult(getResult()+","+checkBox7.getText().toString());
                }

                if(checkBox8.isChecked()){

                    setResult(getResult()+","+checkBox8.getText().toString());
                }

                if(checkBox9.isChecked()){

                    setResult(getResult()+","+checkBox9.getText().toString());
                }

                if(checkBox10.isChecked()){

                    setResult(getResult()+","+checkBox10.getText().toString());
                }


                Firebase childRef = mRootRef.child("Sports");

                if(getResult() == ""){
                    setResult(" Not set");
                }

                childRef.setValue(result.substring(1));

                editor.putString("interest",result.substring(1));
                editor.commit();
                SharedPreferences sharedPreferences = getSharedPreferences("userProfile",MODE_PRIVATE);
                Log.d(TAG,sharedPreferences.getString("interest","N/A"));

                startActivity(new Intent(SportsActivity.this,MainActivity.class));

            }
        });

    }

    public void setResult(String text) {

        result = text;

    }

    public String getResult(){

        return result;
    }
}
