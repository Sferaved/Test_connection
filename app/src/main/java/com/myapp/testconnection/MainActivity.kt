package com.myapp.testconnection

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.myapp.testconnection.data.Dog
import com.myapp.testconnection.data.ServersData
import com.myapp.testconnection.data.dogs
import com.myapp.testconnection.ui.theme.TestConnectionTheme
import com.myapp.testconnection.utils.server_time.ApiClient
import com.myapp.testconnection.utils.server_time.ServerTimeResponse
import com.myapp.testconnection.utils.server_time.TestCompletionCallback
import com.myapp.testconnection.utils.server_time.TestConnectServers
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

/**
 * Composable that displays an app bar and a list of dogs.
 */
@Composable
fun TestConnectionApp() {
    Scaffold (
        topBar = {
            TestConnectionTopAppBar()
        }
    ){ it->
        LazyColumn (contentPadding = it) {
            items(dogs) {
                DogItem(
                    dog = it,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
                )
            }
        }
    }
}

/**
 * Composable that displays a list item containing a dog icon and their information.
 *
 * @param dog contains the data that populates the list item
 * @param modifier modifiers to set to this composable
 */
@Composable
fun DogItem(
    dog: Dog,
    modifier: Modifier = Modifier
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    val color by animateColorAsState(
        targetValue = if (expanded) MaterialTheme.colorScheme.tertiaryContainer
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
                DogIcon(dog.imageResourceId)
                DogInformation(dog.name, dog.age)
                Spacer(modifier = Modifier.weight(1f))
                ItemButton(
                    expanded = expanded,
                    onClick = {expanded = !expanded}
                )
            }
            if (expanded){
                dogHobby(
                    dog.hobbies,
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

/**
 * Composable that displays a photo of a dog.
 *
 * @param dogIcon is the resource ID for the image of the dog
 * @param modifier modifiers to set to this composable
 */
@Composable
fun DogIcon(
    @DrawableRes dogIcon: Int,
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier
            .size(dimensionResource(R.dimen.image_size))
            .padding(dimensionResource(R.dimen.padding_small))
            .clip(MaterialTheme.shapes.small),
        painter = painterResource(dogIcon),
        contentScale = ContentScale.Crop,

        // Content Description is not needed here - image is decorative, and setting a null content
        // description allows accessibility services to skip this element during navigation.

        contentDescription = null
    )
}

/**
 * Composable that displays a dog's name and age.
 *
 * @param dogName is the resource ID for the string of the dog's name
 * @param dogAge is the Int that represents the dog's age
 * @param modifier modifiers to set to this composable
 */
@Composable
fun DogInformation(
    @StringRes dogName: Int,
    dogAge: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(dogName),
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
        )
        Text(
            text = stringResource(R.string.years_old, dogAge),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * Composable that displays what the UI of the app looks like in light theme in the design tab.
 */
@Preview
@Composable
fun TestConnectionPreview() {
    TestConnectionTheme(darkTheme = false) {
        TestConnectionApp()
    }
}

@Preview
@Composable
fun TestConnectionDarkThemePreview() {
    TestConnectionTheme(darkTheme = true) {
        TestConnectionApp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestConnectionTopAppBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.image_size))
                        .padding(dimensionResource(id = R.dimen.padding_small)),
                    painter = painterResource(id = R.drawable.bullet_2157465),
                    contentDescription = null
                )
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.displayLarge
                )
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
private fun dogHobby(
    @StringRes dogHobby: Int,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.about),
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = stringResource(dogHobby),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


data class ServerData(val url: String, val dateTimeNowUtc: String, val duration: Long)

fun checking() {
    val urls = listOf(
        "http://167.235.113.231:7307/",
        "http://167.235.113.231:7306/",
        "http://134.249.181.173:7208",
        "http://91.205.17.153:7208",
        "http://31.43.107.151:7303",
        // Add more URLs as needed
    )

    val serverDataList = mutableListOf<ServerData>()

    val callback = object : TestCompletionCallback {
        override fun onTestComplete(url: String, dateTimeNowUtc: String, duration: Long) {
            // Handle test completion
            serverDataList.add(ServerData(url, dateTimeNowUtc, duration))

            // Check if all requests are completed
            if (serverDataList.size == urls.size) {
                // All requests are completed, do something with the list
                processServerDataList(serverDataList)
            }
        }
    }

    // Make requests for each URL
    for (url in urls) {
        beginTest(url, callback, "TAG_MAIN")
    }

}


private fun processServerDataList(serverDataList: List<ServerData>) {
    // Process the list of ServerData

    for (serverData in serverDataList) {
        Log.d("TAG_MAIN","URL: ${serverData.url}, DateTimeNowUtc: ${serverData.dateTimeNowUtc}, Duration: ${serverData.duration}")
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(title = { Text("Test Results") })

        // Display test results in a LazyColumn
        LazyColumn {
            items(serverDataList) { serverData ->
                ServerDataItem(serverData = serverData)
            }
        }

        // Start tests when the screen is created
        LaunchedEffect(Unit) {
            checking { results ->
                serverDataList = results
            }
        }
    }
}

@Composable
fun ServerDataItem(serverData: ServerData) {
    Column {
        Text("URL: ${serverData.url}")
        Text("DateTimeNowUtc: ${serverData.dateTimeNowUtc}")
        Text("Duration: ${serverData.duration}")
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// Replace the checking function with this updated version
fun checking(callback: (List<ServerData>) -> Unit) {
    val urls = listOf(
        "http://167.235.113.231:7307/",
        "http://167.235.113.231:7306/",
        "http://134.249.181.173:7208",
        "http://91.205.17.153:7208",
        "http://31.43.107.151:7303",
        "http://142.132.213.111:8071",
        "http://142.132.213.111:8072",
        "http://142.132.213.111:8073",
        "http://134.249.181.173:7201",
        "http://31.43.107.151:7303",
    )

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
