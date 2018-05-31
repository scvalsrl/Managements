package com.example.mingi.management;

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
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.List;


public class CarListAdapter extends BaseAdapter {

    private Context context;
    private List<Car> userList;
    private Activity parentActivity;

    private String isGPSEnable;
    private  String nowLat;
    private  String nowLon;
    private  String nowName;


    public CarListAdapter(Context context, List<Car> userList , Activity parentActivity, String isGPSEnable, String nowLat, String nowLon, String nowName ){
        this.context = context;
        this.userList = userList;
        this.parentActivity = parentActivity;
        this.isGPSEnable = isGPSEnable;
        this.nowLat = nowLat;
        this.nowLon = nowLon;
        this.nowName = nowName;
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
    public View getView(final int i, View view, ViewGroup viewGroup){


        View v = View.inflate(context, R.layout.car,null);

        TextView startPlace = (TextView) v.findViewById(R.id.startPlace);
        TextView endPlace = (TextView) v.findViewById(R.id.endPlace);
        TextView startTime = (TextView) v.findViewById(R.id.startTime);
        TextView endTime = (TextView) v.findViewById(R.id.endTime);
        TextView  startDay = (TextView) v.findViewById(R.id.startDay);
        TextView endDay = (TextView) v.findViewById(R.id.endDay);
        TextView kilometer = (TextView) v.findViewById(R.id.kilometer);
        TextView carNum = (TextView) v.findViewById(R.id.carNum);
        final TextView carNo = (TextView) v.findViewById(R.id.carNo);



        startPlace.setText(userList.get(i).getStartPlace());
        endPlace.setText(userList.get(i).getEndPlace());
        startTime.setText(userList.get(i).getStartTime());
        endTime.setText(userList.get(i).getEndTime());
        startDay.setText(userList.get(i).getStartDay());
        endDay.setText(userList.get(i).getEndDay());
        kilometer.setText(userList.get(i).getKilometer());
        carNum.setText(userList.get(i).getCarNum());
        carNo.setText(String.valueOf(userList.get(i).getNo()));



        v.setTag(userList.get(i).getNo());


        Button deleteButton = (Button) v.findViewById(R.id.deleteButton);
        Button updateButton = (Button) v.findViewById(R.id.updateButton);


        updateButton.setOnClickListener(new View.OnClickListener() {




            @Override
            public void onClick(View v) {


                Log.d("  1111 : ", "1");
                Response.Listener<String> responseListener = new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        try{
                            Log.d("  2222 : ", "1");
                            JSONObject jsonResponse = new JSONObject(response);
                            Log.d("  3333 : ", "1");
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

                            if(success){
                                Log.d("  업데이트 성공 : " , " ");


                                // 인텐드에 넣기
                                Intent intent = new Intent(parentActivity , CarUpdateActivity.class);

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

                                intent.putExtra("startLat", startLat);
                                intent.putExtra("startLon", startLon);
                                intent.putExtra("destLat", destLat);
                                intent.putExtra("destLon",destLon);



                                intent.putExtra("isGPSEnable",isGPSEnable);
                                intent.putExtra("nowLat",nowLat);
                                intent.putExtra("nowLon",nowLon);
                                intent.putExtra("nowName",nowName);


                                parentActivity.startActivity(intent);
                                // 화면전환 넣기 //


                            }else{
                                Log.d("  삭제실패 : ", "1");
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                };


                String carNo_s=carNo.getText().toString();
                int carNo_i  = Integer.parseInt(carNo_s);
                Log.d("  업데이트 리퀘스트 생성 요청 : "+carNo_s, "");
                UpdateRequest updateRequest = new UpdateRequest(carNo_i, responseListener);
                RequestQueue queue = Volley.newRequestQueue(parentActivity);
                queue.add(updateRequest);

            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener(){

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




                Log.d("  1111 : ", "1");
                Response.Listener<String> responseListener = new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        try{
                            Log.d("  2222 : ", "1");
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){

                                userList.remove(i);
                                notifyDataSetChanged();

                            }else{
                                Log.d("  삭제실패 : ", "1");
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                };


                String carNo_s=carNo.getText().toString();
                int carNo_i  = Integer.parseInt(carNo_s);

                Log.d("  딜리트르퀘스트 생성 요청 : "+carNo_i, "");
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
}
