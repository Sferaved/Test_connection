package com.myapp.testconnection.utils.testconnect

import com.google.gson.annotations.SerializedName

data class TestResponse(
    @SerializedName("test")
    val test: String
)

