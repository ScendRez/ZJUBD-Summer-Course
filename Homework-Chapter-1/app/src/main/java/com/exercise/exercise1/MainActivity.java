package com.exercise.exercise1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Button;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.Date;


public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    boolean is_hot=false,is_done;
    long loadStartTime;
    long currentTime;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Switch sweatsw = (Switch) findViewById(R.id.sw_sweat);
        final ImageView iv = (ImageView) findViewById(R.id.iv_face);
        final TextView tv = (TextView) findViewById(R.id.tv_1);
        final ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        sweatsw.setChecked(false);
        sweatsw.setOnCheckedChangeListener(this);
        Button btn = (Button) this.findViewById(R.id.btn_saysth);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setProgress(is_hot?100:0);
                tv.setText(is_hot ? R.string.word_hot : R.string.word_cool);
                iv.setImageResource(is_hot ? R.drawable.grinning_face_with_sweat : R.drawable.grinning_face_with_smiling_eyes);

            }
        });
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean b) {
        final ImageView iv = (ImageView) findViewById(R.id.iv_face);
        final TextView tv = (TextView) findViewById(R.id.tv_1);
        final ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        switch (buttonView.getId()) {
            case R.id.sw_sweat:
                if (b) {
                    is_hot=true;
                } else {
                    is_hot = false;
                }
                pb.setProgress(is_hot?100:0);
                tv.setText(R.string.word_think);
                iv.setImageResource(R.drawable.thinking_face);
                break;
            default:
                break;
        }
    }
}
