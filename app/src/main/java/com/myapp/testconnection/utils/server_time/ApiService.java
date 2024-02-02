package com.myapp.testconnection.utils.server_time;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("api/time/")
    Call<ResponseBody> getServerTimeInfo();
}
