package com.example.mingi.management.DrivingJoin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MINGI on 2017-12-09.
 */

public class CarSetRequest extends StringRequest {

    final static private String URL = "http://scvalsrl.cafe24.com/CarSet.php";

    private Map<String, String> parameters;


    public CarSetRequest(String id, String carNum, Response.Listener<String> listener) {

        super(Method.POST, URL, listener, null);


        parameters = new HashMap<>();
        parameters.put("id", id);
        parameters.put("carNum", carNum);

    }

    @Override
    public Map<String, String> getParams() {

        return parameters;
    }


}
