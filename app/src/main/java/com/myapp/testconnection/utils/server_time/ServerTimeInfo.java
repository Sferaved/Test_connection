package com.myapp.testconnection.utils.server_time;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "ServerTimeInfoContract")
public class ServerTimeInfo {

    @Element(name = "DateTimeNowUtc")
    private String dateTimeNowUtc;

    @Element(name = "DateTimeNowUnix")
    private long dateTimeNowUnix;

    public String getDateTimeNowUtc() {
        return dateTimeNowUtc;
    }

    public long getDateTimeNowUnix() {
        return dateTimeNowUnix;
    }
}
