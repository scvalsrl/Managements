package com.example.mingi.management.BusinessCard;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.mingi.management.R;
import com.example.mingi.management.DrivingJoin.SearchAddrActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BCJoinActivity extends AppCompatActivity {
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    boolean preventButtonTouch = false;
    static Uri photoUri;
   private  AlertDialog.Builder builder;
    ImageView imageView, uploadBtn, camBtn;
    EditText bcname, bclevel, bccom, bcphone, bcemail, bcadd;
    TextView camTxt, uploadTxt;
    String bcadd_str, bclat, bclon;
    int no_i;
    String isGPSEnable;
    String nowLat;
    String nowLon;
    String nowName;
    String userID;

    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA}; // permission set

    private static final int MULTIPLE_PERMISSIONS = 101; // permission agree -> use callback func
    private String mCurrentPhotoPath;


    Button uploadButton;
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    String upLoadServerUri = null;

    final String uploadFilePath = "storage/emulated/0/test/";//경로를 모르겠으면, 갤러리 어플리케이션 가서 메뉴->상세 정보
    String uploadFileName = ""; //전송하고자하는 파일 이름

    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bcjoin);
        builder = new AlertDialog.Builder(BCJoinActivity.this);
        checkPermissions();
        initView();



        final ActionBar abar = getSupportActionBar();;//line under the action bar
        View viewActionBar = getLayoutInflater().inflate(R.layout.title_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("명함등록");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setHomeButtonEnabled(true);

        Intent intent = getIntent();
        isGPSEnable = intent.getStringExtra("isGPSEnable");
        nowLat = intent.getStringExtra("nowLat");
        nowLon = intent.getStringExtra("nowLon");
        nowName = intent.getStringExtra("nowName");
        userID = intent.getStringExtra("userID");


        uploadButton = (Button) findViewById(R.id.uploadButton);

        upLoadServerUri = "http://scvalsrl.cafe24.com/UploadToServer.php";//서버컴퓨터의 ip주소

        setClick();


    }

    private void setClick() {
        bcadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(bcadd);
                Intent goSearch = new Intent(BCJoinActivity.this, SearchAddrActivity.class);
                goSearch.putExtra("nowLat", nowLat);
                goSearch.putExtra("nowLon", nowLon);
                startActivityForResult(goSearch, 3);
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromAlbum();
            }
        });

        camBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        camTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        uploadTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                pickFromAlbum();
            }
        });
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

            Intent intent = new Intent(BCJoinActivity.this, BCListActivity.class);
            intent.putExtra("userList", result);
            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon", nowLon);
            intent.putExtra("isGPSEnable", isGPSEnable);
            intent.putExtra("nowName", nowName);
            intent.putExtra("userID", userID);

            finish();
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            BCJoinActivity.this.startActivity(intent);

            overridePendingTransition(0, 0);

        }

    }


    public void onClick(View v) {

        startActivity(new Intent(this, uploadActivity.class));
        finish();

    }


    public int uploadFile(String sourceFileUri) {

        String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    + uploadFilePath + "" + uploadFileName);
            runOnUiThread(new Runnable() {

                public void run() {


                }

            });


            return 0;
        } else {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL

                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//dfdfdf
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }


                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    + uploadFileName;

                            Toast.makeText(BCJoinActivity.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                ex.printStackTrace();

                runOnUiThread(new Runnable() {

                    public void run() {
                        Toast.makeText(BCJoinActivity.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }

                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);

            } catch (Exception e) {

                e.printStackTrace();

                runOnUiThread(new Runnable() {

                    public void run() {
                        Toast.makeText(BCJoinActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }

                });
            }

            return serverResponseCode;

        } // End else block


    }


    public void initView() {
        uploadBtn = (ImageView) findViewById(R.id.uploadBtn);
        camBtn = (ImageView) findViewById(R.id.cameraBtn);
        imageView = (ImageView) findViewById(R.id.imgView);
        bcname = (EditText) findViewById(R.id.bcname);
        bclevel = (EditText) findViewById(R.id.bclevel);
        bccom = (EditText) findViewById(R.id.bccom);
        bcphone = (EditText) findViewById(R.id.bcphone);
        bcemail = (EditText) findViewById(R.id.bcemail);
        bcadd = (EditText) findViewById(R.id.bcadd);
        bcphone = (EditText) findViewById(R.id.bcphone);
        bcphone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        uploadTxt = (TextView) findViewById(R.id.uploadTxt);
        camTxt = (TextView) findViewById(R.id.cameraTxt);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        bcadd.setClickable(false);
        bcadd.setFocusable(false);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                permissions[0])
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), permissions[1])
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), permissions[2])
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, permissions, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                return;
            }
        }
    }

    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "권한 요청에 동의해주셔야 이용가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void hideKeyboard(EditText editText) {
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void pickFromAlbum() {
        Intent takePicture = new Intent(Intent.ACTION_PICK);
        takePicture.setType(MediaStore.Images.Media.CONTENT_TYPE);
        takePicture.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(takePicture, PICK_FROM_ALBUM);
    }

    private Uri getLastCaptureImageUri() {
        Uri uri = null;
        String [] IMAGE_PROJECTION = {
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns._ID,
        };
        try {
            Cursor cursorImages = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_PROJECTION, null, null, null);
            if(cursorImages != null && cursorImages.moveToLast()) {
                uri = Uri.parse(cursorImages.getString(0)); // 경로
                int id = cursorImages.getInt(1); // id
                cursorImages.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return uri;
    }

    private void takePhoto() {
        Intent goCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
            Log.d("photo", "photoFile createImageFile");

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (photoFile != null) {
           photoUri = FileProvider.getUriForFile(BCJoinActivity.this,
                    "com.example.mingi.management.provider", photoFile);
            //photoUri = getLastCaptureImageUri();
            Log.d("photo", "goCamera전");
            if(photoUri == null) {
                Log.d("photo", "Uri null");
            }
            goCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            Log.d("photo", "goCamera후");;
            startActivityForResult(goCamera, PICK_FROM_CAMERA);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "IP" + timeStamp + "_";

        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/test/");
        if (!storageDir.exists()) {
            Log.d("createImageFile ", "directory not exist");
            storageDir.mkdirs();
            Log.d("createImageFile ", "mkdir complete");
        }


        File image = File.createTempFile(imageFileName, ".jpg", storageDir);


        Log.d("createImageFile", "complete creating image file");
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        Log.d("createImageFile ", mCurrentPhotoPath);
        uploadFileName = mCurrentPhotoPath.substring(30);
        Log.d("createImageFile 김민기  ", mCurrentPhotoPath.substring(30));
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_ALBUM) {
            if (data == null) {
                Log.d("onActivityResult", "PICK_FROM_ALBUM but data is NULL");
                return;
            }
            Log.d("onActivityResult", "PICK_FROM_ALBUM but data is not NULL");
            if (resultCode == Activity.RESULT_OK) {
                Log.d("onActivityResult", "PICK_FROM_ALBUM RESULT_OK");
                photoUri = data.getData();
                Log.d("onActivityResult", "PICK_FROM_ALBUM getData 이후");
                cropImage();
                Log.d("onActivityResult", "PICK_FROM_ALBUM cropImage이후");
            }

        } else if (requestCode == PICK_FROM_CAMERA) {
            if(resultCode == Activity.RESULT_OK) {
                Log.d("onActivityResult", "Camera RESULT_OK");
                cropImage();
                Log.d("onActivityResult", "PICK_FROM_CAMERA cropImage 이후");
                // for showing photo on album
                MediaScannerConnection.scanFile(this, new String[]{photoUri.getPath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String s, Uri uri) {

                            }
                        });
            } else {
                Log.d("onActivityResult", "Camera Canceled");
                return;
            }
        } else if (requestCode == CROP_FROM_CAMERA) {
            if (data == null) {
                Log.d("onActivityResult", "CROP_FROM_CAMERA but data is NULL");
                return;
            } else {
                try {
                    Log.d("onActivityResult", "CROP_FROM_CAMERA but data is not NULL");
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    Log.d("onActivityResult", "CROP_FROM_CAMERA 1");
                    Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 1000, 500);
                    Log.d("onActivityResult", "CROP_FROM_CAMERA 2");
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    Log.d("onActivityResult", "CROP_FROM_CAMERA 3");

                    thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, bs);
                    Log.d("onActivityResult", "CROP_FROM_CAMERA 4");
                    imageView.setImageBitmap(thumbImage);
                    Log.d("onActivityResult", "CROP_FROM_CAMERA 5");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ERROR", e.getMessage().toString());
                }
            }
        }

        if (resultCode == 3) { // search company address
            if (requestCode == 3) {
                if (requestCode == 3 && data != null) {
                    bcadd_str = data.getStringExtra("destname");
                    bclat = data.getStringExtra("destlat");
                    bclon = data.getStringExtra("destlon");
                    bcadd.setText(bcadd_str);
                }
            }
        }
    }


    public void cropImage() {
        this.grantUriPermission("com.android.camera", photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        grantUriPermission(list.get(0).activityInfo.packageName, photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(getApplicationContext(), "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(getApplicationContext(), "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra("crop", "true");
            // 높이 200dp, 가로 350dp
            intent.putExtra("aspectX", 7);
            intent.putExtra("aspectY", 4);
            intent.putExtra("scale", true);
            File croppedFileName = null;
            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File folder = new File(Environment.getExternalStorageDirectory() + "/test/");
            File tempFile = new File(folder.toString(), croppedFileName.getName());

            photoUri = FileProvider.getUriForFile(this, "com.example.mingi.management.provider", tempFile);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); // to recieve Bitmap format
            Log.d("cropImage", "outputFormat: " + Bitmap.CompressFormat.JPEG.toString());
            // revise

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            grantUriPermission(res.activityInfo.packageName, photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, CROP_FROM_CAMERA);
        }
    }


    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.bcjoin_menu, menu);

        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if( id == android.R.id.home){

            finish();
            return true;

        }

        if( id == R.id.vbcjoin ) {

            if (preventButtonTouch == true){
                return true;
            }

            preventButtonTouch = true;
            if (bcname.getText().toString().equals("")) {

                builder.setMessage(" 이름을 입력해주세요 ")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();

            }
            else  if (bclevel.getText().toString().equals("")) {

                builder.setMessage(" 직급을 입력해주세요 ")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();

            }
            else  if (bccom.getText().toString().equals("")) {

                builder.setMessage(" 회사명을 입력해주세요 ")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();

            }

            else  if (bcphone.getText().toString().equals("")) {


                builder.setMessage(" 휴대폰을 입력해주세요 ")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();
            }

            else  if (uploadFileName.equals("")  || uploadFileName.equals(null)  ) {

                builder.setMessage(" 사진을 등록해주세요 ")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();

            }

            else {

                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                            }
                        });
                        uploadFile(uploadFilePath + "" + uploadFileName);
                    }
                }).start();

                Response.Listener<String> responseListener2 = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // 제이슨 생성
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {  // 성공
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonResponse = new JSONObject(response);
                                            boolean success = jsonResponse.getBoolean("success");

                                            if (success) {

                                                builder.setMessage("성공적으로 등록 되었습니다")
                                                        .setPositiveButton("확인", null)
                                                        .create()
                                                        .show();


                                                Response.Listener<String> responseListener = new Response.Listener<String>() {


                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {


                                                            JSONObject jsonResponse = new JSONObject(response);
                                                            boolean success = jsonResponse.getBoolean("success");

                                                            String BC_name, BC_level, BC_com, BC_phone, BC_mail, BC_add, BC_lat, BC_lon, BC_photo;
                                                            String no;
                                                            BC_name = jsonResponse.getString("BC_name");
                                                            BC_level = jsonResponse.getString("BC_level");
                                                            BC_com = jsonResponse.getString("BC_com");
                                                            BC_phone = jsonResponse.getString("BC_phone");
                                                            BC_mail = jsonResponse.getString("BC_mail");
                                                            BC_add = jsonResponse.getString("BC_add");
                                                            BC_lat = jsonResponse.getString("BC_lat");
                                                            BC_lon = jsonResponse.getString("BC_lon");
                                                            BC_photo = jsonResponse.getString("BC_photo");
                                                            no = jsonResponse.getString("no");


                                                            if (success) {

                                                                // 주소가 없다면
                                                                if (BC_lat.equals("0") || BC_lon.equals("0") || BC_add.equals("")) {
                                                                    Log.d("BCListActivity-check","lat: " + BC_lat + ", lon: " + BC_lon);
                                                                    BC_lat = "0";
                                                                    BC_lon = "0";
                                                                    BC_add = "";
                                                                    // 인텐드에 넣기
                                                                    Intent intent = new Intent(BCJoinActivity.this, BCDetailNoAddActivity.class);

                                                                    intent.putExtra("BC_name", BC_name);
                                                                    intent.putExtra("BC_level", BC_level);
                                                                    intent.putExtra("BC_com", BC_com);
                                                                    intent.putExtra("BC_phone", BC_phone);
                                                                    intent.putExtra("BC_mail", BC_mail);
                                                                    intent.putExtra("BC_add", BC_add);
                                                                    intent.putExtra("BC_lat", BC_lat);
                                                                    intent.putExtra("BC_lon", BC_lon);
                                                                    intent.putExtra("BC_photo", BC_photo);
                                                                    intent.putExtra("no", no);


                                                                    intent.putExtra("userID", userID);
                                                                    intent.putExtra("isGPSEnable", isGPSEnable);
                                                                    intent.putExtra("nowLat", nowLat);
                                                                    intent.putExtra("nowLon", nowLon);
                                                                    intent.putExtra("nowName", nowName);

                                                                    finish();
                                                                    intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                    preventButtonTouch = false;
                                                                    BCJoinActivity.this.startActivity(intent);



                                                                    // 화면전환 넣기 //

                                                                } else{

                                                                    // 인텐드에 넣기
                                                                    Intent intent = new Intent(BCJoinActivity.this, BCDetailActivity.class);

                                                                    intent.putExtra("BC_name", BC_name);
                                                                    intent.putExtra("BC_level", BC_level);
                                                                    intent.putExtra("BC_com", BC_com);
                                                                    intent.putExtra("BC_phone", BC_phone);
                                                                    intent.putExtra("BC_mail", BC_mail);
                                                                    intent.putExtra("BC_add", BC_add);
                                                                    intent.putExtra("BC_lat", BC_lat);
                                                                    intent.putExtra("BC_lon", BC_lon);
                                                                    intent.putExtra("BC_photo", BC_photo);
                                                                    intent.putExtra("no", no);


                                                                    intent.putExtra("userID", userID);
                                                                    intent.putExtra("isGPSEnable", isGPSEnable);
                                                                    intent.putExtra("nowLat", nowLat);
                                                                    intent.putExtra("nowLon", nowLon);
                                                                    intent.putExtra("nowName", nowName);

                                                                    finish();
                                                                    intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                    preventButtonTouch = false;
                                                                    BCJoinActivity.this.startActivity(intent);
                                                                }

                                                            } else {

                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                    }

                                                };

                                                BCDetailRequest bcDetailRequest = new BCDetailRequest(no_i, responseListener);
                                                RequestQueue queue = Volley.newRequestQueue(BCJoinActivity.this);
                                                queue.add(bcDetailRequest);



                                            } else {
                                                preventButtonTouch = false;
                                                builder.setMessage("등록에 실패 했습니다.")
                                                        .setNegativeButton("다시시도", null).create().show();
                                                Intent intent = new Intent(BCJoinActivity.this, BCJoinActivity.class);
                                                BCJoinActivity.this.startActivity(intent);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }


                                };
                                String no_s = jsonResponse.getString("no");

                                no_i = Integer.parseInt(no_s);
                                no_i++;

                                String id = userID;
                                String bc_name = bcname.getText().toString();

                                String bc_level = bclevel.getText().toString();
                                String bc_com = bccom.getText().toString();
                                String bc_phone = bcphone.getText().toString();
                                String bc_mail = bcemail.getText().toString();
                                String bc_add = bcadd.getText().toString();

                                if(bc_mail.equals("") || bc_mail.equals(null)){
                                    bc_mail = "";
                                }

                                if(bc_add.equals("") || bc_mail.equals(null)){
                                    bc_add = "";
                                    bclat = "0";
                                    bclon = "0";
                                }

                                BCJoinRequest bcJoinRequest = new BCJoinRequest(id, bc_name, bc_level, bc_com, bc_phone, bc_mail, bc_add, bclat, bclon, uploadFileName, no_i, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(BCJoinActivity.this);

                                queue.add(bcJoinRequest);


                            } else {


                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                };


                BCCountRequest bcCountRequest = new BCCountRequest(responseListener2);
                RequestQueue queue2 = Volley.newRequestQueue(BCJoinActivity.this);
                queue2.add(bcCountRequest);


                return true;
            }

        }
        return super.onOptionsItemSelected(item);


    }


}