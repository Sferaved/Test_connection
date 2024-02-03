package com.myapp.testconnection

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.myapp.testconnection.data.ServerData
import com.myapp.testconnection.data.urls
import com.myapp.testconnection.ui.theme.TestConnectionTheme
import com.myapp.testconnection.utils.server_time.ApiClient
import com.myapp.testconnection.utils.server_time.ServerTimeResponse
import com.myapp.testconnection.utils.server_time.TestCompletionCallback
import kotlinx.coroutines.delay
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestConnectionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TestResultsScreen()
                }
            }
        }
    }
}

@Composable
fun ServerIcon() {
    Image(
        modifier = Modifier
            .size(dimensionResource(id = R.dimen.image_size))
            .padding(dimensionResource(id = R.dimen.padding_small)),
        painter = painterResource(id = R.drawable.bullet_2157465),
        contentDescription = null
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestConnectionTopAppBar(
    modifier: Modifier = Modifier,
    isChecking: Boolean
) {

    CenterAlignedTopAppBar(
        title = {
            Column {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                ) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.displayLarge.copy(textAlign = TextAlign.Center)
                    )
                }
                if (isChecking) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .padding(4.dp),
                        color = Color.Green,
                        trackColor = Color.Red,
                        strokeCap = StrokeCap.Butt
                    )
                }
            }


        },
        modifier = modifier
    )
}

@Composable
private fun ItemButton(
    expanded: Boolean,
    onClick: ()->Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = stringResource(id = R.string.expand_button_content_description),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}
@Composable
private fun serverInfo(
    serverData: ServerData,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier
    ) {
        Text(
            text = if (serverData.duration.toInt() == 10000) {
                " "
            } else {
                "Время Utc: ${serverData.dateTimeNowUtc}"
            },
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = if (serverData.duration.toInt() == 10000) {
                "Скорость подключения (мс) >10000"
            } else {
                "Скорость подключения (мс): ${serverData.duration}"
            },
            style = MaterialTheme.typography.bodyLarge
        )


    }
}


private fun beginTest(url: String, callback: TestCompletionCallback, TAG: String?) {
    val startTime = System.currentTimeMillis()
    val apiService = ApiClient.getApiService(url)
    Log.d(TAG, "TestConnectServers: ")
    val call: Call<ResponseBody> = apiService.getServerTimeInfo()

    call.enqueue(object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            if (response.isSuccessful) {
                try {
                    val responseBodyString = response.body()?.string()

                    val gson = Gson()
                    val serverTimeResponse = gson.fromJson(responseBodyString, ServerTimeResponse::class.java)

                    val dateTimeNowUtc = serverTimeResponse.datetimeNowUtc

                    Log.d(TAG, "onResponse: DateTimeNowUtc $dateTimeNowUtc")

                    val endTime = System.currentTimeMillis()
                    val duration = endTime - startTime

                    Log.d(TAG, "onResponse:time connection $duration")

                    callback.onTestComplete(url, dateTimeNowUtc, duration)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                callback.onTestComplete(url, "", 10000)
                val errorCode = response.code()
                var errorBody = ""

                try {
                    errorBody = response.errorBody()?.string() ?: ""
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                Log.e(TAG, "onResponse: Unsuccessful request. Code: $errorCode, Body: $errorBody")
            }
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            callback.onTestComplete(url, "", 10000)
            Log.e(TAG, "onFailure: Unexpected error: ${t.message}")

            if (t is HttpException) {
                val errorBody = t.response()?.errorBody()
                try {
                    Log.e(TAG, "onFailure: Error body: ${errorBody?.string()}")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                Log.e(TAG, "onFailure: Non-HttpException error handling")

                if (t.message != null) {
                    Log.e(TAG, "onFailure: Throwable message: ${t.message}")
                }
            }
        }
    })


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestResultsScreen() {
    var serverDataList by remember { mutableStateOf(emptyList<ServerData>()) }
    var isChecking by remember { mutableStateOf(true) } // Флаг для отслеживания статуса проверки


    Scaffold (
        topBar = {
            TestConnectionTopAppBar(
                modifier = Modifier,
                isChecking)
        }
    ){ it->


        LazyColumn(
            contentPadding = it,
            modifier = Modifier
                .background(color = Color.LightGray)
        ) {
            items(serverDataList.sortedByDescending { it.duration }) { serverData ->
                ServerDataItem(
                    serverData = serverData,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
                )
            }
        }

        // Start tests when the screen is created
        LaunchedEffect(Unit) {
            while (true) {
                checking { results ->
                    serverDataList = results
                    isChecking = false
                }
                delay( 15 * 60 * 1000) // Задержка в 2 минуты перед следующей проверкой
            }
        }
    }
}

@Composable
fun ServerDataItem(
    serverData: ServerData,
    modifier: Modifier = Modifier
) {

    var expanded by remember {
        mutableStateOf(false)
    }
    val color by animateColorAsState(
        targetValue = if (expanded) MaterialTheme.colorScheme.tertiaryContainer
        else if (serverData.duration.toInt() == 10000) Color.Red
        else MaterialTheme.colorScheme.primaryContainer,
        label = "",
    )
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                .background(color = color)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        dimensionResource(id = R.dimen.padding_small)
                    )
            ) {
                ServerIcon()
                Text(
                    "URL: ${serverData.url}",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
                )

                Spacer(modifier = Modifier.weight(1f))
                ItemButton(
                    expanded = expanded,
                    onClick = {expanded = !expanded}
                )
            }
            if (expanded){
                serverInfo(
                    serverData,
                    modifier = Modifier.padding(
                        start = dimensionResource(id = R.dimen.padding_medium),
                        top = dimensionResource(id = R.dimen.padding_small),
                        end = dimensionResource(id = R.dimen.padding_medium),
                        bottom = dimensionResource(id = R.dimen.padding_medium)
                    )
                )
            }
        }
    }
}

// Replace the checking function with this updated version
fun checking(callback: (List<ServerData>) -> Unit) {

    val serverDataList = mutableListOf<ServerData>()

    val callbackImpl = object : TestCompletionCallback {
        override fun onTestComplete(url: String, dateTimeNowUtc: String, duration: Long) {
            serverDataList.add(ServerData(url, dateTimeNowUtc, duration))

            // Check if all requests are completed
            if (serverDataList.size == urls.size) {
                // All requests are completed, invoke the callback with the list
                callback(serverDataList)
            }
        }
    }

    // Make requests for each URL
    for (url in urls) {
        beginTest(url, callbackImpl, "TAG_MAIN")
    }
}
