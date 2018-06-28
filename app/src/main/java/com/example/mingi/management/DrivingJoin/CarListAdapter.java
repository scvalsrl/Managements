package com.example.mingi.management.DrivingJoin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.mingi.management.R;

import org.json.JSONObject;

import java.util.List;


public class CarListAdapter extends BaseAdapter {

    private Context context;
    private List<Car> userList;
    private Activity parentActivity;
    PersonViewHolder viewHolder;

    private String isGPSEnable;
    private String nowLat;
    private String nowLon;
    private String nowName;
    private String mycar;
    private String str_mm;
    private String str_yy;


    public CarListAdapter(Context context, List<Car> userList, Activity parentActivity, String isGPSEnable, String nowLat, String nowLon, String nowName, String mycar, String str_mm, String str_yy) {
        this.context = context;
        this.userList = userList;
        this.parentActivity = parentActivity;
        this.isGPSEnable = isGPSEnable;
        this.nowLat = nowLat;
        this.nowLon = nowLon;
        this.nowName = nowName;
        this.mycar = mycar;
        this.str_mm = str_mm;
        this.str_yy = str_yy;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int i) {
        return userList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(final int i, View v, ViewGroup viewGroup) {


        if(v==null) {
            v = View.inflate(context, R.layout.car, null);

            viewHolder = new PersonViewHolder();
            viewHolder. startPlace = (TextView) v.findViewById(R.id.startPlace);
            viewHolder. endPlace = (TextView) v.findViewById(R.id.endPlace);
            viewHolder. startTime = (TextView) v.findViewById(R.id.startTime);
            viewHolder. endTime = (TextView) v.findViewById(R.id.endTime);
            viewHolder. startDay = (TextView) v.findViewById(R.id.startDay);
            viewHolder. endDay = (TextView) v.findViewById(R.id.endDay);
            viewHolder. kilometer = (TextView) v.findViewById(R.id.kilometer);
            viewHolder. carNum = (TextView) v.findViewById(R.id.carNum);
            viewHolder. carNo = (TextView) v.findViewById(R.id.carNo);

            v.setTag(viewHolder);

        } else
        {
            viewHolder = (PersonViewHolder) v.getTag();
        }


        viewHolder.startPlace.setText(userList.get(i).getStartPlace());
        viewHolder.endPlace.setText(userList.get(i).getEndPlace());
        viewHolder.startTime.setText(userList.get(i).getStartTime());
        viewHolder.endTime.setText(userList.get(i).getEndTime());
        viewHolder.startDay.setText(userList.get(i).getStartDay());
        viewHolder.endDay.setText(userList.get(i).getEndDay());
        viewHolder.kilometer.setText(userList.get(i).getKilometer());
        viewHolder.carNum.setText(userList.get(i).getCarNum());
        viewHolder.carNo.setText(String.valueOf(userList.get(i).getNo()));

        ImageView deleteButton = (ImageView) v.findViewById(R.id.deleteButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);

                builder
                        .setMessage("운행기록을 삭제 합니다")
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

                                                        userList.remove(i);
                                                        notifyDataSetChanged();

                                                    } else {
                                                        Log.d("  삭제실패 : ", "1");
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }

                                        };


                                        String carNo_s = viewHolder.carNo.getText().toString();
                                        int carNo_i = Integer.parseInt(carNo_s);

                                        DeleteRequest deleteRequest = new DeleteRequest(carNo_i, responseListener);
                                        RequestQueue queue = Volley.newRequestQueue(parentActivity);
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


            }


        });


        return v;
    }

    public class PersonViewHolder
    {
        public TextView startPlace;
        public TextView endPlace  ;
        public TextView startTime ;
        public TextView endTime ;
        public TextView startDay;
        public TextView endDay;
        public TextView kilometer;
        public TextView carNum;
        public TextView carNo;
    }

}
