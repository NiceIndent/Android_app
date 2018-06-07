package com.websarva.wings.android.niceindent;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import android.content.Intent;
import android.widget.ImageView;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private TextView answer;
    private Uri _imageUri;
    private Uri m_uri;
    private RequestQueue mQueue;

    final String url = "https://api.apigw.smt.docomo.ne.jp/imageRecognition/v1/concept/classify/?APIKEY=3136636f45474468776a6c714671784f686e4479777965676c6a2f4841504c3752756366506b4c67543032";

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

                case R.id.navigation_upload:
                    onUploadMenuClick();
                    return false;

                case R.id.navigation_maps:
                    //mTextMessage.setText(R.string.title_notifications);
                    Intent intent = new Intent(MainActivity.this, MapActivity.class);
                    int data1 = 1;
                    intent.putExtra("menu_number", data1);
                    startActivity(intent);
                    return false;


            }
            return false;
        }
    };
    private byte[] mImageData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = findViewById(R.id.message);
        answer = findViewById(R.id.answer);
        //説明文を格納する変数
        TextView landmark = (TextView) findViewById(R.id.landmark);
        //TextView explain = (TextView)findViewById(R.id.explain);


        BottomNavigationView navigation = findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            ContentResolver cr = getContentResolver();
            String[] columns = {MediaStore.Images.Media.DATA};
            Cursor c = cr.query(data.getData(), columns, null, null, null);
            c.moveToFirst();
            Log.d("aaa", "Image File Path: " + c.getString(0));


            if (resultCode != RESULT_OK) {
                // キャンセル時
                return;
            }

            Uri resultUri = (data != null ? data.getData() : m_uri);
            Uri uri2 = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri2);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bytes = baos.toByteArray();


                if (resultUri == null) {
                    // 取得失敗
                    return;
                }

                // ギャラリーへスキャンを促す
                MediaScannerConnection.scanFile(
                        this,
                        new String[]{resultUri.getPath()},
                        new String[]{"image/jpeg"},
                        null
                );


                // 画像を設定
                //Bitmap bitmap = data.getParcelableExtra("data");
                ImageView ivCamera = findViewById(R.id.ivCamera);
                ivCamera.setImageURI(resultUri);

                mQueue = Volley.newRequestQueue(this);
                //Log.d("DEBUG", "Uriの場所" + resultUri.getPath());

                Map fileMap = new HashMap();
                Map<String, String> stringMap = new HashMap<String, String>();


                stringMap.put("modelName", URLEncoder.encode("landmark", "UTF-8"));

                fileMap.put("image", bytes);

                MultipartRequest multipartRequest = new MultipartRequest(
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //Upload成功
                                Log.d("DEBUG", "Upload success: " + response);

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Upload失敗
                                Log.d("d", "Upload error!!!: " + error.getMessage());
                            }

                        },
                        stringMap, fileMap);

                mQueue.add(multipartRequest);



                } catch (IOException e) {
                    Log.e("ERROR", e.getMessage());
                }
        }
    }


    public void onCameraMenuClick() {
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

    public void onUploadMenuClick() {
        Intent intentGallery;
        if (Build.VERSION.SDK_INT < 19) {
            intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
            intentGallery.setType("image/*");
        } else {
            intentGallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intentGallery.addCategory(Intent.CATEGORY_OPENABLE);
            intentGallery.setType("image/jpeg");
        }

        ContentValues contentValues = new ContentValues();

        m_uri = getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);


        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, m_uri);

        Intent intent = Intent.createChooser(intentCamera, "画像の選択");
        intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentGallery});
        startActivityForResult(intent, 1000);


    }


    class MultipartRequest extends Request<String> {


        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        MultipartEntityBuilder entity2 = MultipartEntityBuilder.create();

        HttpEntity httpEntitiy;
        private final Response.Listener<String> mListener;
        private final Map<String, String> mStringParts;
        private final Map<String, byte[]> mFileParts;

        public MultipartRequest(String url, Response.Listener<String> listener,
                                Response.ErrorListener errorListener,
                                Map<String, String> stringParts, Map fileParts) {
            super(Method.POST, url, errorListener);

            mListener = listener;
            mStringParts = stringParts;
            mFileParts = fileParts;
            buildMultipartEntity();
        }

        private void buildMultipartEntity() {
            entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            //送信するリクエストを設定する
            //StringData
            for (Map.Entry<String, String> entry : mStringParts.entrySet()) {
                entity.addTextBody(entry.getKey(), entry.getValue());

            }

            //byte DataKEY_PICTURE, file, ContentType.create("image/jpeg"), fileName

            entity.addBinaryBody("image",mFileParts.get("image"), ContentType.create("image/jpeg"), "sample.jpeg");

            /*entity.addBinaryBody("image", mFileParts.get("image"));
            entity.addTextBody("fileName","sample.jpeg");
            entity.addTextBody("withName", "image");
            entity.addTextBody("mimeType", "image/jpeg");*/

            httpEntitiy = entity.build();
            //Log.d("entyty", "konn");

        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = super.getHeaders();
            Map<String, String> newHeaders = new HashMap<String, String>();
            newHeaders.putAll(headers);
            newHeaders.put("Content-Type", "multipart/form-data");
            Log.e("TEST", String.valueOf(newHeaders));
            return newHeaders;
        }


        @Override
        public String getBodyContentType() {
            return httpEntitiy.getContentType().getValue();
        }

        public HttpEntity getEntity() {
            return httpEntitiy;
        }


        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            return Response.success("Uploaded", getCacheEntry());

        }

        //リスナーにレスポンスを返す
        @Override
        protected void deliverResponse(String response) {
            mListener.onResponse(response);
        }


    }
    }



