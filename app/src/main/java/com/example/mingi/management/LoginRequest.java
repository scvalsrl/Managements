package com.example.mingi.management;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class LoginRequest extends StringRequest {

    final static private String URL = "http://scvalsrl.cafe24.com/Login.php";

    private Map<String, String> parameters;

    public LoginRequest(String userID, String userPassword, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);


        // ID 와 비밀번호를 파라메터로 가져와서 PHP로 가져감
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPassword", userPassword);


    }

    @Override
    public Map<String, String> getParams() {

        return parameters;

    }


}
