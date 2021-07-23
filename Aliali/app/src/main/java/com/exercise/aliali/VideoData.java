package com.exercise.aliali;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;

public class VideoData implements Serializable
{
    @SerializedName("_id")
    public String id="abcdefghijklmnopqrstuvwxyz";
    @SerializedName("student_id")
    public String studentId="00000";
    @SerializedName("user_name")
    public String author="_author";
    @SerializedName("image_url")
    public String imageUrl="https://i1.hdslb.com/bfs/archive/5242750857121e05146d5d5b13a47a2a6dd36e98.jpg@400w_250h_1c.webp";
    @SerializedName("video_url")
    public String videoUrl="https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218114723HDu3hhxqIT.mp4";
    @SerializedName("createdAt")
    public Timestamp createTime;
    @SerializedName("updatedAt")
    public Timestamp updateTime;
    @SerializedName("image_w")
    public int imageW=0;
    @SerializedName("image_h")
    public int imageH=0;

    public String title="_title";

    public void setTitle(String title) {
        this.title = title;
    }

    public VideoData(String title, String uploader)
    {
        this.title=title;
        author=uploader;
    }
}