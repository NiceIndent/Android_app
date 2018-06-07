package com.websarva.wings.android.niceindent;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap map_ = null;

    private TextView _tvLatitude;
    private Uri _imageUri;

    private double _latitude = 35.681382;
    private double _longitude = 139.766084;

    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    finish();
                    return false;

                case R.id.navigation_camera:
                    onCameraMenuClick();
                    return false;

                case R.id.navigation_upload:
                    return false;

                case R.id.navigation_maps:
                    return false;

            }
            return false;
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //LocationManager オブジェクトを取得
        //LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //位置情報が更新された際のリスナオブジェクトを生成
        //GPSLocationListener locationListener = new GPSLocationListener();

        //Log.d("d", "テストテスト"+_latitude+"");
        //_tvLatitude = findViewById(R.id.tvLatitude);

        //位置情報の追跡開始
        //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
        //    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        //    ActivityCompat.requestPermissions(MapActivity.this, permissions, 1000);

        //    return;
        // }

        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.getMenu().findItem(R.id.navigation_maps).setChecked(true);


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        map_ = map;
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
        map.setMyLocationEnabled(true);
        // Add a marker in Tokyo station and move the camera
        //LatLng tokyo = new LatLng(35.681382,139.766084);
        //map_.addMarker(new MarkerOptions().position(tokyo).title(""));
        //map_.moveCamera(CameraUpdateFactory.newLatLngZoom(tokyo, 15));


    }

/* 座標の取得関連
    public class GPSLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            _latitude = location.getLatitude();
            _longitude = location.getLongitude();

            _tvLatitude.setText(Double.toString(_latitude));


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras){}

        @Override
        public void onProviderEnabled(String provider){}

        @Override
        public void onProviderDisabled(String provider){}


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        //ACCESS_FINE_LOCATIONに対する’パーミッションダイアログでかつ許可を選択した場合
        if(requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //LocationManagerオブジェクト取得
            LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
            //位置情報が更新された場合のリスナオブジェクトを生成
            GPSLocationListener locationListener = new GPSLocationListener();

            //再度許可のチェック，許可がなければ中止
            if(ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }
            //位置情報の追跡開始
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);
        }
    }



*/

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

}

