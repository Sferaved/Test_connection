package com.myapp.testconnection.utils.testconnect_java;

import android.util.Log;

public class TestConnect {
    String apiKey = "77bb21fd8ee6cbfde9bc5733e01eaf59";
    public TestConnect() {
        this.testConnectionTime("https://api.visicom.ua/data-api/5.0/uk/", apiKey, 1000, new ConnectionSpeedTestCallback() {
            @Override
            public void onConnectionTestResult(boolean isConnectionFast, long duration) {
                Log.d("SpeedTest", "connectionTime: " + duration);
                Log.d("SpeedTest", "testConnectionTime: timeLimitMillis: 1000");
                Log.d("SpeedTest", "testConnectionTime: res: " + isConnectionFast);
                if (!isConnectionFast) {

                }
            }
        });
//        http://167.235.113.231:7307/api/time/
    }

    public void testConnectionTime(String baseUrl, String apiKey, long timeLimitMillis, ConnectionSpeedTestCallback callback) {
        final long[] connectionTime = {0};  // Переменная для хранения времени подключения

        long startTime = System.currentTimeMillis();

        // Убедимся, что baseUrl заканчивается символом /
        if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl + "/";
        }

        ConnectionSpeedTester.testConnectionSpeed(baseUrl, apiKey, new ConnectionSpeedTester.SpeedTestListener() {
            @Override
            public void onSpeedTestCompleted(double speed) {
                long endTime = System.currentTimeMillis();
                connectionTime[0] = endTime - startTime;

                Log.d("SpeedTest", "Скорость подключения: " + speed + " байт/мс");
                Log.d("SpeedTest", "Скорость подключения: " + connectionTime[0]);

                // Здесь вы можете обновить ваш интерфейс или выполнить другие действия

                // Передаем результаты обратно через callback
                boolean isConnectionFast = connectionTime[0] >= 0 && connectionTime[0] <= timeLimitMillis;
                callback.onConnectionTestResult(isConnectionFast, connectionTime[0]);
            }

            @Override
            public void onSpeedTestFailed(String errorMessage) {
                Log.e("SpeedTest", errorMessage);
                // Обработка ошибок, например, вывод сообщения пользователю
                connectionTime[0] = -1;  // Помечаем время подключения как ошибочное

                // Передаем результаты обратно через callback
                callback.onConnectionTestResult(false, connectionTime[0]);
            }
        });
    }
    public interface ConnectionSpeedTestCallback {
        void onConnectionTestResult(boolean isConnectionFast, long duration);
    }

}
