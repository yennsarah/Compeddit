package com.yennsarah.compeddit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageAsset
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.ui.tooling.preview.Preview
import coil.Coil
import coil.request.ImageRequest
import com.yennsarah.compeddit.ui.CompedditTheme
import com.yennsarah.data.RedditApi
import com.yennsarah.data.RedditChildrenResponse
import com.yennsarah.data.RedditNewsDataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val redditApi = RedditApi()
        setContent {
            CompedditTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Content(mainViewModel, redditApi)
                }
            }
        }
    }
}

data class PostState(var image: Bitmap? = null)

@Composable
private fun Content(mainViewModel: MainViewModel, redditApi: RedditApi) {

    ScrollableColumn(modifier = Modifier.fillMaxSize()) {
        val posts: List<RedditChildrenResponse>? by mainViewModel.postList.observeAsState(
            mutableListOf()
        )

        // TODO: Replace with DragToRefresh
        ReloadButton(reload = {
            GlobalScope.launch(Dispatchers.Default) {
                val resp = redditApi.getNews("", "")
                launch(Dispatchers.Main) {
                    mainViewModel.onPostsChanged(resp.data.children)
                }
            }
        })

        posts?.forEach {
            RedditPost(it.data)
        }
    }
}

@Composable
fun RedditPost(post: RedditNewsDataResponse) {
    val context = ContextAmbient.current
    val rememberedState = remember { mutableStateOf(PostState()) }
    val postUrl = post.url

    Card(
        modifier = Modifier.padding(10.dp).then(Modifier.fillMaxWidth()).then(
            Modifier.drawShadow(
                elevation = 4.dp
            )
        )
    ) {
        Column(Modifier.fillMaxWidth()) {
            loadImage(context, postUrl, rememberedState)

            Text(text = post.title, modifier = Modifier.padding(10.dp))

            val imageAsset = rememberedState.value.image?.asImageAsset()
            if (imageAsset != null) {
                Image(asset = imageAsset)
            }
        }
    }
}

@Composable
private fun loadImage(
    context: Context,
    postUrl: String?,
    rememberedState: MutableState<PostState>
) {
    GlobalScope.launch {
        var drawable: Drawable? = null
        Coil.execute(
            ImageRequest.Builder(context)
                .data(postUrl)
                .target(onSuccess = {
                    drawable = it
                    Log.d("loadImage", "image loaded $postUrl")
                }, onError = {
                    drawable = null
                    Log.d("loadImage", "image not loaded $postUrl")
                })
                .listener(object : ImageRequest.Listener {
                    override fun onError(request: ImageRequest, throwable: Throwable) {
                        drawable = null
                        Log.d("loadImage", "Error occurred")
                        // throwable.printStackTrace()
                    }
                })
                .build()
        )
        MainScope().launch {
            rememberedState.value = PostState(drawable?.toBitmap())
        }
    }
}

@Composable
private fun ReloadButton(reload: () -> Unit) {
    Button(onClick = { reload() }, modifier = Modifier.padding(16.dp)) {
        Text(
            "Load",
            style = TextStyle(Color.White)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CompedditTheme {

    }
}