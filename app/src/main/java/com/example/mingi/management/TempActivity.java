package com.example.mingi.management;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class TempActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        TextView id_, carNum_, startPlace_ , endPlace_;
        TextView startTime_ , startDay_, endTime_ , endDay_, no_ , kilometer_;

        id_ = (TextView) findViewById(R.id.id);
        carNum_ =  (TextView) findViewById(R.id.carNum);
        startPlace_ = (TextView) findViewById(R.id.startPlace);
        endPlace_ =  (TextView) findViewById(R.id.endPlace);
        startTime_ = (TextView) findViewById(R.id.startTime);
        startDay_ =  (TextView) findViewById(R.id.startDay);

        endTime_ = (TextView) findViewById(R.id.endTime);
        endDay_ =  (TextView) findViewById(R.id.endDay);
        no_ = (TextView) findViewById(R.id.no);
        kilometer_ =  (TextView) findViewById(R.id.kilometer);


        Intent intent = getIntent();

        String id =intent.getExtras().getString("id");
        String carNum =intent.getExtras().getString("carNum");
        String startPlace =intent.getExtras().getString("startPlace");
        String endPlace =intent.getExtras().getString("endPlace");
        String startTime = intent.getExtras().getString("startTime");
        String startDay =intent.getExtras().getString("startDay");
        String endTime =intent.getExtras().getString("endTime");
        String endDay =intent.getExtras().getString("endDay");
        String no =intent.getExtras().getString("no");
        String kilometer = intent.getExtras().getString("kilometer");


        String isGPSEnable = intent.getExtras().getString("isGPSEnable");
        String nowLat = intent.getExtras().getString("nowLat");
        String nowLon = intent.getExtras().getString("nowLon");
        String nowName = intent.getExtras().getString("nowName");



        id_.setText(id);
        carNum_.setText(carNum);
        startPlace_.setText(startPlace);
        endPlace_.setText(endPlace);
        startTime_.setText(startTime);
        startDay_.setText(startDay);
        endTime_.setText(endTime);
        endDay_.setText(endDay);
        no_.setText(no);
        kilometer_.setText(kilometer);



    }
}
