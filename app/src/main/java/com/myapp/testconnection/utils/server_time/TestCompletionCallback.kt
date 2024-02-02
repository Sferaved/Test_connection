package com.myapp.testconnection.utils.server_time

interface TestCompletionCallback {
    fun onTestComplete(url: String, dateTimeNowUtc: String, duration: Long)
}
