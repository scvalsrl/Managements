package com.example.mingi.management;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BusinessCardMain extends AppCompatActivity {
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

    Uri photoUri;

    Button uploadBtn, camBtn , bcJoin, bccancel;
    ImageView imageView;
    TextView bcadd;
    EditText bcname , bclevel, bccom, bcphone, bcemail;
    String nowLat, nowLon, bcadd_str, bclat, bclon;

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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_card_main);
        checkPermissions();
        initView();

        uploadButton = (Button)findViewById(R.id.uploadButton);


        upLoadServerUri = "http://scvalsrl.cafe24.com/UploadToServer.php";//서버컴퓨터의 ip주소

        bcadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goSearch = new Intent(BusinessCardMain.this, SearchAddrActivity.class);
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

        bcJoin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog = ProgressDialog.show(BusinessCardMain.this, "", "등록 중입니다", true);

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
                        Log.d("  리퀘스트 응답 받음 : ", "1");
                        try{
                            // 제이슨 생성
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if(success){  // 성공
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        boolean success = jsonResponse.getBoolean("success");

                                        if (success) {

                                            AlertDialog.Builder builder = new AlertDialog.Builder(BusinessCardMain.this);

                                            builder.setMessage("성공적으로 등록 되었습니다")
                                                    .setPositiveButton("확인", null)
                                                    .create()
                                                    .show();


                                            finish();
                                        } else {

                                            AlertDialog.Builder builder = new AlertDialog.Builder(BusinessCardMain.this);
                                            builder.setMessage("등록에 실패 했습니다.")
                                                    .setNegativeButton("다시시도", null).create().show();
                                            Intent intent = new Intent(BusinessCardMain.this, BusinessCardMain.class);
                                            BusinessCardMain.this.startActivity(intent);

                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();

                                    }

                                }


                            };
                            String no_s = jsonResponse.getString("no");

                            int no_i = Integer.parseInt(no_s);
                            no_i++;

                            String id = "2109812";
                            String bc_name = bcname.getText().toString();
                            String bc_level = bclevel.getText().toString();
                            String bc_com = bccom.getText().toString();
                            String bc_phone = bcphone.getText().toString();
                            String bc_mail = bcemail.getText().toString();
                            String bc_add = bcadd.getText().toString();
                            String bc_lat = "100";
                            String bc_lon = "200";


                            BCJoinRequest bcJoinRequest = new BCJoinRequest(id, bc_name, bc_level, bc_com, bc_phone, bc_mail, bc_add, bc_lat, bc_lon, uploadFileName , no_i ,responseListener);
                            RequestQueue queue = Volley.newRequestQueue(BusinessCardMain.this);

                            queue.add(bcJoinRequest);


                            }else{

                                Log.d(" 카운팅 실패 : ", "1");

                            }


                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                };


                BCCountRequest bcCountRequest = new BCCountRequest(responseListener2);
                RequestQueue queue2 = Volley.newRequestQueue(BusinessCardMain.this);
                queue2.add(bcCountRequest);

            }
        });

        bccancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


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
                    +uploadFilePath + "" + uploadFileName);
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

                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    +uploadFileName;

                            Toast.makeText(BusinessCardMain.this, "File Upload Complete.",
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
                        Toast.makeText(BusinessCardMain.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }

                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);

            } catch (Exception e) {
                dialog.dismiss();

                e.printStackTrace();

                runOnUiThread(new Runnable() {

                    public void run() {
                        Toast.makeText(BusinessCardMain.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }

                });
            }
            dialog.dismiss();

            return serverResponseCode;
        } // End else block



    }


    public void initView() {
        uploadBtn = (Button) findViewById(R.id.uploadBtn);
        camBtn = (Button) findViewById(R.id.cameraBtn);
        bcJoin = (Button) findViewById(R.id.bcjoin);
        bccancel = (Button) findViewById(R.id.bccancel);

        imageView = (ImageView) findViewById(R.id.imgView);
        bcname = (EditText) findViewById(R.id.bcname);
        bclevel = (EditText) findViewById(R.id.bclevel);
        bccom = (EditText) findViewById(R.id.bccom);
        bcphone = (EditText) findViewById(R.id.bcphone);
        bcemail = (EditText) findViewById(R.id.bcemail);
        bcadd = (TextView) findViewById(R.id.bcadd);
        bcphone = (EditText) findViewById(R.id.bcphone);
        bcphone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                permissions[0])
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), permissions[1])
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), permissions[2])
                != PackageManager.PERMISSION_GRANTED)
        {
            Log.d("checkPerm", "requestPermissions");
            ActivityCompat.requestPermissions(this, permissions, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch(requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if(permissions[i].equals(this.permissions[0])) {
                            if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if(permissions[i].equals(this.permissions[1])) {
                            if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if(permissions[i].equals(this.permissions[2])) {
                            if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
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

    private void takePhoto() {
        Intent goCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
            Log.d("takePhoto", "createImageFIle done");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }

        if(photoFile != null) {
            photoUri = FileProvider.getUriForFile(this,
                    "com.example.mingi.management.provider", photoFile);
            goCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(goCamera, PICK_FROM_CAMERA);
        }
    }

    private File createImageFile() throws IOException{
        // Create an image file name
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "IP" + timeStamp + "_";
        Log.d("createImageFile 1111", "imgName: " + imageFileName);
        // save at test
        Log.d("createImageFile 22222 ", "sotrageDir: " + Environment.getExternalStorageDirectory().getAbsolutePath());
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/test/");
        if (!storageDir.exists()) {
            Log.d("createImageFile 33333 ", "directory not exist");
            storageDir.mkdirs();
            Log.d("createImageFile 44444 ", "mkdir complete");
        }


        File image = File.createTempFile(imageFileName, ".jpg", storageDir);


        Log.d("createImageFile", "complete creating image file");
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        Log.d("createImageFile ", mCurrentPhotoPath);
        uploadFileName = mCurrentPhotoPath.substring(30);
        Log.d("createImageFile 김민기  ",  mCurrentPhotoPath.substring(30) );
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(getBaseContext(), "resultCode: " + resultCode, Toast.LENGTH_SHORT).show();

        if(requestCode == PICK_FROM_ALBUM) {
            if(data == null) {
                Log.d("onActivityResult", "PICK_FROM_ALBUM but data is NULL");
                return;
            }

            if(resultCode == Activity.RESULT_OK) {
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
        }

        else if(requestCode == PICK_FROM_CAMERA) {
            if(data == null) {
                Log.d("onActivityResult", "PICK_FROM_CAMERA but data is NULL");
                return;
            }
            else {
                cropImage();
                // for showing photo on album
                MediaScannerConnection.scanFile(this, new String[]{photoUri.getPath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String s, Uri uri) {

                            }
                        });
            }
        }
        else if(requestCode == CROP_FROM_CAMERA) {
            if(data == null) {
                Log.d("onActivityResult", "CROP_FROM_CAMERA but data is NULL");
                return;
            }
            else {
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

        if(resultCode == 3) { // search company address
            if(requestCode == 3) {
                if(requestCode == 3 && data != null) {
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
}
