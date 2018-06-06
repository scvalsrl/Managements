package com.example.mingi.management;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

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

public class BCEditActivity extends AppCompatActivity {


    ImageView imageView;
    EditText bcname, bclevel, bccom, bcphone, bcemail;
    TextView bcadd;
    Button bccamera, bcupload;

    String bcname_str, bclevel_str, bccom_str, bcphone_str, bcemail_str, bcadd_str;
    String bclat, bclon, userID, no, bcphoto_str, bclat_str, bclon_str;
    String isGPSEnable, nowLat, nowLon, nowName;

    //for camera
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA}; // permission set

    private static final int MULTIPLE_PERMISSIONS = 101; // permission agree -> use callback func
    private String mCurrentPhotoPath;
    Uri photoUri;
    String uploadFileName = ""; //전송하고자하는 파일 이름
    String temp = "";
    String check = "";

    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    String upLoadServerUri = "http://scvalsrl.cafe24.com/UploadToServer.php";
    final String uploadFilePath = "storage/emulated/0/test/";//경로를 모르겠으면, 갤러리 어플리케이션 가서 메뉴->상세 정보

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bcedit);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("수정화면");

        getDataFromDetail();
        initView();
        checkPermissions();

        setInitView();
        uploadFileName = bcphoto_str;

        setClickBtn();
    }

    void initView() {
        bcname = (EditText) findViewById(R.id.bcname);
        bclevel = (EditText) findViewById(R.id.bclevel);
        bccom = (EditText) findViewById(R.id.bccom);
        bcphone = (EditText) findViewById(R.id.bcphone);
        bcemail = (EditText) findViewById(R.id.bcemail);
        bcadd = (TextView) findViewById(R.id.bcadd);

        bcupload = (Button) findViewById(R.id.uploadBtn);
        bccamera = (Button) findViewById(R.id.cameraBtn);

        imageView = (ImageView) findViewById(R.id.imgView);
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

    private void pickFromAlbum() {
        Intent takePicture = new Intent(Intent.ACTION_PICK);
        takePicture.setType(MediaStore.Images.Media.CONTENT_TYPE);
        takePicture.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(takePicture, PICK_FROM_ALBUM);
    }

    void getDataFromDetail() {
        Intent intent = getIntent();
        isGPSEnable = intent.getStringExtra("isGPSEnable");
        nowLat = intent.getStringExtra("nowLat");
        nowLon = intent.getStringExtra("nowLon");
        nowName = intent.getStringExtra("nowName");
        userID = intent.getStringExtra("userID");
        Log.d("ㅇㅇdd", "userID: " + userID);
        bcname_str = intent.getStringExtra("bcname");
        bclevel_str = intent.getStringExtra("bclevel");
        bccom_str = intent.getStringExtra("bccom");
        bcphone_str = intent.getStringExtra("bcphone");
        bcemail_str = intent.getStringExtra("bcemail");
        bcadd_str = intent.getStringExtra("bcadd");
        bclat_str = intent.getStringExtra("bclat");
        bclon_str = intent.getStringExtra("bclon");
        bcphoto_str = intent.getStringExtra("bcphoto");
        temp = bcphoto_str;
        no = intent.getStringExtra("no");
    }

    void setInitView() {
        bcname.setText(bcname_str);
        bclevel.setText(bclevel_str);
        bccom.setText(bccom_str);
        bcphone.setText(bcphone_str);
        bcemail.setText(bcemail_str);
        bcadd.setText(bcadd_str);

        new BCEditActivity.DownloadImageTask((ImageView) findViewById(R.id.imgView))
                .execute("http://scvalsrl.cafe24.com/uploads/" + bcphoto_str);

    }

    void setClickBtn() {
        bccamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        bcupload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                pickFromAlbum();
            }
        });


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

    private void takePhoto() {
        Intent goCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(this,
                    "com.example.mingi.management.provider", photoFile);
            goCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
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
        Log.d("createImageFile 김민기2  ", uploadFileName);
        return image;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(getBaseContext(), "resultCode: " + resultCode, Toast.LENGTH_SHORT).show();

        if (requestCode == PICK_FROM_ALBUM) {
            if (data == null) {
                Log.d("onActivityResult", "PICK_FROM_ALBUM but data is NULL");
                return;
            }

            if (resultCode == Activity.RESULT_OK) {
                /*
                try {
                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    imageView.setImageBitmap(image_bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                photoUri = data.getData();
                cropImage();
            }
        } else if (requestCode == PICK_FROM_CAMERA) {
            if (data == null) {
                Log.d("onActivityResult", "PICK_FROM_CAMERA but data is NULL");
                return;
            } else {
                cropImage();
                // for showing photo on album
                MediaScannerConnection.scanFile(this, new String[]{photoUri.getPath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String s, Uri uri) {

                            }
                        });
            }
        } else if (requestCode == CROP_FROM_CAMERA) {
            if (data == null) {
                Log.d("onActivityResult", "CROP_FROM_CAMERA but data is NULL");
                return;
            } else {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 1000, 500);
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();

                    thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, bs);
                    imageView.setImageBitmap(thumbImage);
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

                            Toast.makeText(BCEditActivity.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {


                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {

                    public void run() {
                        Toast.makeText(BCEditActivity.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }

                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);

            } catch (Exception e) {
                dialog.dismiss();

                e.printStackTrace();

                runOnUiThread(new Runnable() {

                    public void run() {
                        Toast.makeText(BCEditActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }

                });
            }
            dialog.dismiss();

            return serverResponseCode;

        } // End else block


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

            Intent intent = new Intent(BCEditActivity.this, BCListActivity.class);
            intent.putExtra("userList", result);
            intent.putExtra("nowLat", nowLat);
            intent.putExtra("nowLon", nowLon);
            intent.putExtra("isGPSEnable", isGPSEnable);
            intent.putExtra("nowName", nowName);
            intent.putExtra("userID", userID);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            BCEditActivity.this.startActivity(intent);
            finish();
            overridePendingTransition(0, 0);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.bcupdate_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if( id == android.R.id.home){
            finish();
            return true;
        }

        if( id == R.id.vbcupdate2) {


            if (bcname.getText().toString().equals("")) {

                AlertDialog.Builder builder = new AlertDialog.Builder(BCEditActivity.this);
                builder.setMessage(" 이름을 입력해주세요 ")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();

            } else if (bclevel.getText().toString().equals("")) {

                AlertDialog.Builder builder = new AlertDialog.Builder(BCEditActivity.this);
                builder.setMessage(" 직급을 입력해주세요 ")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();

            } else if (bccom.getText().toString().equals("")) {

                AlertDialog.Builder builder = new AlertDialog.Builder(BCEditActivity.this);
                builder.setMessage(" 회사명을 입력해주세요 ")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();

            } else if (bcphone.getText().toString().equals("")) {

                AlertDialog.Builder builder = new AlertDialog.Builder(BCEditActivity.this);
                builder.setMessage(" 휴대폰을 입력해주세요 ")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();

            } else if (uploadFileName.equals("") || uploadFileName.equals(null)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(BCEditActivity.this);
                builder.setMessage(" 사진을 등록해주세요 ")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();

            } else {


                dialog = ProgressDialog.show(BCEditActivity.this, "", "등록 중입니다", true);

                if (!uploadFileName.equals(temp)) {
                    check = temp;
                    new Thread(new Runnable() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {

                                }
                            });
                            uploadFile(uploadFilePath + "" + uploadFileName);

                        }
                    }).start();
                }


                bcname_str = bcname.getText().toString();
                bclevel_str = bclevel.getText().toString();
                bccom_str = bccom.getText().toString();
                bcphone_str = bcphone.getText().toString();
                bcemail_str = bcemail.getText().toString();
                bcadd_str = bcadd.getText().toString();

                if(bcemail_str.equals("") || bcemail_str.equals(null)){
                    bcemail_str = "";
                }
                if(bcadd_str.equals("") || bcadd_str.equals(null)){
                    bcadd_str = "";
                    bclat_str = "0";
                    bclon_str = "0";
                }

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

                                                AlertDialog.Builder builder = new AlertDialog.Builder(BCEditActivity.this);

                                                builder.setMessage("성공적으로 수정 되었습니다")
                                                        .setCancelable(false)
                                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                            public void onClick(
                                                                    DialogInterface dialog, int id) {
                                                                // 프로그램을 종료한다
                                                                new BCEditActivity.BackgroundTask2().execute();

                                                            }
                                                        }).
                                                        create()
                                                        .show();

                                            } else {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(BCEditActivity.this);
                                                builder.setMessage("등록에 실패 했습니다.")
                                                        .setNegativeButton("다시시도", null).create().show();
                                                Intent intent = new Intent(BCEditActivity.this, BCEditActivity.class);
                                                BCEditActivity.this.startActivity(intent);
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };


                                int update_no = Integer.parseInt(jsonResponse.getString("no"));
                                update_no++;

                                BCUpdateRequest bcUpdateRequest = new BCUpdateRequest(userID, bcname_str, bclevel_str, bccom_str, bcphone_str, bcemail_str, bcadd_str, bclat_str,
                                        bclon_str, uploadFileName, update_no, Integer.parseInt(no), check, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(BCEditActivity.this);
                                queue.add(bcUpdateRequest);


                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                };

                BCCountRequest bcCountRequest = new BCCountRequest(responseListener2);
                RequestQueue queue2 = Volley.newRequestQueue(BCEditActivity.this);
                queue2.add(bcCountRequest);

                return true;

            }
        }

        return super.onOptionsItemSelected(item);
    }

}