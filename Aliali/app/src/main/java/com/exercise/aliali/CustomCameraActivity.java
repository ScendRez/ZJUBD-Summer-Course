package com.exercise.aliali;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.exercise.aliali.LoadActivity;
import com.exercise.aliali.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class CustomCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    private SurfaceHolder mHolder;
    private ImageView mImageView;
    private VideoView mVideoView;
    private Button mRecordButton;
    private TextView mtextView;
    private boolean isRecording = false;

    private String mp4Path = "";
    private static final int REQUEST_CODE_ADD = 1002;

    private Handler uiHandler = new Handler();

    public static void startUI(Context context) {
        Intent intent = new Intent(context, CustomCameraActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera);
        setTitle(R.string.record_title);
        mSurfaceView = findViewById(R.id.surfaceview);
        mImageView = findViewById(R.id.iv_img);
        mVideoView = findViewById(R.id.videoview);
        mRecordButton = findViewById(R.id.bt_record);
        mtextView = findViewById(R.id.bt_remain_time);

        FloatingActionButton fab = findViewById(R.id.fab_upload);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent=new Intent(CustomCameraActivity.this, LoadActivity.class);
                startActivity(intent);
            }
        });
        mHolder = mSurfaceView.getHolder();
        initCamera();
        mHolder.addCallback(this);
    }

    private void initCamera() {
        mCamera = Camera.open();
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.set("orientation", "portrait");
        parameters.set("rotation", 90);
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);
    }

    private boolean prepareVideoRecorder() {
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        // todo
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mp4Path = getOutputMediaPath();
        mMediaRecorder.setOutputFile(mp4Path);

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());
        mMediaRecorder.setOrientationHint(90);

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder() {
        // todo
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private String getOutputMediaPath() {
        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Log.i("tag",mediaStorageDir.getPath());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".mp4");
        if (!mediaFile.exists()) {
            mediaFile.getParentFile().mkdirs();
        }
        return mediaFile.getAbsolutePath();
    }
    public void record(View view) {
        if (isRecording) {
            mRecordButton.setText("录制");

            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setOnInfoListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }

//            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
            mImageView.setVisibility(View.GONE);
            mVideoView.setVideoPath(mp4Path);
            mVideoView.start();
            // Intent intent = new Intent(CustomCameraActivity.this, NoteActivity.class);
            // intent.putExtra("path", mp4Path);
            // startActivity(intent);

            mVideoView.setVisibility(View.VISIBLE);
        } else {
            if(prepareVideoRecorder()) {
                mRecordButton.setText("暂停");
                mMediaRecorder.start();

                mtextView.setVisibility(View.VISIBLE);

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        long date_start =  System.currentTimeMillis();
                        long date_end = System.currentTimeMillis();
                        while (date_end - date_start < 10 * 1000) {
                            if (!isRecording)
                                return;
                            final String s = ""+String.format("%d", (int)(10 - (date_end - date_start) / 1000));
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    mtextView.setText(s);
                                }
                            };
                            uiHandler.post(runnable);
                            date_end = System.currentTimeMillis();
                        }
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                record(mRecordButton);
                                mtextView.setText("");
                            }
                        };
                        uiHandler.post(runnable);
                    }
                }).start();
            }
        }
        isRecording = !isRecording;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // todo
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (holder.getSurface() == null) {
            return;
        }
        //停止预览效果
        mCamera.stopPreview();
        //重新设置预览效果
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            initCamera();
        }
        mCamera.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }
}