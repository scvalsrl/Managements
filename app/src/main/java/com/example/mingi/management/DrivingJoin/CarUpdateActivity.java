package com.example.mingi.management.DrivingJoin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.mingi.management.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class CarUpdateActivity extends AppCompatActivity {
    /*mingi*/
    int count = 0;
    TextView txtDate, txtTime, txtDate2, txtTime2, txtCar;

    Calendar currentTime;
    int hour, minute;
    String format;
    String gps;
    String userID, no;

    int y, m, d;

    private DrawerLayout mDrawerLayout = null;

    /*yoonju*/
    TextView startText, destText, distanceText;
    Button changeBtn;
    String startPlace = "출발지 입력";
    String endPlace = "도착지 입력";
    String startLat, startLon, destLat, destLon, nowName, kilometer;
    int distance = -1;

    final Context context = this;

    String nowLat = "129.065782";
    String nowLon = "35.145404";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_update);
        findcontrol();


        ActionBar actionBar = getSupportActionBar();


        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("수정 화면");




        Intent fromSplash = getIntent();
        String isGPSEnable = fromSplash.getStringExtra("isGPSEnable");
        userID = fromSplash.getExtras().getString("id");
        String carNum_ = fromSplash.getExtras().getString("carNum");
        startPlace = fromSplash.getExtras().getString("startPlace");
        endPlace = fromSplash.getExtras().getString("endPlace");
        String startTime_ = fromSplash.getExtras().getString("startTime");
        String startDay_ = fromSplash.getExtras().getString("startDay");
        String endTime_ = fromSplash.getExtras().getString("endTime");
        String endDay_ = fromSplash.getExtras().getString("endDay");
        no = fromSplash.getExtras().getString("no");
        String kilometer_ = fromSplash.getExtras().getString("kilometer");

        startLat = fromSplash.getExtras().getString("startLat");
        startLon = fromSplash.getExtras().getString("startLon");
        destLat = fromSplash.getExtras().getString("destLat");
        destLon = fromSplash.getExtras().getString("destLon");


        txtCar.setText(carNum_);
        startText.setText(startPlace);
        destText.setText(endPlace);
        distanceText.setText(kilometer_);
        txtDate.setText(startDay_);
        txtTime.setText(startTime_);
        txtDate2.setText(endDay_);
        txtTime2.setText(endTime_);


        gps = isGPSEnable;

        // get current info
        if (isGPSEnable.compareTo("0") == 0) { // success

            // reverse Geo
            nowLat = fromSplash.getStringExtra("nowLat");
            nowLon = fromSplash.getStringExtra("nowLon");
            nowName = fromSplash.getStringExtra("nowName");

            boolean isStart = startPlace.equals("출발지 입력"); // true: ?낅젰X, false: ?낅젰O
            boolean isDest = endPlace.equals("도착지 입력");
            calculateDistance(isStart, isDest);
        }


        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                d = calendar.get(Calendar.DAY_OF_MONTH);
                m = calendar.get(Calendar.MONTH);
                y = calendar.get(Calendar.YEAR);

                DatePickerDialog pickerDialog = new DatePickerDialog(CarUpdateActivity.this, new DatePickerDialog.OnDateSetListener() {
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(CarUpdateActivity.this, new TimePickerDialog.OnTimeSetListener() {
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


        //////////////////////////////////


        txtDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                d = calendar.get(Calendar.DAY_OF_MONTH);
                m = calendar.get(Calendar.MONTH);
                y = calendar.get(Calendar.YEAR);

                DatePickerDialog pickerDialog = new DatePickerDialog(CarUpdateActivity.this, new DatePickerDialog.OnDateSetListener() {
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(CarUpdateActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

        ///////////
        startText.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarUpdateActivity.this, ListActivity.class);
                intent.putExtra("curLat", nowLat);
                intent.putExtra("curLon", nowLon);
                intent.putExtra("curAddr", nowName);
                startActivityForResult(intent, 0);
            }
        });

        destText.setOnClickListener(new TextView.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarUpdateActivity.this, DestListActivity.class);
                intent.putExtra("curLat", nowLat);
                intent.putExtra("curLon", nowLon);
                intent.putExtra("curAddr", nowName);
                startActivityForResult(intent, 1);
            }
        });

        changeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean isChange = false;

                boolean isStart = startPlace.equals("출발지 입력"); // true: 입력X, false: 입력O
                boolean isDest = CarUpdateActivity.this.endPlace.equals("도착지 입력");
                Animation anim = AnimationUtils.loadAnimation(
                        getApplicationContext(),
                        R.anim.rotation);

                String temp = startPlace;
                startPlace = endPlace;
                endPlace = temp;

                temp = startLat;
                startLat = destLat;
                destLat = temp;

                temp = startLon;
                startLon = destLon;
                destLon = temp;

                startText.setText(startPlace);
                destText.setText(CarUpdateActivity.this.endPlace);
                changeBtn.startAnimation(anim);
            }
        });



        txtCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                switch (v.getId()) {
                    case R.id.txtCar:

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(CarUpdateActivity.this, R.style.MyAlertDialogStyle);
                        alertBuilder.setIcon(R.drawable.ic_directions_car_black_24dp);
                        alertBuilder.setTitle("차량을 선택해주세요");

                        // List Adapter 생성
                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                CarUpdateActivity.this,
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


    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.carupdate_menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if( id == android.R.id.home){

            finish();
            return true;

        }

        if( id == R.id.carupdate ){

            startPlace = startText.getText().toString();
            CarUpdateActivity.this.endPlace = destText.getText().toString();
            kilometer = distanceText.getText().toString();
            String carNum = txtCar.getText().toString();
            String startday = txtDate.getText().toString();
            String endday = txtDate2.getText().toString();
            String startTime = txtTime.getText().toString();
            String endTime = txtTime2.getText().toString();

            if (startLat.equals(destLat) && startLon.equals(destLon)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CarUpdateActivity.this);
                builder.setMessage(" 출발지와 목적지가 동일합니다.")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();

            } else if (carNum.equals("차량을 선택해주세요")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CarUpdateActivity.this);
                builder.setMessage(" 차량을 선택해주세요.")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();

            } else if (startPlace.equals("출발지 입력") || CarUpdateActivity.this.endPlace.equals("도착지 입력")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CarUpdateActivity.this);
                builder.setMessage(" 경로를 설정해주세요.")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();

            } else if (startday.equals("출발 날짜를 선택해주세요") || startTime.equals("출발 시간을 선택해주세요")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CarUpdateActivity.this);
                builder.setMessage(" 출발 일자를 설정해주세요.")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();

            } else if (endday.equals("도착 날짜를 선택해주세요") || endTime.equals("도착 시간을 선택해주세요")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CarUpdateActivity.this);
                builder.setMessage(" 도착 일자를 설정해주세요.")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();


            } else {


                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {


                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");


                            if (success) {

                                Log.d("업데이트 ", "onResponse: 성공");
                                AlertDialog.Builder builder = new AlertDialog.Builder(CarUpdateActivity.this);

                                builder.setMessage("성공적으로 수정 되었습니다")
                                        .setCancelable(false)
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog, int id) {
                                                // 프로그램을 종료한다
                                                new BackgroundTask().execute();
                                                overridePendingTransition(0, 0);
                                                finish();

                                            }
                                        }).
                                        create()
                                        .show();


                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(CarUpdateActivity.this);
                                builder.setMessage("등록에 실패 했습니다.")
                                        .setNegativeButton("다시시도", null).create().show();
                                Intent intent = new Intent(CarUpdateActivity.this, CarUpdateActivity.class);
                                CarUpdateActivity.this.startActivity(intent);

                            }

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }


                    }


                };


                Log.d("김민기 업데이트 생성 ", " ");
                CarUpdateRequest carUpdateRequest = new CarUpdateRequest(userID, carNum, startPlace, CarUpdateActivity.this.endPlace, kilometer, startday, endday, startTime, endTime, Integer.parseInt(no), startLat, startLon, destLat, destLon, responseListener);
                RequestQueue queue = Volley.newRequestQueue(CarUpdateActivity.this);

                queue.add(carUpdateRequest);


            }
            return true;

        }

        return super.onOptionsItemSelected(item);
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

            Intent intent = new Intent(CarUpdateActivity.this, CarManegementActivity.class);
            intent.putExtra("userList", result);
            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon", nowLon);
            intent.putExtra("isGPSEnable", gps);
            intent.putExtra("nowName", nowName);
            intent.putExtra("userID", userID);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            CarUpdateActivity.this.startActivity(intent);
            overridePendingTransition(0, 0);
            finish();

        }

    }

    void calculateDistance(boolean isStart, boolean isDest) {
        if (!isStart && !isDest) {
            if (startLat.equals(destLat) && startLon.equals(destLon)) {
                distanceText.setText(" 0 km");

                AlertDialog.Builder builder = new AlertDialog.Builder(CarUpdateActivity.this);
                builder.setMessage(" 출발지와 목적지가 같습니다 ")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();

                return;
            }
            try {
                distance = (int) new Task(destLon, destLat, startLon, startLat).execute().get();
                //Toast.makeText(getApplicationContext(), "distance: " + distance, Toast.LENGTH_SHORT).show();
                if (distance > -1) {
                    float distanceKM = (float) (distance / 1000 + (distance % 1000) * 0.001);
                    kilometer = Float.toString(distanceKM);
                    distanceText.setText(kilometer);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
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

}
