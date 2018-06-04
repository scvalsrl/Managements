package com.example.mingi.management;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MINGI on 2018-04-27.
 */

public class DeleteRequest2 extends StringRequest {
    final static private String URL = "http://scvalsrl.cafe24.com/BCDelete.php";
    private Map<String, String> parameters;

    public DeleteRequest2(int No, Response.Listener<String> listener) {

        super(Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("no", No + "");


    }


    @Override
    public Map<String, String> getParams() {

        return parameters;

    }
}
