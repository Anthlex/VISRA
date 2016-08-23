package com.monash.eric.mytestingdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by IBM on 19/08/2016.
 */
public class MyAdapter extends BaseAdapter {
    private final ArrayList mData;

    public MyAdapter(Map<String, String> map) {
        mData = new ArrayList();
        mData.addAll(map.entrySet());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String, String> getItem(int position) {
        System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        System.out.println((Map.Entry) mData.get(position));
        System.out.println("position");
        System.out.println(position);
        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO implement you own logic with ID
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;
        final Context context = parent.getContext();
        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_adapter_item, parent, false);
        } else {
            result = convertView;
        }

        Map.Entry<String, String> item = getItem(position);

        // TODO replace findViewById by ViewHolder
        ((TextView) result.findViewById(R.id.textView_item)).setText(item.getKey());
//        ((TextView) result.findViewById(android.R.id.text2)).setText(item.getValue());

//        result.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "from click", Toast.LENGTH_SHORT).show();
//            }
//        });

        return result;
    }
}
