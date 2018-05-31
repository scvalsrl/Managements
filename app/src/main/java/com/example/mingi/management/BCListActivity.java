package com.example.mingi.management;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BCListActivity extends AppCompatActivity {

    private ListView listView;
    private BCListAdapter adapter;
    private List<BC> userList;
    private FloatingActionButton fab;


    String isGPSEnable;
    String nowLat;
    String nowLon;
    String nowName;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bclist);
        BottomNavigationView bottomnav = findViewById(R.id.bottom_navigation);
        bottomnav.setOnNavigationItemSelectedListener(navListener);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("명함 관리");


        Intent intent = getIntent();

        final String isGPSEnable_ = intent.getStringExtra("isGPSEnable");
        final String nowLat_ = intent.getStringExtra("nowLat");
        final String nowLon_ = intent.getStringExtra("nowLon");
        final String nowName_ = intent.getStringExtra("nowName");
        final String userID_ = intent.getStringExtra("userID");


        isGPSEnable = isGPSEnable_;
        nowLat= nowLat_;
        nowLon = nowLon_;
        nowName = nowName_;
        userID = userID_;

        listView = (ListView) findViewById(R.id.listVView);
        userList = new ArrayList<BC>();
        adapter = new BCListAdapter(getApplicationContext(), userList, this,isGPSEnable_ , nowLat_ , nowLon_ , nowName_);
        listView.setAdapter(adapter);


        try{
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("userList"));
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0;
            String BC_name, BC_level, BC_com, BC_phone, BC_mail, BC_add, BC_lat, BC_lon , BC_photo;
            int no;
            Log.d("YearMonthPickerTest", "444 들어옴 : " + jsonArray.length());
            while (count< jsonArray.length()){

                JSONObject object = jsonArray.getJSONObject(count);

                BC_name = object.getString("BC_name");
                BC_level = object.getString("BC_level");
                BC_com = object.getString("BC_com");
                BC_phone = object.getString("BC_phone");
                BC_mail = object.getString("BC_mail");

                BC_add = object.getString("BC_add");
                BC_lat = object.getString("BC_lat");
                BC_lon = object.getString("BC_lon");
                BC_photo = object.getString("BC_photo");
                no = object.getInt("no");
                Log.d("YearMonthPickerTest", "가져왔을까 : " + no +"    " + BC_photo );
                BC bc = new BC(BC_name, BC_level,BC_com , BC_phone , BC_mail , BC_add, BC_lat, BC_lon, BC_photo, no);
                userList.add(bc);
                count++;

            }

        }catch (Exception e){

            e.printStackTrace();

        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.attachToListView(listView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent bcList = new Intent(getApplicationContext(), BusinessCardMain.class);
                startActivity(bcList);

            }
        });


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {

                        case R.id.nav_home:
                            Log.d("김민기5", "onNavigationItemSelected: ");
                            Intent intent = new Intent(BCListActivity.this, CarJoinActivity.class);
                            intent.putExtra("userID", userID);
                            intent.putExtra("nowLat", nowLat);
                            intent.putExtra("nowLon", nowLon);
                            intent.putExtra("isGPSEnable",isGPSEnable);
                            intent.putExtra("nowName", nowName);
                            BCListActivity.this.startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                            break;
                        case R.id.nav_search:

                            break;

                    }


                    return true;
                }
            };




    class BackgroundTask extends AsyncTask<Void, Void, String> {

        String target;

        @Override
        protected void onPreExecute() {
            target = "http://scvalsrl.cafe24.com/CarList.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null) {

                    stringBuilder.append(temp + "\n");

                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();

            } catch (Exception e) {

                e.printStackTrace();

            }


            return null;
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        public void onPostExecute(String result) {

            Intent intent = new Intent(BCListActivity.this, BCListActivity.class);
            intent.putExtra("userList", result);
            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon", nowLon);
            intent.putExtra("isGPSEnable", isGPSEnable);
            intent.putExtra("nowName", nowName);
            intent.putExtra("userID", userID);
            BCListActivity.this.startActivity(intent);

            finish();
            overridePendingTransition(0, 0);

        }

    }
}
