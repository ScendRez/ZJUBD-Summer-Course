package com.example.mediaworksdemo;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class VideoDetailActivity extends AppCompatActivity {

    MediaPlayer player= new MediaPlayer();
    SurfaceHolder holder;
    TextView timelabel;
    Button playctrl;
    SeekBar seekBar;
    ProgressUpdateThread put;
    private Handler handler=new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int currentPosition=0;
            if(msg.what==250)
            {
                if(player!=null) {
                    currentPosition = player.getCurrentPosition();
                    int maxPosition=player.getDuration();
                    seekBar.setProgress((int)((float)currentPosition*100f/(float)maxPosition));
                    timelabel.setText((currentPosition/60000)+((currentPosition/1000)>10?":":":0")+((currentPosition/1000)%60)+"/"+(maxPosition/60000)+((maxPosition/1000)>10?":":":0")+((maxPosition/1000)%60));
                }
            }
            sendEmptyMessageDelayed(250,1000-currentPosition%1000);
        }
    };
    String mockUrl = "https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218114723HDu3hhxqIT.mp4";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        seekBar=findViewById(R.id.seekbar);
        timelabel=findViewById(R.id.tv_progress);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo((int)(player.getDuration()*(float)seekBar.getProgress()/(float)seekBar.getMax()));
            }
        });
        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                int secendProssed= player.getDuration()/100*percent;
                seekBar.setSecondaryProgress(secendProssed);
            }
        });
        playctrl=findViewById(R.id.btn_pauseplay);
        playctrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player.isPlaying())
                {
                    player.pause();
                    playctrl.setText("Play");
                }
                else
                {
                    player.start();
                    playctrl.setText("Pause");
                }
            }
        });
        SurfaceView svv=findViewById(R.id.svv);
        try{
            player.setDataSource(this,Uri.parse(mockUrl));
            holder= svv.getHolder();
            holder.addCallback(new PlayerCallBack());
            player.prepare();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // 首先取得video的宽和高
                    int vWidth = player.getVideoWidth();
                    int vHeight = player.getVideoHeight();
                    // 该LinearLayout的父容器 android:orientation="vertical" 必须
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutPlay);
                    int lw = linearLayout.getWidth();
                    int lh = linearLayout.getHeight();
                    Log.i("DEBUG","jkasd");
                        // 如果video的宽或者高超出了当前屏幕的大小，则要进行缩放
                        float wRatio = (float) vWidth / (float) lw;
                        float hRatio = (float) vHeight / (float) lh;
                        // 选择大的一个进行缩放
                        float ratio = Math.max(wRatio, hRatio);
                        vWidth = (int) Math.ceil((float) vWidth / ratio);
                        vHeight = (int) Math.ceil((float) vHeight / ratio);
                        Log.i("DEBUG",String.valueOf(vWidth));
                        Log.i("DEBUG",String.valueOf(vHeight));

                        // 设置surfaceView的布局参数
                        LinearLayout.LayoutParams lp= new LinearLayout.LayoutParams(vWidth, vHeight);
                        lp.gravity = Gravity.CENTER;
                        svv.setLayoutParams(lp);
                    // 然后开始播放视频
                    player.start();
                    player.setLooping(true);
                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        put=new ProgressUpdateThread();
        put.run();

//        VideoView videoView = findViewById(R.id.vv_detail);
//        videoView.setVideoURI(Uri.parse(mockUrl));
//        videoView.setMediaController(new MediaController(this));
//        videoView.start();
    }

    private class PlayerCallBack implements SurfaceHolder.Callback
    {
        @Override
        public void surfaceCreated(SurfaceHolder holder)
        {
            player.setDisplay(holder);
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder,int format,int width,int height)
        {

        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder)
        {
        }
    }

    public class ProgressUpdateThread extends Thread{
        @Override
        public void run() {
            super.run();
            handler.sendEmptyMessage(250);
        }
    }

}
