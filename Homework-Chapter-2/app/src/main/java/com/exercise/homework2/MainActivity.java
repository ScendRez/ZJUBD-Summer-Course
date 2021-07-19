package com.exercise.homework2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_toast=findViewById(R.id.btn_toast);
        Button btn_baidu=findViewById(R.id.btn_baidu);
        Button btn_dial=findViewById(R.id.btn_dial);
        Button btn_jump=findViewById(R.id.btn_practice);
        Button btn_rv=findViewById(R.id.btn_rv);
        btn_toast.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(MainActivity.this,"🍞🍞🍞",Toast.LENGTH_SHORT).show();
            }

        });
        btn_jump.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(MainActivity.this,PracticeActivity.class);
                startActivity(intent);
            }
        });
        btn_rv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(MainActivity.this,Recycler.class);
                startActivity(intent);
            }
        });
        btn_baidu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.baidu.com"));
                startActivity(intent);
            }
        });
        btn_dial.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Intent.ACTION_CALL_BUTTON);
                startActivity(intent);
            }
        });

        Log.i("MainActivity","onCreate");
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.i("MainActivity","onStart");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.i("MainActivity","onResume");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.i("MainActivity","onPause");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.i("MainActivity","onStop");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.i("MainActivity","onDestroy");
    }
}
