package com.myapp.testconnection.utils.server_time;

import com.google.gson.annotations.SerializedName;

public class ServerTimeResponse {

    @SerializedName("datetime_now_utc")
    private String datetimeNowUtc;

    @SerializedName("datetime_now_unix")
    private long datetimeNowUnix;

    public String getDatetimeNowUtc() {
        return datetimeNowUtc;
    }

    public long getDatetimeNowUnix() {
        return datetimeNowUnix;
    }
}

