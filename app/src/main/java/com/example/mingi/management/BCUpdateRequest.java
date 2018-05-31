package com.example.mingi.management;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MINGI on 2018-04-27.
 */

public class BCUpdateRequest extends StringRequest {
    final static private String URL = "http://scvalsrl.cafe24.com/BCDetail.php";
    private Map<String, String> parameters;

    public BCUpdateRequest(int no, Response.Listener<String> listener){

        super(Method.POST,URL , listener , null);

        Log.d("  업데이트 리퀘스트 들어옴 : "+ no," ");
        parameters = new HashMap<>();
        parameters.put("no", no + "");


    }


    @Override
    public Map<String , String> getParams(){
        Log.d("  겟파람 : ","1");
        return parameters;
    }

}
