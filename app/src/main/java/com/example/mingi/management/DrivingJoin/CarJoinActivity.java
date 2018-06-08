package com.example.mingi.management.DrivingJoin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.mingi.management.BusinessCard.BCListActivity;
import com.example.mingi.management.login.LoginActivity;
import com.example.mingi.management.R;
import com.example.mingi.management.login.Splashscreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class CarJoinActivity extends AppCompatActivity {

    TextView txtDate, txtTime, txtDate2, txtTime2, txtCar;
    Calendar currentTime;
    int hour, minute;
    String format, userID;
    String gps;
    BottomNavigationView bottomnav;

    int y, m, d;


    /*yoonju*/
    TextView startText, destText, distanceText;
    View totalBox;
    Button changeBtn;
    String startPlace = "출발지 입력";
    String endPlace = "도착지 입력";
    String startLat, startLon, destLat, destLon, nowName, kilometer;
    int distance = -1;

    String nowLat = "129.065782";
    String nowLon = "35.145404";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_join);






        findcontrol();
        setActionBar();

        getFromIntent();
        startDateTime();
        setTotalBox();
        DestDateTime();
        setStartDestTxt();
        ChangeStartDest();
        setCar();
    }

    private void getFromIntent() {
        Intent fromSplash = getIntent();
        String isGPSEnable = fromSplash.getStringExtra("isGPSEnable");
        userID = fromSplash.getStringExtra("userID");
        gps = isGPSEnable;

        if (isGPSEnable.compareTo("0") == 0) { // success
            // reverse Geo
            nowLat = fromSplash.getStringExtra("nowLat");
            nowLon = fromSplash.getStringExtra("nowLon");
            nowName = fromSplash.getStringExtra("nowName");

            if (nowName != null) {
                startPlace = nowName;
                startText.setText(startPlace);
            }

            startLat = nowLat;
            startLon = nowLon;

            boolean isStart = startPlace.equals("출발지 입력");
            boolean isDest = endPlace.equals("도착지 입력");
            calculateDistance(isStart, isDest);
        }
    }

    private void setActionBar() {
        final ActionBar abar = getSupportActionBar();;//line under the action bar
        View viewActionBar = getLayoutInflater().inflate(R.layout.title_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("BNK운행일지");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
    }

    private void setCar() {
        txtCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.txtCar:

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(CarJoinActivity.this, R.style.MyAlertDialogStyle);
                        alertBuilder.setIcon(R.drawable.ic_directions_car_black_24dp);
                        alertBuilder.setTitle("차량을 선택해주세요");

                        // List Adapter 생성
                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                CarJoinActivity.this,
                                android.R.layout.select_dialog_item);

                        adapter.add("02미 8815 (YF쏘나타)");
                        adapter.add("12가 3386 (아반떼 쿠페)");
                        adapter.add("15러 1517 (쏘나타)");
                        adapter.add("22바 9539 (제네시스)");
                        adapter.add("35우 4012 (싼타페)");
                        adapter.add("45노 6521 (K3)");
                        adapter.add("50더 1234 (카니발)");
                        adapter.add("52딘 6543 (카니발)");
                        adapter.add("59호 5544 (K5)");
                        adapter.add("64오 1775 (그렌져)");

                        // 버튼 생성
                        alertBuilder.setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });

                        // Adapter 셋팅
                        alertBuilder.setAdapter(adapter,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String strName = adapter.getItem(id);
                                        txtCar.setText(strName);
                                    }
                                });
                        alertBuilder.show();
                        break;

                    default:
                        break;
                }

            }
        });
    }

    private void ChangeStartDest() {
        changeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean isChange = false;

                boolean isStart = startPlace.equals("출발지 입력"); // true: 입력X, false: 입력O
                boolean isDest = endPlace.equals("도착지 입력");
                Animation anim = AnimationUtils.loadAnimation(
                        getApplicationContext(),
                        R.anim.rotation);
                // start x
                if (isStart) {
                    // dest O

                    if (!isDest) {
                        startPlace = endPlace;
                        startLat = destLat;
                        startLon = destLon;
                        destLat = null;
                        destLon = null;
                        endPlace = "도착지 입력";
                        isChange = true;
                    }

                } else { // start o
                    if (!isDest) { // dest o
                        String tmp = startPlace;
                        startPlace = endPlace;
                        endPlace = tmp;

                        tmp = startLat;
                        startLat = destLat;
                        destLat = tmp;

                        tmp = startLon;
                        startLon = destLon;
                        destLon = tmp;

                        isChange = true;
                    } else { // dest x
                        endPlace = startPlace;
                        destLat = startLat;
                        destLon = startLon;

                        startPlace = "출발지 입력";
                        startLat = null;
                        startLon = null;
                        isChange = true;


                    }
                }

                if (isChange) {
                    startText.setText(startPlace);
                    destText.setText(endPlace);
                    changeBtn.startAnimation(anim);

                }
            }
        });
    }


    private void setStartDestTxt() {
        startText.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarJoinActivity.this, ListActivity.class);
                intent.putExtra("curLat", nowLat);
                intent.putExtra("curLon", nowLon);
                intent.putExtra("curAddr", nowName);
                startActivityForResult(intent, 0);
            }
        });

        destText.setOnClickListener(new TextView.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarJoinActivity.this, DestListActivity.class);
                intent.putExtra("curLat", nowLat);
                intent.putExtra("curLon", nowLon);
                intent.putExtra("curAddr", nowName);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void DestDateTime() {
        txtDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                d = calendar.get(Calendar.DAY_OF_MONTH);
                m = calendar.get(Calendar.MONTH);
                y = calendar.get(Calendar.YEAR);

                DatePickerDialog pickerDialog = new DatePickerDialog(CarJoinActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        i1 += 1;
                        String Day = i + "/" + i1 + "/" + i2;
                        txtDate2.setText(Day);

                    }
                }, y, m, d);
                pickerDialog.show();

            }
        });


        txtTime2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                currentTime = Calendar.getInstance();
                hour = currentTime.get(Calendar.HOUR_OF_DAY);
                minute = currentTime.get(Calendar.MINUTE);

                seletedTimeFormat(hour);

                TimePickerDialog timePickerDialog = new TimePickerDialog(CarJoinActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        seletedTimeFormat(hourOfDay);

                        String time = hourOfDay + ":" + minute + " " + format;
                        txtTime2.setText(time);


                    }
                }, hour, minute, true);

                timePickerDialog.show();

            }

        });
    }

    private void startDateTime() {
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                d = calendar.get(Calendar.DAY_OF_MONTH);
                m = calendar.get(Calendar.MONTH);
                y = calendar.get(Calendar.YEAR);

                DatePickerDialog pickerDialog = new DatePickerDialog(CarJoinActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        i1 += 1;
                        String Day = i + "/" + i1 + "/" + i2;
                        txtDate.setText(Day);


                    }
                }, y, m, d);
                pickerDialog.show();

            }
        });

        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                currentTime = Calendar.getInstance();
                hour = currentTime.get(Calendar.HOUR_OF_DAY);
                minute = currentTime.get(Calendar.MINUTE);

                seletedTimeFormat(hour);

                TimePickerDialog timePickerDialog = new TimePickerDialog(CarJoinActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        seletedTimeFormat(hourOfDay);

                        String time = hourOfDay + ":" + minute + " " + format;
                        txtTime.setText(time);


                    }
                }, hour, minute, true);

                timePickerDialog.show();

            }

        });
    }


    public void seletedTimeFormat(int hour) {
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
    }

    private void findcontrol() {
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtTime = (TextView) findViewById(R.id.txtTime);

        txtDate2 = (TextView) findViewById(R.id.txtDate2);
        txtTime2 = (TextView) findViewById(R.id.txtTime2);


        startText = findViewById(R.id.startText);
        destText = findViewById(R.id.endText);
        distanceText = findViewById(R.id.distancetext);
        changeBtn = findViewById(R.id.changeBtn);
        txtCar = (TextView) findViewById(R.id.txtCar);
        bottomnav = findViewById(R.id.bottom_navigation);
        bottomnav.setOnNavigationItemSelectedListener(navListener);
        totalBox = (LinearLayout) findViewById(R.id.totalBox);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if( id == R.id.logout ){



            Intent intent = new Intent(CarJoinActivity.this, LoginActivity.class);

            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon",nowLon);
            intent.putExtra("nowName", nowName);
            intent.putExtra("isGPSEnable", gps);

            startActivity(intent);
            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = auto.edit();
            //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
            editor.clear();
            editor.commit();
            Toast.makeText(CarJoinActivity.this, "로그아웃 하였습니다", Toast.LENGTH_SHORT).show();
            finish();

            return true;
        }
        if( id == R.id.newPost ){

            final String ids = "2109812";
            String carNum = txtCar.getText().toString();
            String startday = txtDate.getText().toString();
            String endday = txtDate2.getText().toString();
            String startTime = txtTime.getText().toString();
            String endTime = txtTime2.getText().toString();


            if (startLat.equals(destLat) && startLon.equals(destLon)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CarJoinActivity.this);
                builder.setMessage(" 출발지와 목적지가 동일합니다.")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();

            } else if (carNum.equals("차량을 선택해주세요")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CarJoinActivity.this);
                builder.setMessage(" 차량을 선택해주세요.")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();

            } else if (startPlace.equals("출발지 입력") || endPlace.equals("도착지 입력")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CarJoinActivity.this);
                builder.setMessage(" 경로를 설정해주세요.")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();

            } else if (startday.equals("출발 날짜를 선택해주세요") || startTime.equals("출발 시간을 선택해주세요")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CarJoinActivity.this);
                builder.setMessage(" 출발 일자를 설정해주세요.")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();
            } else if (endday.equals("도착 날짜를 선택해주세요") || endTime.equals("도착 시간을 선택해주세요")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CarJoinActivity.this);
                builder.setMessage(" 도착 일자를 설정해주세요.")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();

            } else {
                Response.Listener<String> responseListener2 = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // 제이슨 생성
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {  // 성공


                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {


                                        try {
                                            JSONObject jsonResponse = new JSONObject(response);
                                            boolean success = jsonResponse.getBoolean("success");


                                            if (success) {


                                                startPlace = "출발지 입력";
                                                endPlace = "도착지 입력";
                                                startText.setText(startPlace);
                                                destText.setText(endPlace);
                                                distanceText.setText("");
                                                txtCar.setText("차량을 선택해주세요");
                                                txtTime.setText("출발 시간을 선택해주세요");
                                                txtTime2.setText("도착 시간을 선택해주세요");
                                                txtDate.setText("출발 날짜를 선택해주세요");
                                                txtDate2.setText("도착 날짜을 선택해주세요");


                                                AlertDialog.Builder builder = new AlertDialog.Builder(CarJoinActivity.this);


                                                builder.setMessage("성공적으로 등록 되었습니다")
                                                        .setPositiveButton("확인", null)
                                                        .create()
                                                        .show();

                                            } else {

                                                AlertDialog.Builder builder = new AlertDialog.Builder(CarJoinActivity.this);
                                                builder.setMessage("등록에 실패 했습니다.")
                                                        .setNegativeButton("다시시도", null).create().show();
                                                Intent intent = new Intent(CarJoinActivity.this, CarJoinActivity.class);
                                                CarJoinActivity.this.startActivity(intent);

                                            }

                                        } catch (JSONException e) {

                                            e.printStackTrace();
                                        }


                                    }


                                };
                                String no_s = jsonResponse.getString("no");

                                int no_i = Integer.parseInt(no_s);
                                no_i++;

                               int no = no_i;

                                // 화면전환 넣기 //
                                String id = "2109812";
                                String carNum = txtCar.getText().toString();
                                String startday = txtDate.getText().toString();
                                String endday = txtDate2.getText().toString();
                                String startTime = txtTime.getText().toString();
                                String endTime = txtTime2.getText().toString();

                                CarJoinRequest carJoinRequest = new CarJoinRequest(ids, carNum, startPlace, endPlace, kilometer, startday, endday, startTime, endTime, no, startLat, startLon, destLat, destLon, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(CarJoinActivity.this);

                                queue.add(carJoinRequest);

                            } else {
                                Log.d(" 카운팅 실패 : ", "1");

                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                };
                CarCountRequest carcountrequest = new CarCountRequest(responseListener2);
                RequestQueue queue2 = Volley.newRequestQueue(CarJoinActivity.this);
                queue2.add(carcountrequest);
            }
            return true;




        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.carjoin_menu, menu);
        for(int i = 0; i < menu.size(); i++) {
            Log.d("CarjoinActivity-menu", String.valueOf(menu.size()));
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            if(i == 0) {

            }
            int end = spanString.length();
            spanString.setSpan(new AbsoluteSizeSpan(37), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            item.setTitle(spanString);
        }
        return true;
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

            Intent intent = new Intent(CarJoinActivity.this, CarManegementActivity.class);
            intent.putExtra("userList", result);
            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon", nowLon);
            intent.putExtra("isGPSEnable", gps);
            intent.putExtra("nowName", nowName);
            intent.putExtra("userID", userID);
            CarJoinActivity.this.startActivity(intent);

            finish();
            overridePendingTransition(0, 0);

        }

    }

    class BackgroundTask2 extends AsyncTask<Void, Void, String> {

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

            Intent intent = new Intent(CarJoinActivity.this, BCListActivity.class);
            intent.putExtra("userList", result);
            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon", nowLon);
            intent.putExtra("isGPSEnable", gps);
            intent.putExtra("nowName", nowName);
            intent.putExtra("userID", userID);
            CarJoinActivity.this.startActivity(intent);
            finish();
            overridePendingTransition(0, 0);

        }

    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {

                        case R.id.nav_favorites:
                            new BackgroundTask().execute();
                            break;

                        case R.id.nav_search:
                            new BackgroundTask2().execute();
                            break;
                    }
                    return true;
                }
            };

    void calculateDistance(boolean isStart, boolean isDest) {
        if (!isStart && !isDest) {
            if (startLat.equals(destLat) && startLon.equals(destLon)) {
                distanceText.setText(" 0 km");

                AlertDialog.Builder builder = new AlertDialog.Builder(CarJoinActivity.this);
                builder.setMessage(" 출발지와 목적지가 같습니다 ")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();

                return;
            }
            try {
                distance = (int) new Task(destLon, destLat, startLon, startLat).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Log.d("CarJoinActivity", "distance 계산 후: " + distance);
            if(distance > -1) {
                Log.d("CarJoinActivity", "distance > -1 " + distance);
                final float distanceKM = (float) (distance / 1000 + (distance % 1000) * 0.001);
                kilometer = Float.toString(distanceKM);
                AlertDialog.Builder userdistanceBuilder = new AlertDialog.Builder(CarJoinActivity.this);

                String short_start = startPlace;
                String short_end = endPlace;

                if(startPlace.length() > 8) {
                    short_start = startPlace.substring(0,8) + "..";
                }
                if(endPlace.length() > 8) {
                    short_end = endPlace.substring(0, 8) + "..";
                }
                userdistanceBuilder.setTitle(short_start + "→" + short_end);
                userdistanceBuilder.setMessage(kilometer + "km")
                        .setCancelable(false)
                        .setPositiveButton("직접입력", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditDialog editDialog = new EditDialog(CarJoinActivity.this);
                                editDialog.setDialogListener(new MyDialogListener() {
                                    @Override
                                    public void onPositiveClicked(String km) {
                                        distanceText.setText(km);
                                    }

                                    @Override
                                    public void onNegativeClicked() { }
                                });
                                editDialog.show();
                            }
                        })
                        .setNegativeButton("확인", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                distanceText.setText(kilometer);
                            }
                        });

                AlertDialog userdistance = userdistanceBuilder.create();
                userdistance.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == 0 && data != null) {
                startPlace = data.getStringExtra("startname");
                startLat = data.getStringExtra("startlat");
                startLon = data.getStringExtra("startlon");

                startText.setText(startPlace);
            }
        }
        if (requestCode == 1) {
            if (resultCode == 1 && data != null) {
                endPlace = data.getStringExtra("destname");
                destLat = data.getStringExtra("destlat");
                destLon = data.getStringExtra("destlon");

                destText.setText(endPlace);
            }
        }

        boolean isStart = startPlace.equals("출발지 입력"); // true: 입력X, false: 입력O
        boolean isDest = endPlace.equals("도착지 입력");
        calculateDistance(isStart, isDest);
    }

    void setTotalBox() {
        totalBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isStart = startPlace.equals("출발지 입력");
                boolean isDest = endPlace.equals("도착지 입력");
                if(isStart) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CarJoinActivity.this);
                    builder.setMessage(" 출발지를 입력해주세요 ")
                            .setNegativeButton("확인", null)
                            .create()
                            .show();

                    return;
                }
                if(isDest) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CarJoinActivity.this);
                    builder.setMessage(" 도착지를 입력해주세요 ")
                            .setNegativeButton("확인", null)
                            .create()
                            .show();

                    return;
                }

                if(startLat == destLat && startLon == destLon) {
                    Log.d("CarJoin", "출발지==도착지");
                    distanceText.setText(" 0 km");
                    AlertDialog.Builder builder = new AlertDialog.Builder(CarJoinActivity.this);
                    builder.setMessage(" 출발지와 목적지가 같습니다 ")
                            .setNegativeButton("확인", null)
                            .create()
                            .show();
                    return;
                }

                EditDialog editDialog = new EditDialog(CarJoinActivity.this);
                editDialog.setDialogListener(new MyDialogListener() {
                    @Override
                    public void onPositiveClicked(String km) {
                        distanceText.setText(km);
                    }

                    @Override
                    public void onNegativeClicked() { }
                });
                editDialog.show();
            }
        });
    }


}
