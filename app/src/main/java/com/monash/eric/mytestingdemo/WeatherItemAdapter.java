package com.monash.eric.mytestingdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.monash.eric.mytestingdemo.Entity.WeatherForeCast;
import java.util.ArrayList;

/**
 * Created by IBM on 6/09/2016.
 */
public class WeatherItemAdapter extends BaseAdapter {

    private static ArrayList<WeatherForeCast> weatherforcastItems;

    private LayoutInflater mInflater;

    public WeatherItemAdapter(Context context, ArrayList<WeatherForeCast> results) {
        weatherforcastItems = results;
        mInflater = LayoutInflater.from(context);
    }



    public int getCount() {
        return weatherforcastItems.size();
    }

    public Object getItem(int position) {
        return weatherforcastItems.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.weather_items, null);
            holder = new ViewHolder();
            holder.txtDate = (TextView) convertView.findViewById(R.id.tv_date_forecast);
            holder.txtWeatherType = (TextView) convertView
                    .findViewById(R.id.tv_weather_type);
            holder.txtMaxTemp = (TextView) convertView.findViewById(R.id.tv_max_temp);
            holder.txtMinTemp = (TextView) convertView.findViewById(R.id.tv_min_temp);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtDate.setText(weatherforcastItems.get(position).getDate());
        holder.txtWeatherType.setText(weatherforcastItems.get(position)
                .getWeather());
        holder.txtMaxTemp.setText(weatherforcastItems.get(position).getTemperatue_max());
        holder.txtMinTemp.setText(weatherforcastItems.get(position).getTemperatue_min());


        return convertView;
    }

    static class ViewHolder {
        TextView txtDate;
        TextView txtMinTemp;
        TextView txtMaxTemp;
        TextView txtWeatherType;

    }
}