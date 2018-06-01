package com.example.mingi.management;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BCEditActivity extends AppCompatActivity {


    ImageView imageView;
    EditText bcname, bclevel, bccom, bcphone, bcemail;
    TextView bcadd;
    Button bccamera, bcupload;

    String bcname_str, bclevel_str, bccom_str, bcphone_str, bcemail_str, bcadd_str;
    String bclat, bclon;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bcedit);
        initView();
        checkPermissions();
        getDataFromDetail();
        setInitView();
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

    void getDataFromDetail() {
        Intent fromDetail = getIntent();
        bcname_str = fromDetail.getStringExtra("bcname");
        bclevel_str = fromDetail.getStringExtra("bclevel");
        bccom_str = fromDetail.getStringExtra("bccom");
        bcphone_str = fromDetail.getStringExtra("bcphone");
        bcemail_str = fromDetail.getStringExtra("bcemail");
        bcadd_str = fromDetail.getStringExtra("bcadd");
        nowLat = fromDetail.getStringExtra("nowLat");
        nowLon = fromDetail.getStringExtra("nowLon");
        isGPSEnable = fromDetail.getStringExtra("isGPSEnable");
        nowName = fromDetail.getStringExtra("nowName");
    }

    void setInitView() {
        bcname.setText(bcname_str);
        bclevel.setText(bclevel_str);
        bccom.setText(bccom_str);
        bcphone.setText(bcphone_str);
        bcemail.setText(bcemail_str);
        bcadd.setText(bcadd_str);
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
                != PackageManager.PERMISSION_GRANTED)
        {

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