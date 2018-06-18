
package com.example.mingi.management.DrivingJoin;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.mingi.management.BusinessCard.BCDetailActivity;
import com.example.mingi.management.BusinessCard.BCEditActivity;
import com.example.mingi.management.DrivingJoin.LocationAdapter;
import com.example.mingi.management.R;

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

public class DestListActivity extends AppCompatActivity {
    EditText search_text;
    Button search_btn;
    ImageView nowLoc_btn;
    ListView search_list;

    String lats[], lons[], upperaddrs[], midaddrs[], roadNames[], roadNos[], names[];
    String mLat = null;
    String mLng = null;
    String mName = null;
    String startname, finalCurLat, finalCurLong;

    public static String defaultUrl = "https://api2.sktelecom.com/tmap/pois?version=1&appKey=03772af9-f665-47d1-9008-207ca403d775&searchKeyword=";
    public static String currentUrl = "https://api2.sktelecom.com/tmap/geo/reversegeocoding?version=1&appKey=03772af9-f665-47d1-9008-207ca403d775&lat=";
    Handler handler = new Handler();
    int jsonResultsLength = 0;

    Intent destIntent;

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dest_list);
        final ActionBar abar = getSupportActionBar();;//line under the action bar
        View viewActionBar = getLayoutInflater().inflate(R.layout.title_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("도착지 검색");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setHomeButtonEnabled(true);


        search_text = (EditText) findViewById(R.id.search_text);
        search_btn = (Button) findViewById(R.id.search_btn);
        nowLoc_btn = (ImageView) findViewById(R.id.nowLoc_btn);
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        Intent outIntent = getIntent();
        destIntent = new Intent(getApplicationContext(), CarJoinActivity.class);

        final String startname = outIntent.getStringExtra("curAddr");
        final String finalCurLat = outIntent.getStringExtra("curLat");
        final String finalCurLong = outIntent.getStringExtra("curLon");

        checkPermissions();

        search_text.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    Log.d("ListActivity_onKey", "Press Enter");
                    searchLocation(finalCurLat, finalCurLong, startname);
                }
                Log.d("ListActivity_onKey", "not Press Enter");
                return false;
            }
        });

        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userStr = search_text.getText().toString();
                String urlStr = defaultUrl + userStr + "&centerLat=" + finalCurLat + "&centerLon=" + finalCurLong;
                ConnectThread thread = new ConnectThread(urlStr);
                thread.start();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        search_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String userStr = search_text.getText().toString();
                Log.d("도착지 리스트", "검색 버튼 누름" + userStr);
                String urlStr = defaultUrl + userStr + "&centerLat=" + finalCurLat + "&centerLon=" + finalCurLong;
                ConnectThread thread = new ConnectThread(urlStr);
                thread.start();
            }
        });


        nowLoc_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(startname != null){
                    destIntent.putExtra("destname", startname);
                    destIntent.putExtra("destlat", finalCurLat);
                    destIntent.putExtra("destlon", finalCurLong);
                    setResult(1, destIntent);
                    finish();
                }
                else {
                    requestMyLocation();
                    if(Double.parseDouble(finalCurLat) >= 0 && Double.parseDouble(finalCurLong) >= 0) {
                        String fullCurrentUrl = currentUrl + finalCurLat + "&lon=" + finalCurLong;
                        CurrentThread currentThread = new CurrentThread(fullCurrentUrl);
                        currentThread.start();
                    } else {
                        Toast.makeText(getApplicationContext(), "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    private void checkPermissions() {
        //마시멜로 이상이면 권한 요청
        if(Build.VERSION.SDK_INT >= 23) {
            //권한 없는 경우
            if (ContextCompat.checkSelfPermission(DestListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(DestListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(DestListActivity.this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            // 권한 있는 경우
            else {
                Log.d("Permission", "마시멜로 이상 권한 있음");
                requestMyLocation();
            }
        }
        //마시멜로 아래
        else {
            Log.d("Permission", "마시멜로 이하 권한 있음");
            requestMyLocation();
        }
    }

    public void requestMyLocation() {
        if (ContextCompat.checkSelfPermission(DestListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(DestListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("requestMyLocation", "if문 안");
            return;
        }
        //요청
        Log.d("requestMyLocation", "요청 전");
        if(locationManager == null) {
            Log.d("locationManager" , "null");
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        //위치정보 구하기 리스너
    }

    LocationListener locationListener =   new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("onLocationChanged", "들어옴");
            if (ContextCompat.checkSelfPermission(DestListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(DestListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("onLocationChanged", "if안");
                return;
            }
            // 나의 위치를 한번만 가져오기 위해
            Log.d("requestMyLocation", "remove 전");
            locationManager.removeUpdates(locationListener);

            finalCurLat = Double.toString(location.getLatitude());
            finalCurLong = Double.toString(location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void searchLocation(String finalCurLat, String finalCurLong, String startname) {
        String userStr = search_text.getText().toString();
        String urlStr = defaultUrl + userStr + "&centerLat="+ finalCurLat + "&centerLon=" + finalCurLong;
        if(startname == null) {
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

            try {
                final String output = request(urlStr);
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if(output != null) {
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
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if(conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setRequestProperty("Accept-Charset", "UTF-8");

                    int resCode = conn.getResponseCode();

                    if (resCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = null;
                        while(true) {
                            line = reader.readLine();
                            if(line == null) {
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

            if(jsonResultsLength > 0) {
                names = new String[jsonResultsLength];
                lats = new String[jsonResultsLength];
                lons = new String[jsonResultsLength];
                upperaddrs = new String[jsonResultsLength];
                midaddrs = new String[jsonResultsLength];
                roadNames = new String[jsonResultsLength];
                roadNos = new String[jsonResultsLength];

                for(int i = 0; i < jsonResultsLength; i++) {
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

            if(jsonResultsLength > 0) {
                search_list = (ListView)findViewById(R.id.search_list);

                ArrayList<Listitem> datas = new ArrayList<Listitem>();
                for(int i = 0; i < jsonResultsLength; i++) {
                    datas.add(new Listitem(names[i], upperaddrs[i],
                            midaddrs[i], roadNames[i], roadNos[i],
                            lats[i],lons[i]));
                }
                final LocationAdapter adapter = new LocationAdapter(this, datas, getLayoutInflater());
                search_list.setAdapter(adapter);
                search_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mLat = lats[position];
                        mLng = lons[position];
                        mName = names[position];
                        destIntent.putExtra("destname",mName);
                        destIntent.putExtra("destlat",mLat);
                        destIntent.putExtra("destlon",mLng);
                        // Toast.makeText(getApplicationContext(), mLng, Toast.LENGTH_SHORT).show();
                        setResult(1,destIntent);
                        finish();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class CurrentThread extends Thread {
        String urlStr;
        public CurrentThread(String inStr) { urlStr = inStr; }
        public void run() {
            final String output = request(urlStr);
            try {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if(output != null) {
                            findAddr(output);
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
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if(conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setRequestProperty("Accept-Charset", "UTF-8");

                    int resCode = conn.getResponseCode();

                    Log.d("geo resCode", String.valueOf(resCode));

                    if(resCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = null;
                        while(true) {
                            line = reader.readLine();
                            if(line == null) {
                                break;
                            }
                            output.append(line + "\n");
                        }
                        Log.d("geo result", "HTTP_OK finish");
                        reader.close();
                        conn.disconnect();
                    }
                }
            } catch (MalformedURLException e) {
                Log.e("geo SampleHTTP", "Exception in processing response.", e);
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return output.toString();
        }
    }

    private void findAddr(String output) {
        Log.d("김민기 findAddr", "들어옴");

        try {
            JSONObject jobj = new JSONObject(output).getJSONObject("addressInfo");
            startname = jobj.optString("fullAddress");
            destIntent.putExtra("destname", startname);
            destIntent.putExtra("destlat", finalCurLat);
            destIntent.putExtra("destlon", finalCurLong);
            setResult(1, destIntent);
            finish();

        } catch (JSONException e) {
            Log.d("김민기 findAddr", "***예외발생****");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if( id == android.R.id.home){
            finish();

            return true;

        }


        return super.onOptionsItemSelected(item);
    }
}