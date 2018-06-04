package com.example.mingi.management;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class BCFullMapActivity extends AppCompatActivity {
    String lat, lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bcfull_map);
        if (Build.VERSION.SDK_INT >= 21) {
            getSupportActionBar().hide();
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        Intent outIntent = getIntent();
        lat = outIntent.getStringExtra("lat");
        lon = outIntent.getStringExtra("lon");

        if (savedInstanceState == null) {
            MainFragment mainFragment = new MainFragment();
            Bundle bundle = new Bundle(2); // # of datas

            bundle.putString("lat", lat);
            bundle.putString("lon", lon);
            mainFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFragment3, mainFragment, "main")
                    .commit();
        }
    }
}
