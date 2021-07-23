package com.exercise.aliali;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.exercise.aliali.MainActivity.TOKEN;

public class VideoplayActivity extends AppCompatActivity implements MyAdaptor.IOnItemClickListener {
    private VideoData videoData;

    private MediaPlayer player = new MediaPlayer();
    private SurfaceHolder holder;
    private TextView timelabel;
    private ImageButton playctrl;
    private SeekBar seekBar;
    private LinearLayout controlbar, actionresponse;
    private ProgressBar loadingring;
    private int onPauseProgress = 0, isonPausePlaying = 0;
    private Boolean notinit = true, isOnPause = false, onPauseLoadSuccess = false;
    private ImageView iv_like;

    private TextView sameperson;
    private RecyclerView recyclerView;
    private MyAdaptor mAdaptor;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton fab;
    //    private GridLayoutManager gridLayoutManager;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    DisplayMetrics outMetrics = new DisplayMetrics();
    SurfaceView svv;
    private final Context context=this;
    private Handler handleanim=new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==333)
            {
                iv_like.setVisibility(View.VISIBLE);
                fab.setEnabled(false);
                Animator animator=AnimatorInflater.loadAnimator(context,R.animator.popup);
                animator.setTarget(findViewById(R.id.an_heartsmile));
                animator.start();
                sendEmptyMessageDelayed(334,2000);
            }
            if(msg.what==334)
            {
                Animator animator=AnimatorInflater.loadAnimator(context,R.animator.fadeout);
                animator.setTarget(findViewById(R.id.an_heartsmile));
                animator.start();
                sendEmptyMessageDelayed(335,1000);
            }
            if(msg.what==335) fab.setEnabled(true);
        }
    };
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

                    if(controlbar.getVisibility()==View.GONE) {
                        controlbar.setVisibility(View.VISIBLE);
                        actionresponse.setVisibility(View.VISIBLE);
                    }

                    if(loadingring.getVisibility()==View.VISIBLE
                    ) loadingring.setVisibility(View.GONE);
                    if(!seekBar.isPressed()) seekBar.setProgress((int)((float)currentPosition/(float)maxPosition*1000f));
                    timelabel.setText(((currentPosition/60000)>=10?"":"0")+
                            (currentPosition/60000)+((currentPosition/1000%60)>=10?":":":0")+
                            ((currentPosition/1000)%60)+((maxPosition/60000)>=10?"/":"/0")+
                            (maxPosition/60000)+((maxPosition/1000%60)>=10?":":":0")+((maxPosition/1000)%60));
                }
            }
            sendEmptyMessageDelayed(250,1000-currentPosition%1000);
        }
    };
    private TextView title,author,uploadtime;
    public class MyThread extends Thread{

        @Override
        public void run() {
            super.run();
            try{
                player.setDataSource(context,Uri.parse(videoData.videoUrl));
                holder= svv.getHolder();
                holder.addCallback(new PlayerCallBack());
                player.prepare();
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // 首先取得video的宽和高
                        if(!isOnPause) player.start();
                        else onPauseLoadSuccess=true;
                        player.setLooping(false);


                        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
                        int videoW=player.getVideoWidth();
                        int videoH=player.getVideoHeight();
                        int widthPixels = outMetrics.widthPixels;
                        int VideoPlayHeight = widthPixels*videoH/videoW;
                        FrameLayout.LayoutParams lp= new FrameLayout.LayoutParams(widthPixels, VideoPlayHeight);
                        lp.gravity = Gravity.CENTER;
                        svv.setLayoutParams(lp);
                        handler.sendEmptyMessage(333);
                    }
                });

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setWindowStatusBarColor(VideoplayActivity.this,R.color.black);
        setContentView(R.layout.activity_videoplay);

        fab = findViewById(R.id.fab_like);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleanim.sendEmptyMessage(333);
            }
        });
        sameperson=findViewById(R.id.tv_sameperson);
        controlbar=findViewById(R.id.layout_controlBar);
        svv=findViewById(R.id.svv);
        iv_like=findViewById(R.id.an_heartsmile);
        title=findViewById(R.id.tv_title);
        author=findViewById(R.id.tv_author);
        uploadtime=findViewById(R.id.tv_createtime);
        seekBar=findViewById(R.id.seekbar);
        timelabel=findViewById(R.id.tv_progress);
        videoData=(VideoData)getIntent().getSerializableExtra("videoData");
        loadingring=findViewById(R.id.loading);
        actionresponse=findViewById(R.id.layout_action_response);
        iv_like=findViewById(R.id.an_heartsmile);

        iv_like.setVisibility(View.INVISIBLE);
        actionresponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player.isPlaying())
                {
                    player.pause();
                    playctrl.setImageDrawable(getResources().getDrawable(R.drawable.icon_play));
                    handler.sendEmptyMessage(250);
                }
                else
                {
                    player.start();
                    playctrl.setImageDrawable(getResources().getDrawable(R.drawable.icon_pause));
                    handler.sendEmptyMessage(250);
                }
            }
        });
        // 设置surfaceView的布局参数
        controlbar.setVisibility(View.GONE);
        actionresponse.setVisibility(View.GONE);
        loadingring.setVisibility(View.VISIBLE);
        String uploadtimestring=videoData.createTime.toString();
        uploadtimestring=uploadtimestring.substring(0,uploadtimestring.indexOf('.')) ;
        title.setText(videoData.title);
        author.setText(videoData.author);
        uploadtime.setText(("于 "+uploadtimestring+" 上传"));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if(player!=null) player.seekTo((int)((float)seekBar.getProgress()/(float)seekBar.getMax()*(float)player.getDuration()));
                int currentPosition = player.getCurrentPosition();
                int maxPosition=0;
                if(player!=null) maxPosition=player.getDuration();
                seekBar.setProgress((int)((float)currentPosition/(float)maxPosition*1000f));
            }
        });
        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                int secendProssed=0;
                if(mp.isPlaying()) secendProssed= mp.getDuration()/100*percent;
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
                    playctrl.setImageDrawable(getResources().getDrawable(R.drawable.icon_play));
                    handler.sendEmptyMessage(250);
                }
                else
                {
                    player.start();
                    playctrl.setImageDrawable(getResources().getDrawable(R.drawable.icon_pause));
                    handler.sendEmptyMessage(250);
                }
            }
        });
        initRecyclerView();

        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int heightPixels = outMetrics.heightPixels;
        int widthPixels = outMetrics.widthPixels;
        float density = context.getResources().getDisplayMetrics().density;
        int heightOfPanel=(int)(124*density+0.5f);
        //注意这里，到底是用ViewGroup还是用LinearLayout或者是FrameLayout，主要是看你这个EditTex
        //控件所在的父控件是啥布局，如果是LinearLayout，那么这里就要改成LinearLayout.LayoutParams
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(widthPixels,heightPixels-heightOfPanel);
        recyclerView.setLayoutParams(lp);
        new MyThread().start();
