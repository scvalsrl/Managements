package com.example.mingi.management;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class BCFullMapActivity extends AppCompatActivity {
    String lat, lon, name;
    Button minimizeBtn;

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
        name = outIntent.getStringExtra("name");

        if (savedInstanceState == null) {
            MainFragment mainFragment = new MainFragment();
            Bundle bundle = new Bundle(2); // # of datas

            bundle.putString("lat", lat);
            bundle.putString("lon", lon);
            bundle.putString("name", name);
            mainFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFragment3, mainFragment, "main")
                    .commit();
        }
        initView();
        clickBtn();
    }

    void initView() {
        minimizeBtn = (Button) findViewById(R.id.minimize);
    }

    void clickBtn() {
        minimizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
