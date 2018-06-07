package com.example.mingi.management.DrivingJoin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mingi.management.DrivingJoin.ListMapActivity;
import com.example.mingi.management.DrivingJoin.Listitem;
import com.example.mingi.management.R;

import java.util.ArrayList;

/**
 * Created by MINGI on 2018-05-14.
 */

class LocationAdapter extends BaseAdapter {
    Context maincon;
    ArrayList<Listitem> datas;
    LayoutInflater inflater;

    public LocationAdapter(Context maincon, ArrayList<Listitem> datas, LayoutInflater inflater) {
        this.maincon = maincon;
        this.datas = datas;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, null);
        }
        TextView name_pos = (TextView)convertView.findViewById(R.id.textViewHead);
        TextView addr_pos = (TextView)convertView.findViewById(R.id.textViewDesc);
        ImageView checkBtn = (ImageView) convertView.findViewById(R.id.mapCheckBtn);

        checkBtn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent goMap = new Intent(maincon, ListMapActivity.class);
                goMap.putExtra("lat", datas.get(position).getLat());
                goMap.putExtra("lon", datas.get(position).getLon());
                goMap.putExtra("name", datas.get(position).getName());
                maincon.startActivity(goMap);
            }
        });

        name_pos.setText(datas.get(position).getName());
        addr_pos.setText(datas.get(position).getFullAddr());

        return convertView;
    }
}