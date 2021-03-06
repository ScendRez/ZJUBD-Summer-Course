package com.exercise.aliali;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetVideosResponse {

    @SerializedName("success") public boolean success;
    @SerializedName("feeds") public List<VideoData> videos;
    @SerializedName("error") public String error;

    @Override
    public String toString() {
        return "success=" + success +
                ", error=" + error +
                '}';
    }
}
