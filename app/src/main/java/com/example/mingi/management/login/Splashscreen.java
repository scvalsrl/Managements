package com.example.mingi.management.login;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.example.mingi.management.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Splashscreen extends Activity implements LocationListener{

    // for GPS
    LocationManager locationManager;
    double nowLat = -1;
    double nowLon = -1;
    boolean isChange = false;
    int cn = 0; // debug

    public static String defaultUrl = "https://api2.sktelecom.com/tmap/geo/reversegeocoding?version=1&appKey=03772af9-f665-47d1-9008-207ca403d775&lat=";
    String nowName = null;
    String urlStr = null;
    Handler handler = new Handler();


    public void onAttachedToWindow(){
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }


    Thread splashTread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        StartAnimations();
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l = (LinearLayout)findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.splash);
        iv.clearAnimation();
        iv.startAnimation(anim);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        getLocation();

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(Splashscreen.this,
                            LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    if(isChange) { // success
                        intent.putExtra("isGPSEnable", String.valueOf(0));
                    }
                    else { // not sucess
                        intent.putExtra("isGPSEnable", String.valueOf(1));
                        // Toast.makeText(this, "   ?꾩옱 ?꾩튂瑜?諛쏆븘?ㅼ? 紐삵뻽?듬땲??", Toast.LENGTH_SHORT);
                    }
                    intent.putExtra("nowLat", String.valueOf(nowLat));
                    intent.putExtra("nowLon", String.valueOf(nowLon));
                    intent.putExtra("nowName", nowName);

                    Log.d("김민기", nowLat + ", " + nowLon + "/ " + isChange + "/ name : " + nowName +"/  cn: " + cn);

                    startActivity(intent);
                    overridePendingTransition(0, 0);

                    Splashscreen.this.finish();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    Splashscreen.this.finish();
                }
            }
    };
        splashTread.start();
    }


    // dㅇㅇ
    @Override
    public void onLocationChanged(Location location) {
        isChange = true;
        nowLat = location.getLatitude();
        nowLon = location.getLongitude();
        Log.d("current", "lat: " + nowLat);
        urlStr = defaultUrl + nowLat +"&lon=" + nowLon;
        ConnectThread reverseGeo = new ConnectThread(urlStr);
        reverseGeo.start();
        try {
            locationManager.removeUpdates(this);
        } catch (SecurityException e) {
            Log.e("Sercurity", "SercurityException2");
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {  }

    @Override
    public void onProviderEnabled(String provider) {   }

    @Override
    public void onProviderDisabled(String provider) {   }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        } catch(SecurityException e) {
            Log.e("Security", "SecurtiyException3");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            locationManager.removeUpdates(this);
        } catch(SecurityException e) {
            Log.e("Security", "SecurtiyException4");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            locationManager.removeUpdates(this);
        } catch(SecurityException e) {
            Log.e("Security", "SecurtiyException5");
        }

    }

    void getLocation() {
        try {
            Log.d("getLocation", "GET LOCATION 들어왔옹");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            Log.e("Security", "SercurityException");
            e.printStackTrace();
        }
    }

    // reverseGeocording
    class ConnectThread extends Thread {

        public ConnectThread(String inStr) { urlStr = inStr; }
        public void run() {
            Log.d("김민기 run", "들어옴");
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
            nowName = jobj.optString("fullAddress");
            Log.d("김민기 findAddr (nowName)", nowName);
        } catch (JSONException e) {
            Log.d("김민기 findAddr", "***예외발생****");
            e.printStackTrace();
        }
    }

    }