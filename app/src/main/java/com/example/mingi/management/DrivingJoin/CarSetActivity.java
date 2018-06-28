package com.example.mingi.management.DrivingJoin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.mingi.management.BusinessCard.BCCountRequest;
import com.example.mingi.management.BusinessCard.BCEditActivity;
import com.example.mingi.management.BusinessCard.BCUpdateRequest;
import com.example.mingi.management.R;

import org.json.JSONException;
import org.json.JSONObject;

public class CarSetActivity extends AppCompatActivity {


    TextView txtCar;
    String userID, mycar, isGPSEnable, nowLat, nowLon, nowName , carNum;
    boolean preventButtonTouch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_set);

        final ActionBar abar = getSupportActionBar();;//line under the action bar
        View viewActionBar = getLayoutInflater().inflate(R.layout.title_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("차량지정");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setHomeButtonEnabled(true);


        Intent fromSplash = getIntent();
        userID = fromSplash.getStringExtra("userID");
        mycar = fromSplash.getStringExtra("mycar");
        isGPSEnable = fromSplash.getStringExtra("isGPSEnable");
        nowLat = fromSplash.getStringExtra("nowLat");
        nowLon = fromSplash.getStringExtra("nowLon");
        nowName = fromSplash.getStringExtra("nowName");

        txtCar = (TextView) findViewById(R.id.txtCar);

        if (!mycar.equals("차량을 선택해주세요")) {
            txtCar.setText(mycar);
            txtCar.setTextColor(Color.BLACK);
            txtCar.setTypeface(null, Typeface.BOLD);
        }

        txtCar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.txtCar:

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(CarSetActivity.this, R.style.MyAlertDialogStyle);
                        alertBuilder.setIcon(R.drawable.ic_directions_car_black_24dp);
                        alertBuilder.setTitle("차량을 선택해주세요");

                        // List Adapter 생성
                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                CarSetActivity.this,
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bcupdate_menu, menu);
        MenuItem item = menu.getItem(0);
        SpannableString spanString = new SpannableString(menu.getItem(0).getTitle().toString());
        int end = spanString.length();
        spanString.setSpan(new AbsoluteSizeSpan(40), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        item.setTitle(spanString);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if( id == android.R.id.home){
            finish();
            return true;
        }
        if( id == R.id.vbcupdate2) {

            if (preventButtonTouch == true) {
                return true;
            }

            preventButtonTouch = true;


            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if (success) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(CarSetActivity.this);
                            preventButtonTouch = false;
                            builder.setMessage("차량이 등록 되었습니다")
                                    .setPositiveButton("확인", null)
                                    .create()
                                    .show();

                            Intent intent = new Intent(CarSetActivity.this, CarJoinActivity.class);

                            intent.putExtra("isGPSEnable", isGPSEnable);
                            intent.putExtra("userID", userID);
                            intent.putExtra("mycar", carNum);
                            intent.putExtra("nowLat", nowLat);
                            intent.putExtra("nowLon",nowLon);
                            intent.putExtra("nowName", nowName);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            CarSetActivity.this.startActivity(intent);

                            finish();

                        } else {

                            preventButtonTouch = false;
                            AlertDialog.Builder builder = new AlertDialog.Builder(CarSetActivity.this);
                            builder.setMessage("등록에 실패 했습니다.")
                                    .setNegativeButton("다시시도", null).create().show();
                            Intent intent = new Intent(CarSetActivity.this, CarJoinActivity.class);
                            CarSetActivity.this.startActivity(intent);

                        }

                    } catch (JSONException e) {

                        e.printStackTrace();

                    }
                }

            };

            // 화면전환 넣기 //
            carNum = txtCar.getText().toString();
            CarSetRequest carSetRequest = new CarSetRequest(userID, carNum, responseListener);
            RequestQueue queue = Volley.newRequestQueue(CarSetActivity.this);
            queue.add(carSetRequest);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
