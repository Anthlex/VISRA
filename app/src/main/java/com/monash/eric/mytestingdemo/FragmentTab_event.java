package com.monash.eric.mytestingdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by IBM on 15/08/2016.
 */
public class FragmentTab_event extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_layout_events, null);
//        View v = inflater.inflate(R.layout.fragment_layout_facilities, container, false);
//        TextView tv = (TextView) v.findViewById(R.id.text);
//        tv.setText(this.getTag() + " Content");
//        return v;

//        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
//            return inflater.inflate(R.layout.fragment_1, null);
//        }
    }
}
