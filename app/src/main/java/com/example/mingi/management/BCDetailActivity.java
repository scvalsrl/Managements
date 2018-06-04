package com.example.mingi.management;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BCDetailActivity extends FragmentActivity implements OnMapReadyCallback {
    ImageView imgView, emailBtn, callBtn, msgBtn;
    TextView bcname, bclevel, bccom, bcphone, bcemail, bcadd, bcno;
    Button bcedit, bcdelete;

    String bcname_str, bclevel_str, bccom_str, bcphone_str, bcemail_str, bcadd_str, bclat_str, bclon_str, bcphoto_str, no;
    String isGPSEnable, nowLat, nowLon, nowName ,userID;

    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bcdetail);


        Intent intent = getIntent();

        isGPSEnable = intent.getStringExtra("isGPSEnable");
        nowLat = intent.getStringExtra("nowLat");
        nowLon = intent.getStringExtra("nowLon");
        nowName = intent.getStringExtra("nowName");
        userID = intent.getStringExtra("userID");

        bcname_str = intent.getStringExtra("BC_name");
        bclevel_str = intent.getStringExtra("BC_level");
        bccom_str = intent.getStringExtra("BC_com");
        bcphone_str = intent.getStringExtra("BC_phone");
        bcemail_str = intent.getStringExtra("BC_mail");
        bcadd_str = intent.getStringExtra("BC_add");
        bclat_str = intent.getStringExtra("BC_lat");
        bclon_str = intent.getStringExtra("BC_lon");
        bcphoto_str = intent.getStringExtra("BC_photo");
        no = intent.getStringExtra("no");
        Log.d("김민기", "ㅁㄴㅇㅁㄴㅇ: " + no);

        initView();
        // SupportMapFragment 통해 레이아웃에 만든 fragment의 ID 참조하고 구글맵 호출
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mainFragment);
        mapFragment.getMapAsync(this); // getMapAstnc must be called on the main thread
        // getMapAsync가 onMapReady를 자동 호출
        btnClick();
        setTextView();
    }

    void initView() {
        bcname = (TextView) findViewById(R.id.bcname);
        bclevel = (TextView) findViewById(R.id.bclevel);
        bccom = (TextView) findViewById(R.id.bccom);
        bcphone = (TextView) findViewById(R.id.bcphone);
        bcemail = (TextView) findViewById(R.id.bcemail);
        bcadd = (TextView) findViewById(R.id.bcadd);
        bcno = (TextView) findViewById(R.id.bcno);

        bcedit = (Button) findViewById(R.id.bcedit);
        bcdelete = (Button) findViewById(R.id.bcdelete);


        new BCDetailActivity.DownloadImageTask((ImageView) findViewById(R.id.imgView))
                .execute("http://scvalsrl.cafe24.com/uploads/"+bcphoto_str);

        emailBtn = (ImageView) findViewById(R.id.emailbtn);
        callBtn = (ImageView) findViewById(R.id.callbtn);
        msgBtn = (ImageView) findViewById(R.id.msgbtn);
    }

    void setTextView() {

        bcno.setText(no);
        bcname.setText(bcname_str);
        bclevel.setText(bclevel_str);
        bccom.setText(bccom_str);
        bcphone.setText(bcphone_str);
        bcemail.setText(bcemail_str);
        bcadd.setText(bcadd_str);
    }


    void btnClick() {
        bcedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goEdit = new Intent(getApplicationContext(), BCEditActivity.class);

                goEdit.putExtra("bcname", bcname_str);
                goEdit.putExtra("bclevel", bclevel_str);
                goEdit.putExtra("bccom", bccom_str);
                goEdit.putExtra("bcphone", bcphone_str);
                goEdit.putExtra("bcemail", bcemail_str);
                goEdit.putExtra("bcadd", bcadd_str);
                goEdit.putExtra("bclat", bclat_str);
                goEdit.putExtra("bclon", bclon_str);
                goEdit.putExtra("bcphoto", bcphoto_str);
                goEdit.putExtra("no", no);

                goEdit.putExtra("userID", userID);
                goEdit.putExtra("nowLat", nowLat);
                goEdit.putExtra("nowLon", nowLon);
                goEdit.putExtra("isGPSEnable",isGPSEnable);
                goEdit.putExtra("nowName", nowName);

                startActivity(goEdit);

            }
        });



        bcdelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(BCDetailActivity.this);

                builder
                        .setMessage("명함을 삭제 합니다")
                        .setCancelable(false)
                        .setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {

                                        Response.Listener<String> responseListener = new Response.Listener<String>() {


                                            @Override
                                            public void onResponse(String response) {
                                                try{

                                                    JSONObject jsonResponse = new JSONObject(response);
                                                    boolean success = jsonResponse.getBoolean("success");
                                                    if(success){
                                                        new BCDetailActivity.BackgroundTask2().execute();
                                                    }else{
                                                        Log.d("  삭제실패 : ", "1");
                                                    }
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }

                                            }

                                        };


                                        int No_i  = Integer.parseInt(no);

                                        DeleteRequest2 deleteRequest = new DeleteRequest2(No_i, responseListener);
                                        RequestQueue queue = Volley.newRequestQueue(BCDetailActivity.this);
                                        queue.add(deleteRequest);



                                    }
                                })
                        .setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        // 다이얼로그를 취소한다
                                        dialog.cancel();
                                    }
                                }).create()
                        .show();



            }
        });





        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tel = "tel:" + bcphone_str;
                Intent goCall = new Intent(Intent.ACTION_CALL, Uri.parse(tel));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BCDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 101);

                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Log.d("BCDetailActivity", "no call Permission");
                    return;
                }
                startActivity(goCall);
            }
        });

        msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri smsUri = Uri.parse("sms:" + bcphone_str);
                Intent goSMS = new Intent(Intent.ACTION_SENDTO, smsUri);
                startActivity(goSMS);
            }
        });

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goEmail = new Intent(Intent.ACTION_SEND);
                goEmail.putExtra(Intent.EXTRA_EMAIL, new String[] {bcemail_str});
                goEmail.setType("text/html");
                goEmail.setPackage("com.google.android.gm");

                if(goEmail.resolveActivity(getPackageManager()) != null) {
                    startActivity(goEmail);
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng location= new LatLng(Double.valueOf(bclat_str), Double.valueOf(bclon_str));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location).title("회사이름을 넣으셈");

        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    class BackgroundTask2 extends AsyncTask<Void, Void, String> {

        String target;

        @Override
        protected void onPreExecute() {
            target = "http://scvalsrl.cafe24.com/BCList.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();

            } catch (Exception e) {

                e.printStackTrace();

            }


            return null;
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        public void onPostExecute(String result) {

            Intent intent = new Intent(BCDetailActivity.this, BCListActivity.class);
            intent.putExtra("userList",result);
            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon", nowLon);
            intent.putExtra("isGPSEnable", isGPSEnable);
            intent.putExtra("nowName", nowName);
            intent.putExtra("userID", userID);
            BCDetailActivity.this.startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        }

    }




}
