package com.example.mingi.management;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ListMapActivity extends AppCompatActivity {
    Button checkBtn;
    String lat = null;
    String lon = null;
    String name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_map);

        Intent outIntent = getIntent();
        lat = outIntent.getStringExtra("lat");
        lon = outIntent.getStringExtra("lon");
        name = outIntent.getStringExtra("name");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(name);
        checkBtn = (Button)findViewById(R.id.checkBtn);

        if (savedInstanceState == null) {
            MainFragment mainFragment = new MainFragment();
            Bundle bundle = new Bundle(2); // # of datas

            bundle.putString("lat", lat);
            bundle.putString("lon", lon);
            bundle.putString("name", name);
            mainFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFragment, mainFragment, "main")
                    .commit();
        }

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListMapActivity.this, ListActivity.class);
                setResult(0, intent);
                finish();
            }
        });
    }
}
