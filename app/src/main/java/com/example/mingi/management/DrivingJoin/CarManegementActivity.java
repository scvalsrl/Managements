package com.example.mingi.management.DrivingJoin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.mingi.management.BusinessCard.BCDetailActivity;
import com.example.mingi.management.BusinessCard.BCDetailNoAddActivity;
import com.example.mingi.management.BusinessCard.BCDetailRequest;
import com.example.mingi.management.BusinessCard.BCListActivity;
import com.example.mingi.management.R;
import com.example.mingi.management.login.BackPressCloseHandler;
import com.example.mingi.management.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CarManegementActivity extends AppCompatActivity {

    private ListView listView;
    private CarListAdapter adapter;
    private List<Car> userList;

    String isGPSEnable, nowLat, nowLon, nowName, userID,mycar;
    String year_s, month_s;
    int year_i, month_i;
    int check = 0 ;
    private BackPressCloseHandler backPressCloseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_car_manegement);
        BottomNavigationView bottomnav = findViewById(R.id.bottom_navigation);
        bottomnav.setSelectedItemId(R.id.nav_favorites);
        bottomnav.setOnNavigationItemSelectedListener(navListener);


        Button left_btn = (Button) findViewById(R.id.left_btn);
        Button right_btn = (Button) findViewById(R.id.right_btn);
        ImageView txtlist = (ImageView)findViewById(R.id.txtlist);
        backPressCloseHandler = new BackPressCloseHandler(this);

        txtlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                Menu menu = popupMenu.getMenu();
                setForceShowIcon(popupMenu);
                inflater.inflate(R.menu.carlist_option_menu, menu);

                popupMenu.setOnMenuItemClickListener
                        (new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {
                                    case R.id.joinlist:
                                              new BackgroundTask().execute();
                                        return true;
                                    case R.id.daylist:
                                              new BackgroundTask2().execute();
                                        return true;
                                }
                                return false;
                            }
                        });
                popupMenu.show();
            }
        });


        TextView txtYearMonthPicker = (TextView) findViewById(R.id.txt_year_month_picker);


        final ActionBar abar = getSupportActionBar();
        ;//line under the action bar
        View viewActionBar = getLayoutInflater().inflate(R.layout.title_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("운행기록");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);


        Intent intent = getIntent();

        final String isGPSEnable_ = intent.getStringExtra("isGPSEnable");
        final String nowLat_ = intent.getStringExtra("nowLat");
        final String nowLon_ = intent.getStringExtra("nowLon");
        final String nowName_ = intent.getStringExtra("nowName");
        final String userID_ = intent.getStringExtra("userID");
        mycar = intent.getStringExtra("mycar");
        year_s = intent.getStringExtra("str_yy");
        month_s = intent.getStringExtra("str_mm");

        listView = (ListView) findViewById(R.id.listVView);
        userList = new ArrayList<Car>();
        adapter = new CarListAdapter(getApplicationContext(), userList, this, isGPSEnable_, nowLat_, nowLon_, nowName_, mycar,month_s, year_s );
        listView.setAdapter(adapter);

        if(month_s.length() == 1 ){
            month_s= "0"+month_s;
        }

        txtYearMonthPicker.setText(year_s + "년 " + month_s + "월");

        year_i = Integer.parseInt(year_s);
        month_i = Integer.parseInt(month_s);

        isGPSEnable = isGPSEnable_;
        nowLat = nowLat_;
        nowLon = nowLon_;
        nowName = nowName_;
        userID = userID_;




        try {
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("userList"));
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0;
            String startPlace, endPlace, startTime, endTime, startDay, endDay, kilometer, carNum;
            int no;



            if(jsonArray.length()==0) {
                 Drawable drawable = getResources().getDrawable(R.drawable.nocarlist);
                ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                imageView.setImageDrawable(drawable);
            }


            while (count < jsonArray.length()) {

                JSONObject object = jsonArray.getJSONObject(count);

                startPlace = object.getString("startPlace");

                endPlace = object.getString("endPlace");
                startTime = object.getString("startTime");
                endTime = object.getString("endTime");
                startDay = object.getString("startDay");

                endDay = object.getString("endDay");
                kilometer = object.getString("kilometer");
                carNum = object.getString("carNum");
                no = object.getInt("no");

                Car car = new Car(startPlace, endPlace, startTime, endTime, startDay, endDay, kilometer, carNum, no);
                userList.add(car);
                count++;

            }

        } catch (Exception e) {

            e.printStackTrace();

        }


        txtYearMonthPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyYearMonthPickerDialog pd = new MyYearMonthPickerDialog();
                pd.setListener(d);
                pd.show(getSupportFragmentManager(), "YearMonthPickerTest");

            }
        });


        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (month_i == 1) {
                    year_i--;
                    month_i = 12;
                } else {
                    month_i--;
                }
                new BackgroundTask2().execute();
            }
        });


        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (month_i == 12) {
                    year_i++;
                    month_i = 1;
                } else {
                    month_i++;
                }
                new BackgroundTask2().execute();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.d("김민기", "리스트 터치: ");
                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("김민기", "김민기김민기김민기김민기: ");
                        try {
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

                            String startLat = jsonResponse.getString("startLat");
                            String startLon = jsonResponse.getString("startLon");
                            String destLat = jsonResponse.getString("destLat");
                            String destLon = jsonResponse.getString("destLon");

                            if (success) {
                                // 인텐드에 넣기
                                Intent intent = new Intent(CarManegementActivity.this, CarUpdateActivity.class);

                                intent.putExtra("id", id);
                                intent.putExtra("carNum", carNum);
                                intent.putExtra("startPlace", startPlace);
                                intent.putExtra("endPlace", endPlace);
                                intent.putExtra("startTime", startTime);
                                intent.putExtra("startDay", startDay);
                                intent.putExtra("endTime", endTime);
                                intent.putExtra("endDay", endDay);
                                intent.putExtra("no", no);
                                intent.putExtra("kilometer", kilometer);

                                intent.putExtra("startLat", startLat);
                                intent.putExtra("startLon", startLon);
                                intent.putExtra("destLat", destLat);
                                intent.putExtra("destLon", destLon);
                                intent.putExtra("isGPSEnable", isGPSEnable);
                                intent.putExtra("nowLat", nowLat);
                                intent.putExtra("nowLon", nowLon);
                                intent.putExtra("nowName", nowName);
                                intent.putExtra("str_yy", year_s);
                                intent.putExtra("str_mm", month_s);
                                intent.putExtra("mycar", mycar);
                                CarManegementActivity.this.startActivity(intent);
                                // 화면전환 넣기 //

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                };
                UpdateRequest updateRequest = new UpdateRequest(userList.get(i).getNo(), responseListener);
                RequestQueue queue = Volley.newRequestQueue(CarManegementActivity.this);
                queue.add(updateRequest);

            }
        });

    }

    public static void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {

                        case R.id.nav_home:

                            Intent intent = new Intent(CarManegementActivity.this, CarJoinActivity.class);
                            intent.putExtra("userID", userID);
                            intent.putExtra("nowLat", nowLat);
                            intent.putExtra("nowLon", nowLon);
                            intent.putExtra("isGPSEnable", isGPSEnable);
                            intent.putExtra("nowName", nowName);
                            intent.putExtra("mycar", mycar);
                            CarManegementActivity.this.startActivity(intent);

                            overridePendingTransition(0, 0);
                            finish();
                            break;
                        case R.id.nav_search:

                            new BackgroundTask3().execute();
                            break;

                    }

                    return true;

                }
            };


    class BackgroundTask extends AsyncTask<Void, Void, String> {

        String target;
        String day;

        @Override
        protected void onPreExecute() {


            if ((year_i % 400 == 0) || (year_i % 4 == 0 && year_i % 100 != 0)) { // 윤년

                if (month_i == 2) {
                    day = "29";
                } else if (month_i == 1 || month_i == 3 || month_i == 5 || month_i == 7 || month_i == 8 || month_i == 10 || month_i == 12) {
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

            if(check == 0) {
                year_s = String.valueOf(year_i);
                month_s = String.valueOf(month_i);
            }
            String start = year_s + "/" + month_s + "/1";
            String end = year_s + "/" + month_s + "/" + day;

            target = "http://scvalsrl.cafe24.com/CarList.php?start=" + start + "&end=" + end+"&userid="+userID;

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

            Intent intent = new Intent(CarManegementActivity.this, CarManegementActivity.class);
            intent.putExtra("userList", result);
            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon", nowLon);
            intent.putExtra("isGPSEnable", isGPSEnable);
            intent.putExtra("nowName", nowName);
            intent.putExtra("userID", userID);
            intent.putExtra("str_yy", year_s);
            intent.putExtra("str_mm", month_s);
            intent.putExtra("mycar", mycar);

            CarManegementActivity.this.startActivity(intent);
            finish();
            overridePendingTransition(0, 0);

        }


    }

    class BackgroundTask2 extends AsyncTask<Void, Void, String> {


        String target;
        String day;

        @Override
        protected void onPreExecute() {


            if ((year_i % 400 == 0) || (year_i % 4 == 0 && year_i % 100 != 0)) { // 윤년

                if (month_i == 2) {
                    day = "29";
                } else if (month_i == 1 || month_i == 3 || month_i == 5 || month_i == 7 || month_i == 8 || month_i == 10 || month_i == 12) {
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

            if(check == 0) {
                year_s = String.valueOf(year_i);
                month_s = String.valueOf(month_i);
            }
            String start = year_s + "/" + month_s + "/1";
            String end = year_s + "/" + month_s + "/" + day;

            target = "http://scvalsrl.cafe24.com/CarList2.php?start=" + start + "&end=" + end +"&userid="+userID;
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

            Intent intent = new Intent(CarManegementActivity.this, CarManegementActivity.class);
            intent.putExtra("userList", result);
            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon", nowLon);
            intent.putExtra("isGPSEnable", isGPSEnable);
            intent.putExtra("nowName", nowName);
            intent.putExtra("userID", userID);
            intent.putExtra("str_yy", year_s);
            intent.putExtra("str_mm", month_s);
            intent.putExtra("mycar", mycar);
            CarManegementActivity.this.startActivity(intent);
            finish();
            overridePendingTransition(0, 0);

        }


    }

    class BackgroundTask3 extends AsyncTask<Void, Void, String> {

        String target;

        @Override
        protected void onPreExecute() {
            target = "http://scvalsrl.cafe24.com/BCList.php?userid="+userID;
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

            Intent intent = new Intent(CarManegementActivity.this, BCListActivity.class);
            intent.putExtra("userList", result);
            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon", nowLon);
            intent.putExtra("isGPSEnable", isGPSEnable);
            intent.putExtra("nowName", nowName);
            intent.putExtra("userID", userID);
            intent.putExtra("listname", "등록순");
            intent.putExtra("mycar", mycar);
            CarManegementActivity.this.startActivity(intent);
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
        if (id == R.id.setting) {
            Intent intent = new Intent(CarManegementActivity.this, CarSetActivity.class);
            intent.putExtra("isGPSEnable", isGPSEnable);
            intent.putExtra("userID", userID);
            intent.putExtra("mycar", mycar);
            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon",nowLon);
            intent.putExtra("nowName", nowName);
            startActivity(intent);

        }
        if (id == R.id.logout) {

            Intent intent = new Intent(CarManegementActivity.this, LoginActivity.class);
            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon", nowLon);
            intent.putExtra("nowName", nowName);
            intent.putExtra("isGPSEnable", isGPSEnable);
            intent.putExtra("mycar", mycar);
            startActivity(intent);
            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = auto.edit();
            //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
            editor.clear();
            editor.commit();
            Toast.makeText(CarManegementActivity.this, "로그아웃 하였습니다", Toast.LENGTH_SHORT).show();
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            year_i = year;
            month_i = monthOfYear;
            year_s = Integer.toString(year);
            month_s = Integer.toString(monthOfYear);
            Log.d("YearMonthPickerTest zz ", year_s + " " + month_s);
            check =1;
            new BackgroundTask2().execute();

        }
    };

    // 뒤로가기 두번 누르면 종료
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }
}
