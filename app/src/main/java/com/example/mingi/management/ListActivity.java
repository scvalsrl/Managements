package com.example.mingi.management;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    EditText search_text;
    Button search_btn;
    ImageView nowLoc_btn;
    ListView search_list;

    String lats[], lons[], upperaddrs[], midaddrs[], roadNames[], roadNos[], names[];
    String mLat = null;
    String mLng = null;
    String mName = null;

    public static String defaultUrl = "https://api2.sktelecom.com/tmap/pois?version=1&appKey=03772af9-f665-47d1-9008-207ca403d775&searchKeyword=";
    Handler handler = new Handler();
    int jsonResultsLength = 0;

    Intent destIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        search_text = (EditText) findViewById(R.id.search_text);
        search_btn = (Button) findViewById(R.id.search_btn);
        nowLoc_btn = (ImageView) findViewById(R.id.nowLoc_btn);

        Intent outIntent = getIntent();
        destIntent = new Intent(getApplicationContext(), CarJoinActivity.class);

        final String startname = outIntent.getStringExtra("curAddr");
        final String finalCurLat = outIntent.getStringExtra("curLat");
        final String finalCurLong = outIntent.getStringExtra("curLon");

        search_text.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    Log.d("ListActivity_onKey", "Press Enter");
                    searchLocation(finalCurLat, finalCurLong, startname);
                }
                Log.d("ListActivity_onKey", "not Press Enter");
                return false;
            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchLocation(finalCurLat, finalCurLong, startname);
            }
        });


        nowLoc_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (startname != null) {
                    destIntent.putExtra("startname", startname);
                    destIntent.putExtra("startlat", finalCurLat);
                    destIntent.putExtra("startlon", finalCurLong);
                    setResult(0, destIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void searchLocation(String finalCurLat, String finalCurLong, String startname) {
        String userStr = search_text.getText().toString();
        String urlStr = defaultUrl + userStr + "&centerLat=" + finalCurLat + "&centerLon=" + finalCurLong;
        if (startname == null) {
            urlStr = defaultUrl + userStr + "&centerLat=35.1473&centerLon=129.0673";
        }
        ConnectThread thread = new ConnectThread(urlStr);
        thread.start();
    }

    class ConnectThread extends Thread {
        String urlStr;

        public ConnectThread(String inStr) {
            urlStr = inStr;
        }

        public void run() {
            Log.d("시작", "connect thread dest start");
            try {
                final String output = request(urlStr);
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (output != null) {
                            findInfo(output);
                        }

                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private String request(String urlStr) {
            StringBuilder output = new StringBuilder();
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setRequestProperty("Accept-Charset", "UTF-8");

                    int resCode = conn.getResponseCode();

                    Log.d("resCode", String.valueOf(resCode));

                    if (resCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = null;
                        while (true) {
                            line = reader.readLine();
                            if (line == null) {
                                break;
                            }
                            output.append(line + "\n");
                        }
                        Log.d("result", "HTTP_OK finish");
                        reader.close();
                        conn.disconnect();
                    }
                }
            } catch (MalformedURLException e) {
                Log.e("SampleHTTP", "Exception in processing response. ", e);
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return output.toString();
        }
    }

    private void findInfo(String output) {
        Log.d("output", output);
        try {
            JSONObject jobj = new JSONObject(output).getJSONObject("searchPoiInfo");
            jobj = jobj.getJSONObject("pois");
            JSONArray jarray = jobj.getJSONArray("poi");
            jsonResultsLength = jarray.length();

            if (jsonResultsLength > 0) {
                names = new String[jsonResultsLength];
                lats = new String[jsonResultsLength];
                lons = new String[jsonResultsLength];
                upperaddrs = new String[jsonResultsLength];
                midaddrs = new String[jsonResultsLength];
                roadNames = new String[jsonResultsLength];
                roadNos = new String[jsonResultsLength];

                for (int i = 0; i < jsonResultsLength; i++) {
                    JSONObject jObject = jarray.getJSONObject(i);
                    names[i] = jObject.optString("name");
                    lats[i] = jObject.optString("frontLat");
                    lons[i] = jObject.optString("frontLon");
                    upperaddrs[i] = jObject.optString("upperAddrName");
                    midaddrs[i] = jObject.optString("middleAddrName");
                    roadNames[i] = jObject.optString("roadName");
                    roadNos[i] = jObject.optString("firstBuildNo");
                }
            }

            if (jsonResultsLength > 0) {
                search_list = (ListView) findViewById(R.id.search_list);

                ArrayList<Listitem> datas = new ArrayList<Listitem>();
                for (int i = 0; i < jsonResultsLength; i++) {
                    datas.add(new Listitem(names[i], upperaddrs[i],
                            midaddrs[i], roadNames[i], roadNos[i],
                            lats[i], lons[i]));
                }
                final LocationAdapter adapter = new LocationAdapter(this, datas, getLayoutInflater());
                search_list.setAdapter(adapter);
                search_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mLat = lats[position];
                        mLng = lons[position];
                        mName = names[position];
                        destIntent.putExtra("startname", mName);
                        destIntent.putExtra("startlat", mLat);
                        destIntent.putExtra("startlon", mLng);
                        // Toast.makeText(getApplicationContext(), mLng, Toast.LENGTH_SHORT).show();
                        setResult(0, destIntent);
                        finish();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}