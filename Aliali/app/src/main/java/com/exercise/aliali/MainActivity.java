package com.exercise.aliali;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.service.autofill.Dataset;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.exercise.aliali.VideoData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MyAdaptor.IOnItemClickListener{

    private final static int PERMISSION_REQUEST_CODE = 1001;
    public static String TOKEN="WkpVLWJ5dGVkYW5jZS1hbmRyb2lk";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.main_title);

        requestPermission();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent=new Intent(MainActivity.this,CustomCameraActivity.class);
                startActivity(intent);
            }
        });
        initView();
    }
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(MiniTikTok.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    MiniTikTok TikTokService = retrofit.create(MiniTikTok.class);
    private static final String TAG = "TAG";

    private VideoData vd=new VideoData("【Rick Astley】Never Gonna Give You Up","Sony中国");
    private VideoData testvd=new VideoData("【全明星】葛炮的消失","吴夕霞");
    private VideoData testvd2=new VideoData("乌兰图雅罕见真唱原key《套马杆》，青年歌唱家实力尽显","草原一吱花");
    private com.exercise.aliali.DataSet mDataset=new com.exercise.aliali.DataSet();
    private RecyclerView recyclerView;
    private MyAdaptor mAdaptor;
    private RecyclerView.LayoutManager layoutManager;
//    private GridLayoutManager gridLayoutManager;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private SwipeRefreshLayout mSwipe;


    private void initView() {
        //获取实例
        mSwipe =  findViewById(R.id.mswipeRefreshLayout);
        setSwipe();
        recyclerView = findViewById(R.id.rv);
        //更改数据时不会变更宽高
        recyclerView.setHasFixedSize(true);
        //创建线性布局管理器
        layoutManager = new LinearLayoutManager(this);
        //创建格网布局管理器
//        gridLayoutManager = new GridLayoutManager(this, 2);
        //设置布局管理器
        staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        //创建Adaptor
        testvd.imageUrl="https://i2.hdslb.com/bfs/archive/551a87d8a0b771845fb43a4122f244eacfedf3db.jpg@400w_250h_1c.webp";
        testvd2.imageUrl="https://i1.hdslb.com/bfs/archive/186e6f137ff1dcda8e4f1af5605a983ec603dcaa.jpg@400w_250h_1c.webp";
//        mDataset.result.add(testvd);
//        mDataset.result.add(testvd2);
//        mDataset.result.add(vd);
        mAdaptor = new MyAdaptor(mDataset.getData());
        recyclerView.setAdapter(mAdaptor);
        mAdaptor.setOnItemClickListener(MainActivity.this);
        fetchFeed(null);
        Log.i("student_id",""+mDataset.getAmount());
        //mAdaptor.addData(0,testvd);
        //分割线
        //LinearItemDecoration itemDecoration = new LinearItemDecoration(Color.BLUE);
//        recyclerView.addItemDecoration(itemDecoration);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //动画
        //DefaultItemAnimator animator = new DefaultItemAnimator();
        //animator.setAddDuration(3000);
        //recyclerView.setItemAnimator(animator);
    }

    @Override
    public void onItemClick(int position, VideoData data) {
//        Toast.makeText(MainActivity.this, "点击了第" + position + "条", Toast.LENGTH_SHORT).show();
        //mAdaptor.addData(position + 1, vd);
        Intent intent=new Intent(MainActivity.this,VideoplayActivity.class);
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
        TikTokService.getVideos(student_id,TOKEN).enqueue(new Callback<GetVideosResponse>() {
            @Override
            public void onResponse(Call<GetVideosResponse> call, Response<GetVideosResponse> response) {
                List<VideoData> Videos = new ArrayList<>();
                Videos.clear();
                if (response.body() != null && response.body().videos != null) {
                    Videos = response.body().videos;
                    for (VideoData video : Videos){
                        //Log.i("student_id",video.studentId+"/"+result.getAmount());
                        result.addData(video);
                    }
                    //mAdaptor.removeAll();
                    mAdaptor.setData(result.result);
                    mSwipe.setRefreshing(false);
                    //设置Adaptor每个item的点击事件
                    //设置Adaptor
                    //mAdaptor.setOnItemClickListener(MainActivity.this);
//                    /*Adapter 刷新 */
//                    mAdaptor = new MyAdaptor(mDataset.getData());
//                    // mRv 是我这里的recyclerview 需要相应改动
//                    recyclerView.setAdapter(mAdaptor);
                }
            }

            @Override
            public void onFailure(Call<GetVideosResponse> call, Throwable throwable) {
                Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //设置Adaptor每个item的点击事件
        //设置Adaptor
    }


    private void requestPermission() {
        boolean hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean hasAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        if (hasCameraPermission && hasAudioPermission) {
            //recordVideo();
        } else {
            List<String> permission = new ArrayList<String>();
            if (!hasCameraPermission) {
                permission.add(Manifest.permission.CAMERA);
            }
            if (!hasAudioPermission) {
                permission.add(Manifest.permission.RECORD_AUDIO);
            }
            ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), PERMISSION_REQUEST_CODE);
        }

    }


    private void setSwipe() {
        mSwipe.setColorSchemeResources(R.color.colorAccent);
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                fetchFeed(null);
            }
        });
    }

}


