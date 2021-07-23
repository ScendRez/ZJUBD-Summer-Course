package com.exercise.aliali;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.TokenWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.exercise.aliali.MainActivity;
import com.exercise.aliali.R;
import com.exercise.aliali.MiniTikTok;
import com.exercise.aliali.PostVideoResponse;
import com.exercise.aliali.VideoData;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoadActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final String TAG = "MainActivity";
    private RecyclerView mRv;
    private List<VideoData> mDataSet = new ArrayList<>();
    public Uri mSelectedImage;
    private Uri mSelectedVideo;
    public Button mBtn_cover;
    public Button mBtn_video;
    public Button mBtn_submit;
    private Button mBtnRefresh;

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(MiniTikTok.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private MiniTikTok TikTokService = retrofit.create(MiniTikTok.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        setTitle(R.string.submit_title);
        Init();
    }

    private void Init()
    {
        mBtn_cover=findViewById(R.id.btn_cover);
        mBtn_video=findViewById(R.id.btn_video);
        mBtn_submit=findViewById(R.id.btn_submit);
        mBtn_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                chooseImage();
            }
    });
        mBtn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                chooseVideo();
            }
        });
        mBtn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //chooseImage();
                if (mSelectedVideo != null && mSelectedImage != null) {
                    postVideo();
                }
//                else
//                {
//                    throw new IllegalArgumentException("error data uri, mSelectedVideo = "
//                            + mSelectedVideo
//                            + ", mSelectedImage = "
//                            + mSelectedImage);
//                }
            }
        });

    }
    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE);
    }

    public void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO);
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        File f = new File(ResourceUtils.getRealPath(LoadActivity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }
    private void postVideo() {
        mBtn_submit.setEnabled(false);
        mBtn_submit.setText(R.string.uploading);
        MiniTikTok TikTokService = retrofit.create(MiniTikTok.class);
        byte[] coverImageData = readDataFromUri(mSelectedImage);
        byte[] videoDat = readDataFromUri(mSelectedVideo);
        MultipartBody.Part img = MultipartBody.Part.createFormData("cover_image", "cover.png",
                RequestBody.create(MediaType.parse("multipart/form-data"), coverImageData));

        MultipartBody.Part vid = MultipartBody.Part.createFormData("video", "video.mp4",
                RequestBody.create(MediaType.parse("multipart/form-data"), videoDat));
//
//        MultipartBody.Part coverImagePart = getMultipartFromUri("cover_image", mSelectedImage);
//        MultipartBody.Part videoPart = getMultipartFromUri("video", mSelectedVideo);
        Log.i("TAG",mSelectedImage.toString() + ";" + mSelectedVideo.toString());
        TikTokService.postVideo("3190101770", "小黑",null, img, vid, com.exercise.aliali.MainActivity.TOKEN).enqueue(
                new Callback<PostVideoResponse>() {
                    @Override
                    public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                        Log.i("TAG","success");
                        if (response.body() != null) {
//                            Toast.makeText(LoadActivity.this, response.body().toString(), Toast.LENGTH_SHORT)
//                                    .show();
                            Toast.makeText(LoadActivity.this, "上传成功~快去首页刷新查看你的视频啦！", Toast.LENGTH_SHORT)
                                    .show();
                            //Snackbar.make(findViewById(R.id.loadActivity),"上传成功",3000);
                            mBtn_submit.setEnabled(true);
                            mBtn_submit.setText(R.string.submit);
                        }
                    }
                    @Override
                    public void onFailure(Call<PostVideoResponse> call, Throwable throwable) {
                        Log.i("TAG","fail");
                        Toast.makeText(LoadActivity.this, "失败惹...因为"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        mBtn_submit.setEnabled(true);
                        mBtn_submit.setText(R.string.submit);
                    }
                });
        Log.i("TAG","finish");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() called with: requestCode = ["
                + requestCode
                + "], resultCode = ["
                + resultCode
                + "], data = ["
                + data
                + "]");

        if (resultCode == RESULT_OK && null != data) {
            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                Log.d(TAG, "selectedImage = " + mSelectedImage);
            } else if (requestCode == PICK_VIDEO) {
                mSelectedVideo = data.getData();
                Log.d(TAG, "mSelectedVideo = " + mSelectedVideo);
            }
        }
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
