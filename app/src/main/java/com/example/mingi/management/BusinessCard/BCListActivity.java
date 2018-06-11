package com.example.mingi.management.BusinessCard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.mingi.management.DrivingJoin.CarJoinActivity;
import com.example.mingi.management.DrivingJoin.CarManegementActivity;
import com.example.mingi.management.DrivingJoin.DeleteRequest2;
import com.example.mingi.management.R;
import com.example.mingi.management.login.LoginActivity;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BCListActivity extends AppCompatActivity {



    private SwipeMenuListView listview;
    private BCListAdapter adapter;
    private List<BC> userList;
    private FloatingActionButton fab;
    Button bc_joinlist, bc_namelist , bc_comlist;

    int check=0;
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
        bottomnav.setSelectedItemId(R.id.nav_search);
        bottomnav.setOnNavigationItemSelectedListener(navListener);

        bc_joinlist = (Button)findViewById(R.id.bc_joinlist);
        bc_namelist = (Button)findViewById(R.id.bc_namelist);
        bc_comlist = (Button)findViewById(R.id.bc_comlist);

        Intent intent = getIntent();

        isGPSEnable = intent.getStringExtra("isGPSEnable");
        nowLat = intent.getStringExtra("nowLat");
        nowLon = intent.getStringExtra("nowLon");
        nowName = intent.getStringExtra("nowName");
        userID = intent.getStringExtra("userID");
        Log.d("김민기", "userID: " + userID);

        final ActionBar abar = getSupportActionBar();;//line under the action bar
        View viewActionBar = getLayoutInflater().inflate(R.layout.title_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("명함첩");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        listview = (SwipeMenuListView) findViewById(R.id.listVView);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(200);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete_black_24dp);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };







        userList = new ArrayList<BC>();
        adapter = new BCListAdapter(getApplicationContext(), userList, this, isGPSEnable, nowLat, nowLon, nowName);
        listview.setAdapter(adapter);

        // 리스트뷰 스와이프
        listview.setMenuCreator(creator);
        listview.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
                listview.smoothOpenMenu(position);
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
                listview.smoothOpenMenu(position);
            }
        });
        listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        AlertDialog.Builder builder = new AlertDialog.Builder(BCListActivity.this);

                        builder
                                .setMessage("명함을 삭제 합니다")
                                .setCancelable(false)
                                .setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog, int id) {

                                                Response.Listener<String> responseListener = new Response.Listener<String>() {


                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {

                                                            JSONObject jsonResponse = new JSONObject(response);
                                                            boolean success = jsonResponse.getBoolean("success");
                                                            if (success) {
                                                                new BackgroundTask2().execute();
                                                            } else {
                                                                Log.d("  삭제실패 : ", "1");
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                    }

                                                };




                                                DeleteRequest2 deleteRequest = new DeleteRequest2(userList.get(position).getBC_photo(),userList.get(position).getNo(), responseListener);
                                                RequestQueue queue = Volley.newRequestQueue(BCListActivity.this);
                                                queue.add(deleteRequest);


                                            }
                                        })
                                .setNegativeButton("취소",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog, int id) {
                                                // 다이얼로그를 취소한다
                                                dialog.cancel();
                                            }
                                        }).create()
                                .show();

                       break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });




        // 리스트뷰에 DB 값 받아오기
        try {
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("userList"));
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0;
            String BC_name, BC_level, BC_com, BC_phone, BC_mail, BC_add, BC_lat, BC_lon, BC_photo;
            int no;

            while (count < jsonArray.length()) {

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

                BC bc = new BC(BC_name, BC_level, BC_com, BC_phone, BC_mail, BC_add, BC_lat, BC_lon, BC_photo, no);
                userList.add(bc);
                count++;

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.attachToListView(listView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent bcList = new Intent(getApplicationContext(), BCJoinActivity.class);
                bcList.putExtra("userID", userID);
                bcList.putExtra("nowLat", nowLat);
                bcList.putExtra("nowLon", nowLon);
                bcList.putExtra("isGPSEnable", isGPSEnable);
                bcList.putExtra("nowName", nowName);
                startActivity(bcList);

            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Response.Listener<String> responseListener = new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            String BC_name, BC_level, BC_com, BC_phone, BC_mail, BC_add, BC_lat, BC_lon, BC_photo;
                            String no;
                            BC_name = jsonResponse.getString("BC_name");
                            BC_level = jsonResponse.getString("BC_level");
                            BC_com = jsonResponse.getString("BC_com");
                            BC_phone = jsonResponse.getString("BC_phone");
                            BC_mail = jsonResponse.getString("BC_mail");
                            BC_add = jsonResponse.getString("BC_add");
                            BC_lat = jsonResponse.getString("BC_lat");
                            BC_lon = jsonResponse.getString("BC_lon");
                            BC_photo = jsonResponse.getString("BC_photo");
                            no = jsonResponse.getString("no");


                            if (success) {

                                // 주소가 없다면
                                if (BC_lat.equals("0") || BC_lon.equals("0") || BC_add.equals("")) {
                                    Log.d("BCListActivity-check","lat: " + BC_lat + ", lon: " + BC_lon);
                                    BC_lat = "0";
                                    BC_lon = "0";
                                    BC_add = "";
                                    // 인텐드에 넣기
                                    Intent intent = new Intent(BCListActivity.this, BCDetailNoAddActivity.class);

                                    intent.putExtra("BC_name", BC_name);
                                    intent.putExtra("BC_level", BC_level);
                                    intent.putExtra("BC_com", BC_com);
                                    intent.putExtra("BC_phone", BC_phone);
                                    intent.putExtra("BC_mail", BC_mail);
                                    intent.putExtra("BC_add", BC_add);
                                    intent.putExtra("BC_lat", BC_lat);
                                    intent.putExtra("BC_lon", BC_lon);
                                    intent.putExtra("BC_photo", BC_photo);
                                    intent.putExtra("no", no);


                                    intent.putExtra("userID", userID);
                                    intent.putExtra("isGPSEnable", isGPSEnable);
                                    intent.putExtra("nowLat", nowLat);
                                    intent.putExtra("nowLon", nowLon);
                                    intent.putExtra("nowName", nowName);


                                    BCListActivity.this.startActivity(intent);
                                    // 화면전환 넣기 //

                                } else{

                                    // 인텐드에 넣기
                                    Intent intent = new Intent(BCListActivity.this, BCDetailActivity.class);

                                    intent.putExtra("BC_name", BC_name);
                                    intent.putExtra("BC_level", BC_level);
                                    intent.putExtra("BC_com", BC_com);
                                    intent.putExtra("BC_phone", BC_phone);
                                    intent.putExtra("BC_mail", BC_mail);
                                    intent.putExtra("BC_add", BC_add);
                                    intent.putExtra("BC_lat", BC_lat);
                                    intent.putExtra("BC_lon", BC_lon);
                                    intent.putExtra("BC_photo", BC_photo);
                                    intent.putExtra("no", no);


                                    intent.putExtra("userID", userID);
                                    intent.putExtra("isGPSEnable", isGPSEnable);
                                    intent.putExtra("nowLat", nowLat);
                                    intent.putExtra("nowLon", nowLon);
                                    intent.putExtra("nowName", nowName);


                                    BCListActivity.this.startActivity(intent);
                                    // 화면전환 넣기 //

                                }

                            } else {

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                };

                BCDetailRequest bcDetailRequest = new BCDetailRequest(userList.get(i).getNo(), responseListener);
                RequestQueue queue = Volley.newRequestQueue(BCListActivity.this);
                queue.add(bcDetailRequest);


            }
        });


        bc_joinlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BackgroundTask2().execute();
            }
        });

        bc_namelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = 1;
                new BackgroundTask2().execute();
            }
        });

        bc_comlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = 2;
                new BackgroundTask2().execute();
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
                            intent.putExtra("isGPSEnable", isGPSEnable);
                            intent.putExtra("nowName", nowName);
                            BCListActivity.this.startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                            break;
                        case R.id.nav_favorites:
                            new BCListActivity.BackgroundTask().execute();
                            break;


                    }


                    return true;
                }
            };

    class BackgroundTask extends AsyncTask<Void, Void, String> {

        String day;
        String target;
        SimpleDateFormat yy = new SimpleDateFormat("yyyy", Locale.KOREA);
        String str_yy = yy.format(new Date());
        int year_i = Integer.parseInt(str_yy);
        SimpleDateFormat mm = new SimpleDateFormat("MM", Locale.KOREA);
        String str_mm = mm.format(new Date());
        int month_i = Integer.parseInt(str_mm);

        @Override
        protected void onPreExecute() {

            if ((year_i % 400 == 0) || (year_i % 4 == 0 && year_i % 100 != 0)) { // 윤년
                if (month_i == 2) {
                    day = "29";
                }
                else if (month_i == 1 || month_i == 3 || month_i == 5 || month_i == 7 || month_i == 8 || month_i == 10 || month_i == 12) {
                    day = "31";
                } else {
                    day = "30";
                }
            } else { // 평년
                if (month_i == 2) {
                    day = "28";
                } else if (month_i == 1 || month_i == 3 || month_i == 5 || month_i == 7 || month_i == 8 || month_i == 10 || month_i == 12) {
                    day = "31";
                } else {
                    day = "30";
                }
            }

            String start = str_yy + "/"+str_mm+"/1";
            String end = str_yy + "/"+str_mm+"/"+day;
            target = "http://scvalsrl.cafe24.com/CarList2.php?start="+start+"&end="+end;
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

            Intent intent = new Intent(BCListActivity.this, CarManegementActivity.class);
            intent.putExtra("userList", result);
            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon", nowLon);
            intent.putExtra("isGPSEnable", isGPSEnable);
            intent.putExtra("nowName", nowName);
            intent.putExtra("userID", userID);
            intent.putExtra("str_yy", str_yy);
            intent.putExtra("str_mm", str_mm);
            BCListActivity.this.startActivity(intent);

            finish();
            overridePendingTransition(0, 0);

        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.carlist_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if( id == R.id.logout ){

            Intent intent = new Intent(BCListActivity.this, LoginActivity.class);
            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon",nowLon);
            intent.putExtra("nowName", nowName);
            intent.putExtra("isGPSEnable", isGPSEnable);
            startActivity(intent);
            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = auto.edit();
            //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
            editor.clear();
            editor.commit();
            Toast.makeText(BCListActivity.this, "로그아웃 하였습니다", Toast.LENGTH_SHORT).show();
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    class BackgroundTask2 extends AsyncTask<Void, Void, String> {

        String target;
        String list;
        String list2;
        @Override
        protected void onPreExecute() {

            if(check == 1){
                list="BC_name";
                list2="asc";
            }else if(check == 2){
                list="BC_com";
                list2="asc";
            }else{
                list="no";
                list2="DESC";
            }

            target = "http://scvalsrl.cafe24.com/BCList2.php?list="+list+"&list2="+list2;
            Log.d("김민기", target);
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
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            BCListActivity.this.startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        }
    }


}
