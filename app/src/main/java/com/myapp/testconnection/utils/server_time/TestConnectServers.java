package com.myapp.testconnection.utils.server_time;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.myapp.testconnection.data.ServersData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class TestConnectServers {
    private String TAG = "TAG_Test connection";
    public String dateTimeNowUtc;  // Переменная для хранения dateTimeNowUtc
    public long duration;          // Переменная для хранения duration

    public interface TestCompletionCallback {
        void onTestComplete(String dateTimeNowUtc, long duration);
    }

    public TestConnectServers(String url, TestCompletionCallback callback) {
        beginTest(url, callback);
    }

    private void beginTest(String url, TestCompletionCallback callback) {
        long startTime = System.currentTimeMillis();
        ApiService apiService = ApiClient.getApiService(url);
        Log.d(TAG, "TestConnectServers: ");
        Call<ResponseBody> call = apiService.getServerTimeInfo(); // Используем ResponseBody вместо ServerTimeInfo
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Получаем тело ответа
                        String responseBodyString = response.body().string();

                        // Используем библиотеку для парсинга JSON (например, Gson)
                        Gson gson = new Gson();
                        ServerTimeResponse serverTimeResponse = gson.fromJson(responseBodyString, ServerTimeResponse.class);

                        // Теперь у вас есть доступ к значениям
                        String dateTimeNowUtc = serverTimeResponse.getDatetimeNowUtc();


                        Log.d(TAG, "onResponse: DateTimeNowUtc " + dateTimeNowUtc);

                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startTime;

                        Log.d(TAG, "onResponse:time connection " + duration) ;
                        processTimeData(dateTimeNowUtc, duration);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Обработка неудачного запроса
                    int errorCode = response.code();
                    String errorBody = "";

                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.e(TAG, "onResponse: Unsuccessful request. Code: " + errorCode + ", Body: " + errorBody);
                }
            }


            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                // Обработка ошибки
                Log.e(TAG, "onFailure: Unexpected error: " + t.getMessage());

                if (t instanceof HttpException) {
                    // Добавьте логирование тела ответа только в случае HttpException
                    ResponseBody errorBody = ((HttpException) t).response().errorBody();
                    try {
                        Log.e(TAG, "onFailure: Error body: " + errorBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Другие методы обработки ошибок, если не является HttpException
                    Log.e(TAG, "onFailure: Non-HttpException error handling");

                    // Добавьте логирование тела ответа
                    if (t.getMessage() != null) {
                        Log.e(TAG, "onFailure: Throwable message: " + t.getMessage());
                    }
                }
            }
        });
        callback.onTestComplete(dateTimeNowUtc, duration);

    }
    private void processTimeData(String dateTimeNowUtc, long duration) {
        // В этом методе вы можете использовать dateTimeNowUtc и duration
        // Например:
        Log.d(TAG, "processTimeData: DateTimeNowUtc in processTimeData: " + dateTimeNowUtc);
        Log.d(TAG, "processTimeData: Duration in processTimeData: " + duration);
        this.dateTimeNowUtc = dateTimeNowUtc;
        this.duration = duration;

        // Далее добавьте свой код обработки временных данных
    }
    // Методы получения значений
    public String getDateTimeNowUtc() {
        return dateTimeNowUtc;
    }

    public long getDuration() {
        return duration;
    }

    // Метод возвращает список ServersData
    public List<ServersData> getServersDataList() {
        List<ServersData> serversDataList = new ArrayList<>();
        serversDataList.add(new ServersData("http://167.235.113.231:7307/", dateTimeNowUtc, duration));
        // Добавьте другие элементы, если необходимо
        return serversDataList;
    }
}

