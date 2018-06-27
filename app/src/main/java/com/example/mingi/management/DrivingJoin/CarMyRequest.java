package com.example.mingi.management.DrivingJoin;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MINGI on 2017-12-09.
 */

public class CarMyRequest extends StringRequest {

    final static private String URL = "http://scvalsrl.cafe24.com/CarMy.php";

    private Map<String, String> parameters;


    public CarMyRequest(String id, Response.Listener<String> listener) {

        super(Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("id", id);
        Log.d("김민기", "carmy 들어옴: " + id);
    }

    @Override
    public Map<String, String> getParams() {
        Log.d("김민기", "carmy 겟파람: ");
        return parameters;
    }


}
