package ru.netology.nmedia

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.funcs.countToString

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            published = "21 мая в 18:36",
            content = "Привет, это новая Нетология! " +
                    "Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. " +
                    "Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. " +
                    "Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. " +
                    "Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            likedByMe = false,
            likes = 1114,
            shares = 10
        )
        with(binding) {
            author.text = post.author
            publishedTime.text = post.published
            content.text = post.content
            likeCount.text = countToString(post.likes)

            if (post.likedByMe) {
                likeIv?.setImageResource(R.drawable.ic_baseline_thumb_up_24pd)
            }

            likeIv.setOnClickListener {
                post.likedByMe = !post.likedByMe

                post.likes += if (post.likedByMe) 1 else -1
                likeCount.text = countToString(post.likes)

                likeIv?.setImageResource(
                    if (post.likedByMe) R.drawable.ic_baseline_thumb_up_24pd else R.drawable.baseline_thumb_up_off_alt_24dp
                )
            }

            shareCount.text = countToString(post.shares)

            shareIv.setOnClickListener {
                post.shares += 1
                shareCount.text = countToString(post.shares)
                shareIv.setImageResource(R.drawable.baseline_share_blue_24dp)

                GlobalScope.launch {
                    delay(1500) // In ms
                    //Code after sleep
                    shareIv.setImageResource(R.drawable.ic_baseline_share_24dp)
                }
            }
            /*
            binding.root.setOnClickListener{ println("root") }
            binding.likeIv.setOnClickListener{ println("likeIv")}
            binding.avatar.setOnClickListener{ println("avatar") }
             */
        }
            /*
        setContentView(R.layout.activity_main)
        findViewById<ImageButton>(R.id.likeIv).setOnClickListener{
            println("Click")
        }
        */
            /*
        setContent {
            NMediaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
        */
    }
}

    /*
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NMediaTheme {
        Greeting("Android")
    }
}
     */