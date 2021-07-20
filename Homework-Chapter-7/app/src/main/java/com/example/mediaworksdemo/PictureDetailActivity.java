package com.example.mediaworksdemo;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class PictureDetailActivity extends AppCompatActivity {

    String mockUrl = "https://remywiki.com/images/8/80/GAIA.png";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_detail);
        RoundedCorners rc=new RoundedCorners(20);
        ImageView imageView = findViewById(R.id.iv_detail);

        RequestOptions options=RequestOptions.bitmapTransform(rc).override(1000, 1000);

        Glide.with(this).load(mockUrl)
                .transition(withCrossFade())
                .apply(options)
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.img_loadfailed)
                .fallback(R.drawable.img_loadfailed)
                .into(imageView);
    }
}
