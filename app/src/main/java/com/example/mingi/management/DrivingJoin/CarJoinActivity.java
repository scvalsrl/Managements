package com.example.mingi.management.DrivingJoin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.example.mingi.management.login.BackPressCloseHandler;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CarJoinActivity extends AppCompatActivity {

    TextView txtDate, txtTime, txtDate2, txtTime2, txtCar;
    LinearLayout viewCar, viewStartTxt, viewEndTxt, viewStartDate, viewStartTime, viewEndDate, viewEndTime;
    Calendar currentTime;
    String first_gps;
    int hour, minute;
    String format, userID, mycar;
    String gps , start_day, end_day, start_time, end_time;
    BottomNavigationView bottomnav;

    boolean preventButtonTouch = false;
    int y, m, d;
    long sec;
    int compare;


    /*yoonju*/
    TextView startText, destText, distanceText;
    View totalBox;
    Button changeBtn,recentPathBtn;
    String startPlace = "출발지 입력";
    String endPlace = "도착지 입력";
    String startLat, startLon, destLat, destLon, nowName, kilometer;
    int distance = -1;
    int startM = -1;
    String nowLat = "129.065782";
    String nowLon = "35.145404";
    FromToInfo fromToInfo;

    private BackPressCloseHandler backPressCloseHandler;

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
        RecentPath();
        ChangeStartDest();
        setCar();

    }

    private void getFromIntent() {
        Intent fromSplash = getIntent();
        String isGPSEnable = fromSplash.getStringExtra("isGPSEnable");
        userID = fromSplash.getStringExtra("userID");
        gps = isGPSEnable;
        mycar = fromSplash.getExtras().getString("mycar");


        if(!mycar.equals("차량을 선택해주세요")){
            txtCar.setText(mycar);
            txtCar.setTextColor(Color.BLACK);
            txtCar .setTypeface(null, Typeface.BOLD);
        }

        if (isGPSEnable.compareTo("0") == 0) { // success
            // reverse Geo
            nowLat = fromSplash.getStringExtra("nowLat");
            nowLon = fromSplash.getStringExtra("nowLon");
            nowName = fromSplash.getStringExtra("nowName");

            if (nowName != null) {
                startPlace = nowName;
                first_gps = nowName;
                startText.setText(startPlace);
                startText.setTextColor(Color.BLACK);
                startText .setTypeface(null, Typeface.BOLD);
            }

            startLat = nowLat;
            startLon = nowLon;

            boolean isStart = startPlace.equals("출발지 입력");
            boolean isDest = endPlace.equals("도착지 입력");
            calculateDistance(isStart, isDest);
        }



    }

    private void setActionBar() {
        final ActionBar abar = getSupportActionBar();
        ;//line under the action bar
        View viewActionBar = getLayoutInflater().inflate(R.layout.title_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("운행등록");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
    }

    private void setCar() {
        viewCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("viewCar", "차량 선택 클릭");
                switch (v.getId()) {
                    case R.id.viewCar:
                        Log.d("viewCar", "차량 선택 클릭 스위치 안");
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
                                        txtCar.setTextColor(Color.BLACK);
                                        txtCar.setTypeface(null, Typeface.BOLD);
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
    private void RecentPath() {
        recentPathBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {

                                startPlace = jsonResponse.getString("startPlace");
                                endPlace =  jsonResponse.getString("endPlace");
                                kilometer = jsonResponse.getString("kilometer");

                                startLat = jsonResponse.getString("startLat");
                                startLon = jsonResponse.getString("startLon");
                                destLat = jsonResponse.getString("destLat");
                                destLon = jsonResponse.getString("destLon");

                                startText.setText(startPlace);
                                startText.setTextColor(Color.BLACK);
                                startText.setTypeface(null, Typeface.BOLD);

                                destText.setText(endPlace);
                                destText.setTextColor(Color.BLACK);
                                destText.setTypeface(null, Typeface.BOLD);

                                distanceText  .setText(jsonResponse.getString("kilometer"));

                                boolean isStart = startPlace.equals("출발지 입력"); // true: 입력X, false: 입력O
                                boolean isDest = endPlace.equals("도착지 입력");
                                calculateDistance(isStart, isDest);

                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                };
                CarRecentRequest carRecentRequest = new CarRecentRequest(responseListener);
                RequestQueue queue = Volley.newRequestQueue(CarJoinActivity.this);
                queue.add(carRecentRequest);

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
                        destText.setTextColor(Color.GRAY);
                        destText.setTypeface(null, Typeface.NORMAL);
                        startText.setTextColor(Color.BLACK);
                        startText.setTypeface(null, Typeface.BOLD);
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
                        startText.setTextColor(Color.GRAY);
                        startText.setTypeface(null, Typeface.NORMAL);
                        destText.setTextColor(Color.BLACK);
                        destText.setTypeface(null, Typeface.BOLD);
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
        viewStartTxt.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarJoinActivity.this, ListActivity.class);
                intent.putExtra("curLat", nowLat);
                intent.putExtra("curLon", nowLon);
                intent.putExtra("curAddr", nowName);
                startActivityForResult(intent, 0);
            }
        });

        viewEndTxt.setOnClickListener(new TextView.OnClickListener() {

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
        viewEndDate.setOnClickListener(new View.OnClickListener() {
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
                        end_day = i + "/" + i1 + "/" + i2;
                        txtDate2.setText(end_day);
                        txtDate2.setTextColor(Color.BLACK);
                        txtDate2.setTypeface(null, Typeface.BOLD);

                    }
                }, y, m, d);
                pickerDialog.show();

            }
        });


        viewEndTime.setOnClickListener(new View.OnClickListener() {
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
                        end_time = hourOfDay + ":" + minute ;
                        txtTime2.setText(end_time + " " + format);
                        txtTime2.setTextColor(Color.BLACK);
                        txtTime2.setTypeface(null, Typeface.BOLD);

                    }
                }, hour, minute, false);

                timePickerDialog.show();

            }

        });
    }

    private void startDateTime() {
        viewStartDate.setOnClickListener(new View.OnClickListener() {
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
                        start_day = i + "/" + i1 + "/" + i2;
                        txtDate.setText(start_day);
                        txtDate.setTextColor(Color.BLACK);
                        txtDate.setTypeface(null, Typeface.BOLD);

                        end_day = i + "/" + i1 + "/" + i2;
                        txtDate2.setText(start_day);
                        txtDate2.setTextColor(Color.BLACK);
                        txtDate2.setTypeface(null, Typeface.BOLD);
                    }
                }, y, m, d);
                pickerDialog.show();

            }
        });

        viewStartTime.setOnClickListener(new View.OnClickListener() {
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

                        String t = hourOfDay + ":" + minute + " " + format;
                        start_time = hourOfDay + ":" + minute;
                        txtTime.setText(t);
                        txtTime.setTextColor(Color.BLACK);
                        txtTime.setTypeface(null, Typeface.BOLD);
                        startM = hourOfDay * 60 + minute;

                        if(fromToInfo != null) {
                            if(startM > 0 && fromToInfo.totTime > 0) {
                                int destT = (startM + fromToInfo.totTime) / 60;
                                int destM = (startM + fromToInfo.totTime) % 60;

                                seletedTimeFormat(destT);

                                end_time = destT + ":" + destM;
                                txtTime2.setText(end_time + " " + format);
                                txtTime2.setTextColor(Color.BLACK);
                                txtTime2.setTypeface(null, Typeface.BOLD);
                            }
                        }
                    }
                }, hour, minute, false);
                Log.d("StartTime", "hour: " + hour + "minute: " + minute);
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
        recentPathBtn = findViewById(R.id.recentPathBtn);
        txtCar = (TextView) findViewById(R.id.txtCar);
        bottomnav = findViewById(R.id.bottom_navigation);
        bottomnav.setOnNavigationItemSelectedListener(navListener);
        totalBox = (LinearLayout) findViewById(R.id.totalBox);
        backPressCloseHandler = new BackPressCloseHandler(this);

        viewCar = (LinearLayout) findViewById(R.id.viewCar);
        viewStartTxt = (LinearLayout) findViewById(R.id.viewStartTxt);
        viewEndTxt = (LinearLayout) findViewById(R.id.viewEndTxt);
        viewStartDate = (LinearLayout) findViewById(R.id.viewStartDate);
        viewStartTime = (LinearLayout) findViewById(R.id.viewStartTime);
        viewEndDate = (LinearLayout) findViewById(R.id.viewEndDate);
        viewEndTime = (LinearLayout) findViewById(R.id.viewEndTime);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.setting) {
            Intent intent = new Intent(CarJoinActivity.this, CarSetActivity.class);
            intent.putExtra("isGPSEnable", gps);
            intent.putExtra("userID", userID);
            intent.putExtra("mycar", mycar);
            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon",nowLon);
            intent.putExtra("nowName", nowName);
            startActivity(intent);

        }
        if (id == R.id.logout) {

            if (preventButtonTouch == true) { return true; }

            preventButtonTouch = true;
            Intent intent = new Intent(CarJoinActivity.this, LoginActivity.class);

            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon", nowLon);
            intent.putExtra("nowName", nowName);
            intent.putExtra("isGPSEnable", gps);
            intent.putExtra("mycar", mycar);
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
        if (id == R.id.newPost) {

            if (preventButtonTouch == true) {
                return true;
            }


            final String carNum = txtCar.getText().toString();
            String startday = txtDate.getText().toString();
            String endday = txtDate2.getText().toString();
            String startTime = txtTime.getText().toString();
            String endTime = txtTime2.getText().toString();


            if (!startday.equals("출발 날짜를 선택해주세요") && !endday.equals("도착 날짜를 선택해주세요")){

                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                Date day1 = null;
                Date day2 = null;
                try {
                    day1 = format.parse(start_day);
                    day2 = format.parse(end_day);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                compare = day1.compareTo(day2);
            }


            if (!startTime.equals("출발 시간을 선택해주세요") && !endTime.equals("도착 시간을 선택해주세요")) {
                SimpleDateFormat f = new SimpleDateFormat("HH:mm", Locale.KOREA);
                Date d1 = null;
                Date d2 = null;
                try {
                    d1 = f.parse(start_time);
                    d2 = f.parse(end_time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long diff = d1.getTime() - d2.getTime();
                sec = diff / 1000;
            }

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

            } else if ( compare > 0 ) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CarJoinActivity.this);
                builder.setMessage("도착시각이 출발시각보다 빠릅니다")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();
            }
            else if(compare==0 && sec >0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(CarJoinActivity.this);
                    builder.setMessage("도착시각이 출발시각보다 빠릅니다")
                            .setNegativeButton("확인", null)
                            .create()
                            .show();

            }
            else {

                preventButtonTouch = true;

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
                                                startText.setText(first_gps);
                                                destText.setText(endPlace);
                                                destText.setTextColor(Color.GRAY);
                                                destText.setTypeface(null, Typeface.NORMAL);
                                                distanceText.setText("");
                                                txtCar.setText(carNum);
                                                txtTime.setText("출발 시간을 선택해주세요");
                                                txtTime.setTextColor(Color.GRAY);
                                                txtTime.setTypeface(null, Typeface.NORMAL);
                                                txtTime2.setText("도착 시간을 선택해주세요");
                                                txtTime2.setTextColor(Color.GRAY);
                                                txtTime2.setTypeface(null, Typeface.NORMAL);
                                                txtDate.setText("출발 날짜를 선택해주세요");
                                                txtDate.setTextColor(Color.GRAY);
                                                txtDate.setTypeface(null, Typeface.NORMAL);
                                                txtDate2.setText("도착 날짜을 선택해주세요");
                                                txtDate2.setTextColor(Color.GRAY);
                                                txtDate2.setTypeface(null, Typeface.NORMAL);

                                                AlertDialog.Builder builder = new AlertDialog.Builder(CarJoinActivity.this);

                                                preventButtonTouch = false;
                                                builder.setMessage("성공적으로 등록 되었습니다")
                                                        .setPositiveButton("확인", null)
                                                        .create()
                                                        .show();

                                            } else {

                                                preventButtonTouch = false;
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
                                String id = userID;
                                String carNum = txtCar.getText().toString();
                                String startday = txtDate.getText().toString();
                                String endday = txtDate2.getText().toString();
                                String startTime = txtTime.getText().toString();
                                String endTime = txtTime2.getText().toString();
                                kilometer = distanceText.getText().toString();

                                CarJoinRequest carJoinRequest = new CarJoinRequest(id, carNum, startPlace, endPlace, kilometer, startday, endday, startTime, endTime, no, startLat, startLon, destLat, destLon, responseListener);
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
        for (int i = 0; i < menu.size(); i++) {
            Log.d("CarjoinActivity-menu", String.valueOf(menu.size()));
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            if (i == 0) {

            }
            int end = spanString.length();
            spanString.setSpan(new AbsoluteSizeSpan(37), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            item.setTitle(spanString);
        }
        return true;
    }


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

            String start = str_yy + "/" + str_mm + "/1";
            String end = str_yy + "/" + str_mm + "/" + day;
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

            Intent intent = new Intent(CarJoinActivity.this, CarManegementActivity.class);
            intent.putExtra("userList", result);
            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon", nowLon);
            intent.putExtra("isGPSEnable", gps);
            intent.putExtra("nowName", nowName);
            intent.putExtra("userID", userID);
            intent.putExtra("str_yy", str_yy);
            intent.putExtra("str_mm", str_mm);
            intent.putExtra("mycar", mycar);
            CarJoinActivity.this.startActivity(intent);

            finish();
            overridePendingTransition(0, 0);

        }

    }

    class BackgroundTask2 extends AsyncTask<Void, Void, String> {

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

            Intent intent = new Intent(CarJoinActivity.this, BCListActivity.class);
            intent.putExtra("userList", result);
            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon", nowLon);
            intent.putExtra("isGPSEnable", gps);
            intent.putExtra("nowName", nowName);
            intent.putExtra("userID", userID);
            intent.putExtra("mycar", mycar);
            intent.putExtra("listname", "등록순");
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
                fromToInfo = (FromToInfo) new Task(destLon, destLat, startLon, startLat).execute().get();
                Log.d("fromToInfo", "time: " + fromToInfo.totTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Log.d("CarJoinActivity", "distance 계산 후: " + distance);
            if (fromToInfo.distance > -1) {
                Log.d("CarJoinActivity", "distance > -1 " + distance);
                final float distanceKM = (float) (fromToInfo.distance / 1000 + (distance % 1000) * 0.001);
                kilometer = Float.toString(distanceKM);
                AlertDialog.Builder userdistanceBuilder = new AlertDialog.Builder(CarJoinActivity.this);

                String short_start = startPlace;
                String short_end = endPlace;

                if (startPlace.length() > 12) {
                    short_start = startPlace.substring(0, 12) + "..";
                }
                if (endPlace.length() > 12) {
                    short_end = endPlace.substring(0, 12) + "..";
                }
                userdistanceBuilder.setTitle("출발지 : "+ short_start +"\n"  + "도착지 : "+ short_end + "\n" );
                userdistanceBuilder.setMessage("예상 거리 : "+kilometer + "km"  +"\n" +"소요 시간 : "+ fromToInfo.totTime + "분")
                        .setCancelable(false)
                        .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                distanceText.setText(kilometer);
                                distanceText.setTextColor(Color.BLACK);
                                distanceText.setTypeface(null, Typeface.BOLD);
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
                startText.setTextColor(Color.BLACK);
                startText.setTypeface(null, Typeface.BOLD);
            }
        }
        if (requestCode == 1) {
            if (resultCode == 1 && data != null) {
                endPlace = data.getStringExtra("destname");
                destLat = data.getStringExtra("destlat");
                destLon = data.getStringExtra("destlon");

                destText.setText(endPlace);
                destText.setTextColor(Color.BLACK);
                destText.setTypeface(null, Typeface.BOLD);
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
                if (isStart) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CarJoinActivity.this);
                    builder.setMessage(" 출발지를 입력해주세요 ")
                            .setNegativeButton("확인", null)
                            .create()
                            .show();

                    return;
                }
                if (isDest) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CarJoinActivity.this);
                    builder.setMessage(" 도착지를 입력해주세요 ")
                            .setNegativeButton("확인", null)
                            .create()
                            .show();

                    return;
                }

                if (startLat == destLat && startLon == destLon) {
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
                    public void onNegativeClicked() {
                    }
                });
                editDialog.show();
            }
        });
    }

    // 뒤로가기 두번 누르면 종료
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }



}
