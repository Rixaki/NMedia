package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.funcs.countToString
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel by viewModels<PostViewModel>()
        viewModel.data.observe(this) { post ->
            with(binding) {
                author.text = post.author
                publishedTime.text = post.published
                content.text = post.content
                likeIv.setImageResource(
                    if (post.likedByMe) R.drawable.ic_baseline_thumb_up_24pd
                    else R.drawable.baseline_thumb_up_off_alt_24dp
                )
                likeCount.text = countToString(post.likes)
                shareCount.text = countToString(post.shares)
            }
        }

        binding.likeIv.setOnClickListener {
            viewModel.like()
        }

        with(binding) {
            shareIv.setOnClickListener {
                viewModel.share()
                shareIv.setImageResource(R.drawable.baseline_share_blue_24dp)

                GlobalScope.launch {
                    delay(1500) // In ms
                    //Code after sleep
                    shareIv.setImageResource(R.drawable.ic_baseline_share_24dp)
                }
            }
        }
    }
}