package com.example.mingi.management.DrivingJoin;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MINGI on 2018-04-27.
 */

public class UpdateRequest extends StringRequest {
    final static private String URL = "http://scvalsrl.cafe24.com/CarUpdate.php";
    private Map<String, String> parameters;

    public UpdateRequest(int carNo, Response.Listener<String> listener){

        super(Method.POST,URL , listener , null);

        parameters = new HashMap<>();
        parameters.put("no", carNo + "");


    }


    @Override
    public Map<String , String> getParams(){

        return parameters;
    }

}
