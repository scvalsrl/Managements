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

public class CarManegementActivity extends AppCompatActivity {

    private ListView listView;
    private CarListAdapter adapter;

    private List<Car> userList;


    String isGPSEnable;
    String nowLat;
    String nowLon;
    String nowName;
    String userID;
    String year_s;
    String month_s;

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            year_s = Integer.toString(year);
            month_s = Integer.toString(monthOfYear);
            Log.d("YearMonthPickerTest zz ", year_s + " " + month_s);

            new BackgroundTask2().execute();

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_car_manegement);
        BottomNavigationView bottomnav = findViewById(R.id.bottom_navigation);
        bottomnav.setOnNavigationItemSelectedListener(navListener);


        Button Jlist = (Button) findViewById(R.id.Jlist);
        Button Dlist = (Button) findViewById(R.id.Dlist);
        Button btnYearMonthPicker = findViewById(R.id.btn_year_month_picker);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("운행기록 관리");


        Intent intent = getIntent();

        final String isGPSEnable_ = intent.getStringExtra("isGPSEnable");
        final String nowLat_ = intent.getStringExtra("nowLat");
        final String nowLon_ = intent.getStringExtra("nowLon");
        final String nowName_ = intent.getStringExtra("nowName");
        final String userID_ = intent.getStringExtra("userID");


        isGPSEnable = isGPSEnable_;
        nowLat = nowLat_;
        nowLon = nowLon_;
        nowName = nowName_;
        userID = userID_;

        listView = (ListView) findViewById(R.id.listVView);
        userList = new ArrayList<Car>();
        adapter = new CarListAdapter(getApplicationContext(), userList, this, isGPSEnable_, nowLat_, nowLon_, nowName_);
        listView.setAdapter(adapter);


        try {
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("userList"));
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0;
            String startPlace, endPlace, startTime, endTime, startDay, endDay, kilometer, carNum;
            int no;

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


        Jlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new BackgroundTask().execute();

            }
        });


        Dlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new BackgroundTask1().execute();

            }
        });


        btnYearMonthPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyYearMonthPickerDialog pd = new MyYearMonthPickerDialog();
                pd.setListener(d);
                pd.show(getSupportFragmentManager(), "YearMonthPickerTest");

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

                            Intent intent = new Intent(CarManegementActivity.this, CarJoinActivity.class);
                            intent.putExtra("userID", userID);
                            intent.putExtra("nowLat", nowLat);
                            intent.putExtra("nowLon", nowLon);
                            intent.putExtra("isGPSEnable", isGPSEnable);
                            intent.putExtra("nowName", nowName);
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


    class BackgroundTask2 extends AsyncTask<Void, Void, String> {

        String target;
        private Map<String, String> parameters;

        @Override
        protected void onPreExecute() {
            target = "http://scvalsrl.cafe24.com/CarList2.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {

                Log.d("YearMonthPickerTest", "1111 들어옴!!:! ");

                URL url = new URL(target);

                Log.d("YearMonthPickerTest", "22 들어옴: " + year_s);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("year","2018/5/1");
                httpURLConnection.setRequestProperty("month","2018/5/30");
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
            CarManegementActivity.this.startActivity(intent);
            finish();
            overridePendingTransition(0, 0);

        }


    }


    class BackgroundTask1 extends AsyncTask<Void, Void, String> {

        String target;
        private Map<String, String> parameters;

        @Override
        protected void onPreExecute() {
            target = "http://scvalsrl.cafe24.com/CarList1.php";
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
            CarManegementActivity.this.startActivity(intent);

            finish();
            overridePendingTransition(0, 0);

        }


    }


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

            Intent intent = new Intent(CarManegementActivity.this, CarManegementActivity.class);
            intent.putExtra("userList", result);
            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon", nowLon);
            intent.putExtra("isGPSEnable", isGPSEnable);
            intent.putExtra("nowName", nowName);
            intent.putExtra("userID", userID);
            CarManegementActivity.this.startActivity(intent);

            finish();
            overridePendingTransition(0, 0);

        }

    }

    class BackgroundTask3 extends AsyncTask<Void, Void, String> {

        String target;

        @Override
        protected void onPreExecute() {
            target = "http://scvalsrl.cafe24.com/BCList.php";
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
            CarManegementActivity.this.startActivity(intent);
            finish();
            overridePendingTransition(0, 0);

        }

    }
}
