package com.websarva.wings.android.niceindent;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.TextView;

import android.content.Intent;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity{

    private TextView mTextMessage;
    private Uri _imageUri;

    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return false;

                case R.id.navigation_camera:
                    mTextMessage.setText(R.string.title_camera);
                    onCameraMenuClick();

                    return false;

                case R.id.navigation_maps:
                    //mTextMessage.setText(R.string.title_notifications);
                    Intent intent = new Intent(MainActivity.this, MapActivity.class);
                    int data1 = 1;
                    intent.putExtra("menu_number", data1);
                    int requestCode = 1;
                    startActivityForResult( intent, requestCode );
                    startActivity(intent);

                    return false;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200 && resultCode == RESULT_OK) {
            //Bitmap bitmap = data.getParcelableExtra("data");
            ImageView ivCamera = findViewById(R.id.ivCamera);
            ivCamera.setImageURI(_imageUri);
        }
    }

    public void onCameraMenuClick(){
        // WRITE_EXTERNAL_STORAGEの許可の有無で分岐
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, 2000);
            return;
        }

        //保存する際のファイル名設定
        //日時データの整形
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddss");
        Date now = new Date(System.currentTimeMillis());

        String nowStr = dataFormat.format(now);

        String fileName = "UseCameraActivity" + nowStr + ".jpg";

        ContentValues values = new ContentValues();
        //画像ファイル名の設定
        values.put(MediaStore.Images.Media.TITLE, fileName);
        //画像ファイルの種類を指定
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        ContentResolver resolver = getContentResolver();

        _imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageUri);

        //アクティビティの起動
        startActivityForResult(intent, 200);

    }

    //@Override
    //許可周りの実装，未完成
    public void OnRequestPermissionResult(int requestCode, String[] permission, int[] grantResults){
        if (requestCode == 2000 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            ImageView ivCamera = findViewById(R.id.ivCamera);
            //onNavigationItemSelected(MenuItem )
        }
    }


}
