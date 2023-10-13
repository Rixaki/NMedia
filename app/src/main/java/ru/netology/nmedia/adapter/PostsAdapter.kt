package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.funcs.countToString

typealias OnLikeListener = (post: Post) -> Unit
typealias OnShareListener = (post: Post) -> Unit

class PostsAdapter(
    private val likeListener: OnLikeListener,
    private val shareListener: OnShareListener
    ) : RecyclerView.Adapter<PostViewHolder>() {

    var list : List<Post> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        val view = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(view, likeListener, shareListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = list[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = list.size
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val likeListener: OnLikeListener,
    private val shareListener: OnShareListener
    ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
            with(binding) {
                author.text = post.author
                publishedTime.text = post.published
                content.text = post.content
                likeIv.setImageResource(
                    if (post.likedByMe) R.drawable.ic_baseline_thumb_up_24pd
                    else R.drawable.baseline_thumb_up_off_alt_24dp
                )
                likeIv.setOnClickListener {
                    likeListener(post)
                    if (!post.likedByMe) {
                        likeCount.setTextColor(0xFF0000FF.toInt())
                        GlobalScope.launch {
                            delay(1500) // In ms
                            //Code after sleep
                            likeCount.setTextColor(0xFF777777.toInt())
                        }
                    }
                }
                shareIv.setOnClickListener {
                    shareListener(post)
                    shareIv.setImageResource(R.drawable.baseline_share_blue_24dp)
                    GlobalScope.launch {
                        delay(1500) // In ms
                        //Code after sleep
                        shareIv.setImageResource(R.drawable.ic_baseline_share_24dp)
                    }
                }
                likeCount.text = countToString(post.likes)
                shareCount.text = countToString(post.shares)
            }
    }

}