package com.codebakery.joan.hardwarecamera1113;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button) findViewById(R.id.buttonCamera)).setOnClickListener(this);
        requirePermission();
    }



    void requirePermission(){
        String[] permissions = new String[] {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ArrayList<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission: permissions) {
           if( ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_DENIED){
               //권한 거부 당했을때,요청할 권한들을 add한다
               listPermissionsNeeded.add(permission);
           }
        }

        if (!listPermissionsNeeded.isEmpty()){
            //권한 요청
            listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]);
        }
    }

    @Override
    public void onClick(View view) {
        boolean camera = ContextCompat.checkSelfPermission(view.getContext(),Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean write = ContextCompat.checkSelfPermission(view.getContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if(camera && write){
            takePhoto();
        }else {
            Log.e("ClickButton","카메라 이용권한, 쓰기 권한 필요");
        }
    }

    private void takePhoto() {
        //사진찍기
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null){
            File photoFile = null;
            try {
                photoFile = createImageFile();
                if(photoFile != null){
                    Uri uri = FileProvider.getUriForFile(this,
                            "com.codebakery.joan.hardwarecamera1113",
                            photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent,REQUEST_TAKE_PHOTO);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


}
