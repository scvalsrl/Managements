package com.example.mingi.management;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MINGI on 2017-12-09.
 */

public class CarJoinRequest extends StringRequest {

    final static  private String URL = "http://scvalsrl.cafe24.com/CarRegister.php";

    private Map<String, String> parameters;


    public CarJoinRequest(String id , String carNum, String start , String end, String kilometer, String day1 , String day2 ,  String time1 , String time2 , int no ,
                          String startLat , String startLon , String destLat, String destLon , Response.Listener<String> listener){

        super(Method.POST,URL , listener, null);


        Log.d("김민기 조인 리퀘스트 들어옴", "id: "+ id);

        parameters = new HashMap<>();

        parameters.put("id", id);
        parameters.put("carNum", carNum);

        parameters.put("startPlace", start);
        parameters.put("endPlace", end);
        parameters.put("kilometer", kilometer);



        parameters.put("startDay", day1);
        parameters.put("endDay", day2);
        parameters.put("startTime", time1);
        parameters.put("endTime", time2);

        parameters.put("no", no + "");

        parameters.put("startLat", startLat);
        parameters.put("startLon", startLon);
        parameters.put("destLat", destLat);
        parameters.put("destLon", destLon);

    }

    @Override
    public Map<String, String> getParams(){

        return parameters;
    }


}
