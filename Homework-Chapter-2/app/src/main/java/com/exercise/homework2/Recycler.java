package com.exercise.homework2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import com.exercise.homework2.DataSet;


public class Recycler extends AppCompatActivity implements MyAdaptor.IOnItemClickListener{

    private static final String TAG = "TAG";

    private RecyclerView recyclerView;
    private MyAdaptor mAdaptor;
    private RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        initView();
    }

    private void initView() {
        //获取实例
        recyclerView = findViewById(R.id.rv);
        //更改数据时不会变更宽高
        recyclerView.setHasFixedSize(true);
        //创建线性布局管理器
        layoutManager = new LinearLayoutManager(this);
        //创建格网布局管理器
        gridLayoutManager = new GridLayoutManager(this, 2);
        //设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //创建Adaptor
        mAdaptor = new MyAdaptor(DataSet.getData());
        //设置Adaptor每个item的点击事件
        mAdaptor.setOnItemClickListener(this);
        //设置Adaptor
        recyclerView.setAdapter(mAdaptor);
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
    public void onItemClick(int position, String data) {
        Toast.makeText(Recycler.this, "点击了第" + position + "条", Toast.LENGTH_SHORT).show();
        mAdaptor.addData(position + 1, new String("别戳了"));
    }

    @Override
    public void onItemLongClick(int position, String data) {
        Toast.makeText(Recycler.this, "长按了第" + position + "条", Toast.LENGTH_SHORT).show();
        mAdaptor.removeData(position);
    }
}


