package com.example.mingi.management;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by MINGI on 2018-05-14.
 */

class Task extends AsyncTask {
    private String clientKey = "03772af9-f665-47d1-9008-207ca403d775";
    private String str,receiveMsg;
    private String endX, endY, startX, startY;

    public Task(String destLon, String destLat, String startLon, String startLat) {
        this.endX = destLon;
        this.endY = destLat;
        this.startX = startLon;
        this.startY = startLat;

        Log.d("right parameter", "endX: "+endX+",endY: "+endY+",startX: " + startX +",startY: " + startY);

    }
    @Override
    protected Object doInBackground(Object[] objects) {
        URL url = null;
        try {

            Log.d("distance doInBackground", startX + ", " + startY + ", " + endX + ", " + endY);
            url = new URL("https://api2.sktelecom.com/tmap/routes?version=1&tollgateFareOption=16&startX=" + startX +
                    "&startY=" + startY + "&endX=" + endX + "&endY=" + endY + "&appKey=" + clientKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                receiveMsg = buffer.toString();
                Log.d("distance receiveMsg: ", receiveMsg);
                reader.close();
            } else {
                Log.d("distance 통신 결과", conn.getResponseCode() + "error");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSONdistanceParser(receiveMsg);
    }

    public int JSONdistanceParser(String jsonString) {
        int distance = -1;
        try {
            JSONArray features = new JSONObject(jsonString).getJSONArray("features");
            JSONObject jobj = features.getJSONObject(0);
            jobj = jobj.getJSONObject("properties");
            distance = jobj.optInt("totalDistance");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return distance;
    }

}
