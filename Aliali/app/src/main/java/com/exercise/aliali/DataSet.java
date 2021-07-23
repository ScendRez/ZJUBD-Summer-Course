package com.exercise.aliali;

import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;
public class DataSet {


    public static VideoData defaultVd;
    public List<VideoData> result = new ArrayList();
    public List<VideoData> getData() {
        return result;
    }
    public void addData(VideoData vd)
    {
        result.add(vd);
    }
    public static VideoData getVideoData() {
        return defaultVd;
    }
    public int getAmount(){return result.size();}
}
