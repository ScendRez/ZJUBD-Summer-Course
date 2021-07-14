package com.example.chapter3.homework;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PlaceholderFragment extends Fragment implements MyAdaptor.IOnItemClickListener {
    private static final String TAG = "TAG";

    private RecyclerView recyclerView;
    private MyAdaptor mAdaptor;
    private RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;
    private View view;
    private AnimatorSet animatorSet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO ex3-3: 修改 fragment_placeholder，添加 loading 控件和列表视图控件

        view=inflater.inflate(R.layout.fragment_placeholder, container, false);
        initView(view);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 这里会在 5s 后执行
                // TODO ex3-4：实现动画，将 lottie 控件淡出，列表数据淡入
                ObjectAnimator animatorFadeIn = ObjectAnimator.ofFloat(view.findViewById(R.id.rv2),
                        "alpha",
                        0.0f,
                        1.0f);
                animatorFadeIn.setDuration(500);
                animatorFadeIn.setRepeatCount(0);
                ObjectAnimator animatorFadeOut = ObjectAnimator.ofFloat(view.findViewById(R.id.animation_view2),
                        "alpha",
                        1.0f,
                        0.0f);
                animatorFadeOut.setDuration(500);
                animatorFadeOut.setRepeatCount(0);

                animatorSet = new AnimatorSet();
                animatorSet.playTogether(animatorFadeIn,animatorFadeOut);
                animatorSet.start();
            }
        }, 1000);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initView(View view) {
        //获取实例
        recyclerView = (RecyclerView) view.findViewById(R.id.rv2);
        //更改数据时不会变更宽高
        if(recyclerView==null)Log.i("ddebug","here");
        recyclerView.setHasFixedSize(true);
        //创建线性布局管理器
        layoutManager = new LinearLayoutManager(this.getActivity());
        //创建格网布局管理器
        gridLayoutManager = new GridLayoutManager(this.getActivity(), 2);
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
        Toast.makeText(this.getActivity(), "点击了第" + position + "条", Toast.LENGTH_SHORT).show();
        mAdaptor.addData(position + 1, "别戳了");
    }

    @Override
    public void onItemLongClick(int position, String data) {
        Toast.makeText(this.getActivity(), "长按了第" + position + "条", Toast.LENGTH_SHORT).show();
        mAdaptor.removeData(position);
    }
}
