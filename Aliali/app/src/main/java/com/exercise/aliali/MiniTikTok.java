package com.exercise.aliali;

import com.exercise.aliali.GetVideosResponse;
import com.exercise.aliali.PostVideoResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface MiniTikTok {
//    String BASE_URL = "https://api-zju-camp-2021.bytedance.com/homework/invoke/";
    String BASE_URL = "https://api-sjtu-camp-2021.bytedance.com/homework/invoke/";

    @Multipart
    @POST("video")
    Call<PostVideoResponse> postVideo(
            @Query("student_id") String studentId,
            @Query("user_name") String userName,
            @Query("extra_value") String extraValue,
            @Part MultipartBody.Part image,
            @Part MultipartBody.Part video,
            @Header("token") String token);

    @GET("video")
    Call<GetVideosResponse> getVideos(@Query("student_id") String studentId,
                                      @Header("token") String token);
}
