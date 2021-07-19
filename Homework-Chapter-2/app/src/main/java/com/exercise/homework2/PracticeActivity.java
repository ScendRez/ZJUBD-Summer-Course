package com.exercise.homework2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class PracticeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        Log.i("PracticeActivity","onCreate");
        ImageButton btn_touch=findViewById(R.id.ibtn_touchfish);
        btn_touch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(PracticeActivity.this,"摸鱼成功！！",Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.i("PracticeActivity","onStart");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.i("PracticeActivity","onResume");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.i("PracticeActivity","onPause");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.i("PracticeActivity","onStop");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.i("PracticeActivity","onDestroy");
    }
}
