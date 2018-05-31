package com.example.mingi.management;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;


public class BCCountRequest extends StringRequest {
// 나도 수정했는디?
    final static  private String URL = "http://scvalsrl.cafe24.com/Count2.php";

    private Map<String, String> parameters;

    public BCCountRequest(Response.Listener<String> listener){
        super(Method.POST,URL , listener, null);
        Log.d("  카운트 리퀘스트 접속 : ", "1");
    }

    @Override
    public Map<String, String> getParams(){

        return parameters;

    }


}
