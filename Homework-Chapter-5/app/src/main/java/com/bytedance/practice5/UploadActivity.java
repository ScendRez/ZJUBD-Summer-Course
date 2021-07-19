package com.bytedance.practice5;


import android.app.Activity;
import android.content.Intent;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.practice5.model.MessageListResponse;
import com.bytedance.practice5.model.UploadResponse;
import static com.bytedance.practice5.Constants.BASE_URL;
import static com.bytedance.practice5.Constants.STUDENT_ID;
import static com.bytedance.practice5.Constants.USER_NAME;
import static com.bytedance.practice5.Constants.token;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;

public class UploadActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "chapter5";
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    private static final int REQUEST_CODE_COVER_IMAGE = 101;
    private static final String COVER_IMAGE_TYPE = "image/*";
    private IApi api;
    private Uri coverImageUri;
    private SimpleDraweeView coverSD;
    private EditText fromEditText;
    private EditText toEditText;
    private EditText contentEditText ;
    private Retrofit retrofit;
    private Switch sw;
    private boolean useHttp=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initNetwork();
        setContentView(R.layout.activity_upload);
        coverSD = findViewById(R.id.sd_cover);
        fromEditText = findViewById(R.id.et_from);
        toEditText = findViewById(R.id.et_to);
        contentEditText = findViewById(R.id.et_content);
        findViewById(R.id.btn_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_COVER_IMAGE, COVER_IMAGE_TYPE, "选择图片");
            }
        });
        sw=findViewById(R.id.switch_useHttp);
        if (sw != null) {
            sw.setOnCheckedChangeListener(this);
        }


        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!useHttp) submit();
                else submitMessageWithURLConnection();
            }
        });
    }
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            useHttp=true;//do stuff when Switch is ON
        } else {
            useHttp=false;//do stuff when Switch if OFF
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_COVER_IMAGE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                coverImageUri = data.getData();
                coverSD.setImageURI(coverImageUri);

                if (coverImageUri != null) {
                    Log.d(TAG, "pick cover image " + coverImageUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }

            } else {
                Log.d(TAG, "file pick fail");
            }
        }
    }

    private void initNetwork() {
        //TODO 3
        // 创建Retrofit实例
        // 生成api对象
        retrofit=new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api=retrofit.create(IApi.class);
    }

    private void getFile(int requestCode, String type, String title) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }
    private Handler submitHandler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(android.os.Message msg)
        {
            super.handleMessage(msg);
            switch(msg.what)
            {
                case 234:
                    Toast.makeText(getBaseContext(), "提交失败", Toast.LENGTH_SHORT).show();
                    break;
                case 235:
                    Toast.makeText(getBaseContext(), "提交成功", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    break;
                default:    break;
            }
        }
    };
    private void submit() {
        Log.i("DEBUG","NOW ON RETROFIT");
        byte[] coverImageData = readDataFromUri(coverImageUri);
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(this, "封面不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        String from = fromEditText.getText().toString();
        if (TextUtils.isEmpty(from)) {
            Toast.makeText(this, "请输入你的名字", Toast.LENGTH_SHORT).show();
            return;
        }
        String to = toEditText.getText().toString();
        if (TextUtils.isEmpty(to)) {
            Toast.makeText(this, "请输入TA的名字", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = contentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入想要对TA说的话", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( coverImageData.length >= MAX_FILE_SIZE) {
            Toast.makeText(this, "文件过大", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO 5
        // 使用api.submitMessage()方法提交留言
        // 如果提交成功则关闭activity，否则弹出toast
        MultipartBody.Part img,fromB,toB,contentB;
        img = MultipartBody.Part.createFormData("image", "cover.png", RequestBody.create(MediaType.parse("multipart/form-data"), coverImageData));
        fromB = MultipartBody.Part.createFormData("from",from);
        toB = MultipartBody.Part.createFormData("to",to);
        contentB = MultipartBody.Part.createFormData("content",content);
        Call<UploadResponse> call= api.submitMessage(STUDENT_ID,"",fromB,toB,contentB,img, token);
        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if(!response.isSuccessful())
                {
                    submitHandler.sendEmptyMessage(234);
                    Log.i("Resp",String.valueOf(response.code()));;
                    Log.i("DEBUG",String.valueOf(response.body()));;
                    Log.i("Resp",String.valueOf(call.request().url()));
                    return;
                }
                submitHandler.sendEmptyMessage(235);
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                t.printStackTrace();
                Log.i("HW5","failed");
            }
        });
    }


    // TODO 7 选做 用URLConnection的方式实现提交
    private void submitMessageWithURLConnection(){
        Log.i("DEBUG","NOW ON HTTP_URL");
        byte[] coverImageData = readDataFromUri(coverImageUri);
        //final String fileName = "cover.png";
        //Log.i("DEBUG",fileName);
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(getBaseContext(), "封面不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        String from = fromEditText.getText().toString();
        if (TextUtils.isEmpty(from)) {
            Toast.makeText(getBaseContext(), "请输入你的名字", Toast.LENGTH_SHORT).show();
            return;
        }
        String to = toEditText.getText().toString();
        if (TextUtils.isEmpty(to)) {
            Toast.makeText(getBaseContext(), "请输入TA的名字", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = contentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(getBaseContext(), "请输入想要对TA说的话", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( coverImageData.length >= MAX_FILE_SIZE) {
            Toast.makeText(getBaseContext(), "文件过大", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String urlStr=BASE_URL+"messages";
                urlStr += ("?student_id="+STUDENT_ID+"&extra_value=");
                try {
                    URL url=new URL(urlStr);
                    String BOUNDARY="SUxvdmVBeWVyc2NhcnBl",BOUNDARY_PREFIX="--",RETURN="\r\n";
                    String TEXT_FORMAT="Content-Type: text/plain; charset=UTF-8\r\nContent-Transfer-Encoding: 8bit";
                    HttpURLConnection connect=(HttpURLConnection) url.openConnection();
                    connect.setConnectTimeout(6000);
                    connect.setRequestMethod("POST");
                    connect.setRequestProperty("Content-Type", "multipart/form-data; boundary="+BOUNDARY);
                    connect.setRequestProperty("Connection","Keep-Alive");
                    connect.setRequestProperty("token",token);
                    connect.connect();
                    OutputStream out = connect.getOutputStream();
                    //OutputStreamWriter outWrite = new OutputStreamWriter(out,StandardCharsets.UTF_8);
                    //requestBody=("from="+from+"&to="+to+"&content="+content+"&image="+coverImageData.toString()).getBytes(StandardCharsets.UTF_8);
                    StringBuilder sb= new StringBuilder();

                    sb.append(BOUNDARY_PREFIX);
                    sb.append(BOUNDARY);
                    sb.append(RETURN);
                    sb.append("Content-Disposition: form-data; name=\"from\""+RETURN+
                            TEXT_FORMAT+RETURN+RETURN);
                    sb.append(from+RETURN);

                    sb.append(BOUNDARY_PREFIX);
                    sb.append(BOUNDARY);
                    sb.append(RETURN);
                    sb.append("Content-Disposition: form-data; name=\"to\""+RETURN+
                            TEXT_FORMAT+RETURN+RETURN);
                    sb.append(to+RETURN);

                    sb.append(BOUNDARY_PREFIX);
                    sb.append(BOUNDARY);
                    sb.append(RETURN);
                    sb.append("Content-Disposition: form-data; name=\"content\""+RETURN+
                            TEXT_FORMAT+RETURN+RETURN);
                    sb.append(content+RETURN);

                    sb.append(BOUNDARY_PREFIX);
                    sb.append(BOUNDARY);
                    sb.append(RETURN);

                    sb.append("Content-Disposition: form-data; name=\"image\"; filename=\"cover.png\""+RETURN+
                            "Content-Type: application/octet-stream"+RETURN+
                            "Content-Transfer-Encoding: binary"+RETURN+RETURN);

                    out.write(sb.toString().getBytes());
                    out.write(coverImageData,0,coverImageData.length);
                    out.write((RETURN+BOUNDARY_PREFIX+BOUNDARY+BOUNDARY_PREFIX+RETURN).getBytes());
                    out.flush();
                    String msg="";
                    if(connect.getResponseCode()==200)
                    {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(connect.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            msg += line + "\n";
                        }
                        reader.close();
                        Log.i("DEBUG",msg);
                        submitHandler.sendEmptyMessage(235);
                    }
                    else submitHandler.sendEmptyMessage(234);
                    Log.i("DEBUG",connect.toString());
                    out.close();
                    connect.disconnect();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.i("DEBUG",e.toString());
                }
            }
        }).start();
    }

    private byte[] readDataFromUri(Uri uri) {
        byte[] data = null;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            data = Util.inputStream2bytes(is);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


}
