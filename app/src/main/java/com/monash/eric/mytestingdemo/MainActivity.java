package com.monash.eric.mytestingdemo;

import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //Define a FragmentTabHost Object
    private FragmentTabHost mTabHost;

    //Define a LayoutInflater
    private LayoutInflater layoutInflater;

    //Define a array for all fragments
    private Class fragmentArray [] = {FragmentTab_facility.class,FragmentTab_event.class,FragmentTab_pal.class, FragmentTab_user.class};

    //Define a array of images for tabs
    private int imageArray[] = {R.drawable.tab_facilities_btn,R.drawable.tab_event_btn,R.drawable.tab_pal_btn,R.drawable.tab_user_btn};

    //Define a array of words for tabs
    private String textArray[] = {"Facilities","Events","Pals","Me"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
       // getActionBar().setDisplayHomeAsUpEnabled(false);
        if(getSupportActionBar()==null)
        {
            System.out.println("actionbar null");
        }
        else{
            System.out.println("not null");

        }

//        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
//        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
//
//        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("Tab 1", null), FragmentTab_facility.class, null);
//        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("Tab 2", null), FragmentTab_facility.class, null);
//        mTabHost.addTab(mTabHost.newTabSpec("tab3").setIndicator("Tab 3", null), FragmentTab_facility.class, null);
//        mTabHost.addTab(mTabHost.newTabSpec("tab4").setIndicator("Tab 4", null), FragmentTab_facility.class, null);

        initView();

    }

    //initialize view
    private void initView()
    {
        layoutInflater = LayoutInflater.from(this);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realContent);

        int count = fragmentArray.length;

        for(int i = 0; i < count; i++){
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(textArray[i]).setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, fragmentArray[i], null);

          //  mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }
    }

    private View getTabItemView(int index){
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(imageArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(textArray[index]);

        return view;
    }




}



