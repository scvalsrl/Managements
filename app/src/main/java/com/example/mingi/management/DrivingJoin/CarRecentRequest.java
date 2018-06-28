package com.example.mingi.management.DrivingJoin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;


public class CarRecentRequest extends StringRequest {

    final static private String URL = "http://scvalsrl.cafe24.com/CarRecent.php";

    private Map<String, String> parameters;

    public CarRecentRequest(Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

    }

    @Override
    public Map<String, String> getParams() {

        return parameters;

    }


}
