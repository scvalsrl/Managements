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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
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
import java.util.concurrent.atomic.AtomicIntegerArray;

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


        Intent intent = getIntent();

        isGPSEnable = intent.getStringExtra("isGPSEnable");
        nowLat = intent.getStringExtra("nowLat");
        nowLon = intent.getStringExtra("nowLon");
        nowName = intent.getStringExtra("nowName");
        userID = intent.getStringExtra("userID");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("명함 관리");


        listView = (ListView) findViewById(R.id.listVView);
        userList = new ArrayList<BC>();
        adapter = new BCListAdapter(getApplicationContext(), userList, this,isGPSEnable , nowLat , nowLon , nowName);
        listView.setAdapter(adapter);


        try{
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("userList"));
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0;
            String BC_name, BC_level, BC_com, BC_phone, BC_mail, BC_add, BC_lat, BC_lon , BC_photo;
            int no;

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
                bcList.putExtra("userID", userID);
                bcList.putExtra("nowLat", nowLat);
                bcList.putExtra("nowLon", nowLon);
                bcList.putExtra("isGPSEnable",isGPSEnable);
                bcList.putExtra("nowName", nowName);
                startActivity(bcList);

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("BCListActivity", "listview item click 함");
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            String id = jsonResponse.getString("id");
                            String carNum = jsonResponse.getString("carNum");
                            String startPlace = jsonResponse.getString("startPlace");
                            String endPlace = jsonResponse.getString("endPlace");
                            String startTime = jsonResponse.getString("startTime");
                            String startDay = jsonResponse.getString("startDay");
                            String endTime = jsonResponse.getString("endTime");
                            String endDay = jsonResponse.getString("endDay");
                            String no = jsonResponse.getString("no");
                            String kilometer = jsonResponse.getString("kilometer");


                            if(success){

                                // 인텐드에 넣기
                                Intent intent = new Intent(BCListActivity.this , CarUpdateActivity.class);

                                intent.putExtra("id", id);
                                intent.putExtra("carNum", carNum);
                                intent.putExtra("startPlace", startPlace);
                                intent.putExtra("endPlace", endPlace);
                                intent.putExtra("startTime",startTime);
                                intent.putExtra("startDay", startDay);
                                intent.putExtra("endTime", endTime);
                                intent.putExtra("endDay", endDay);
                                intent.putExtra("no", no);
                                intent.putExtra("kilometer",kilometer);



                                intent.putExtra("isGPSEnable",isGPSEnable);
                                intent.putExtra("nowLat",nowLat);
                                intent.putExtra("nowLon",nowLon);
                                intent.putExtra("nowName",nowName);


                                BCListActivity.this.startActivity(intent);
                                // 화면전환 넣기 //


                            }else{

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                };


                int no  = userList.get(position).getNo();

                BCUpdateRequest updateRequest = new BCUpdateRequest(no, responseListener);
                RequestQueue queue = Volley.newRequestQueue(BCListActivity.this);
                queue.add(updateRequest);


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



}
