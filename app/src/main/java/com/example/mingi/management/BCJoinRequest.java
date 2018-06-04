package com.example.mingi.management;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MINGI on 2017-12-09.
 */

public class BCJoinRequest extends StringRequest {

    final static private String URL = "http://scvalsrl.cafe24.com/BCRegister.php";

    private Map<String, String> parameters;


    public BCJoinRequest(String id, String BC_name, String BC_level
            , String BC_com, String BC_phone, String BC_mail, String BC_add
            , String BC_lat, String BC_lon, String BC_photo
            , int no, Response.Listener<String> listener) {

        super(Method.POST, URL, listener, null);


        parameters = new HashMap<>();

        parameters.put("id", id);
        parameters.put("BC_name", BC_name);
        parameters.put("BC_level", BC_level);
        parameters.put("BC_com", BC_com);
        parameters.put("BC_phone", BC_phone);
        parameters.put("BC_mail", BC_mail);
        parameters.put("BC_add", BC_add);
        parameters.put("BC_lat", BC_lat);
        parameters.put("BC_lon", BC_lon);
        parameters.put("BC_photo", BC_photo);
        parameters.put("no", no + "");


    }

    @Override
    public Map<String, String> getParams() {

        return parameters;
    }


}
