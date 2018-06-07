package com.example.mingi.management.BusinessCard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mingi.management.R;

public class BCDetailNoAddActivity extends AppCompatActivity {
    ImageView imgView, emailBtn, callBtn, msgBtn, bcfullmap;
    TextView bcname, bclevel, bccom, bcphone, bcemail, bcadd, bcno;

    String bcname_str, bclevel_str, bccom_str, bcphone_str, bcemail_str, bcadd_str, bclat_str, bclon_str, bcphoto_str, no;
    String isGPSEnable, nowLat, nowLon, nowName, userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bcdetail_no_add);
    }
}