//        VideoView videoView = findViewById(R.id.vv_detail);
//        videoView.setVideoURI(Uri.parse(videoData.videoUrl));
//        videoView.setMediaController(new MediaController(this));
//        videoView.start();

    }

    public void initRecyclerView()
    {

        //获取实例
        recyclerView = findViewById(R.id.rv_detailed);
        //更改数据时不会变更宽高
        recyclerView.setHasFixedSize(true);
        //创建线性布局管理器
        layoutManager = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        //创建格网布局管理器
//        gridLayoutManager = new GridLayoutManager(this, 2);
        //设置布局管理器
        staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL){
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        fetchFeed(videoData.studentId);
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
    @Override
    protected void onPause()
    {
        super.onPause();
        onPauseProgress=player.getCurrentPosition();
        if(player.isPlaying()) isonPausePlaying=1;
        else isonPausePlaying=0;
        if(player.isPlaying()) player.pause();
        isOnPause=true;
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        if(!notinit) player.seekTo(onPauseProgress);
        else notinit=false;
        if(isonPausePlaying==1)
        {
            player.start();
            player.seekTo(onPauseProgress);
        }
        if(onPauseLoadSuccess)
        {
            player.start();
            onPauseLoadSuccess=false;
        }
        isOnPause=false;
    }
    @Override
    protected void onDestroy() {
        //将线程销毁掉
        super.onDestroy();
        player.release();
        handler.removeCallbacksAndMessages(null);
    }

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(MiniTikTok.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @Override
    public void onItemClick(int position, VideoData data) {
//        Toast.makeText(MainActivity.this, "点击了第" + position + "条", Toast.LENGTH_SHORT).show();
        //mAdaptor.addData(position + 1, vd);
        Intent intent=new Intent(VideoplayActivity.this,VideoplayActivity.class);
        intent.putExtra("videoData",data);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position, VideoData data) {
//        Toast.makeText(MainActivity.this, "长按了第" + position + "条", Toast.LENGTH_SHORT).show();
        //mAdaptor.removeData(position);
    }
    public void fetchFeed(String student_id) {
        DataSet result=new DataSet();
        MiniTikTok TikTokService = retrofit.create(MiniTikTok.class);
        TikTokService.getVideos(student_id,TOKEN).enqueue(new Callback<GetVideosResponse>() {
            @Override
            public void onResponse(Call<GetVideosResponse> call, Response<GetVideosResponse> response) {
                List<VideoData> Videos = new ArrayList<>();
                Videos.clear();
                if (response.body() != null && response.body().videos != null) {
                    Videos = response.body().videos;
                    sameperson.setText("TA的视频 ("+Videos.size()+")");
                    for (VideoData video : Videos){
                        //Log.i("student_id",video.studentId+"/"+result.getAmount());
                        result.addData(video);
                    }
                    mAdaptor = new MyAdaptor(result.getData());
                    //设置Adaptor每个item的点击事件
                    //设置Adaptor
                    mAdaptor.setOnItemClickListener(VideoplayActivity.this);
                    recyclerView.setAdapter(mAdaptor);
//                    /*Adapter 刷新 */
//                    mAdaptor = new MyAdaptor(mDataset.getData());
//                    // mRv 是我这里的recyclerview 需要相应改动
//                    recyclerView.setAdapter(mAdaptor);
                }
            }

            @Override
            public void onFailure(Call<GetVideosResponse> call, Throwable throwable) {
                Toast.makeText(VideoplayActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        mAdaptor = new MyAdaptor(result.getData());
        //设置Adaptor每个item的点击事件
        //设置Adaptor
        mAdaptor.setOnItemClickListener(VideoplayActivity.this);
        recyclerView.setAdapter(mAdaptor);
    }

}
