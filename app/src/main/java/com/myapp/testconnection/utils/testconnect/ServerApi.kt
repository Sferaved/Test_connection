package com.myapp.testconnection.utils.testconnect

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface ServerApi {
    @GET
    fun checkConnection(@Url url: String): Call<TestResponse>
}
