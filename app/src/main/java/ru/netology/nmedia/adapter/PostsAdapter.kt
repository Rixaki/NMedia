package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.compose.ui.window.Popup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.funcs.countToString

interface OnIterationListener {
    fun onLikeLtn (post: Post){}
    fun onShareLtn (post: Post){}
    fun onEditLtn (post: Post){}
    fun onRemoveLtn (post: Post){}
}

class PostsAdapter(
    private val onIterationListener: OnIterationListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallBack) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        val view = CardPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(view, onIterationListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

    /*
    override fun onBindViewHolder(
        holder: PostViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNullOrEmpty()){
            onBindViewHolder(holder, position)
        } else {
            for (pl : payloads) {
                if (pl.id == likeIv) {
                //some set image/animation
                }
                if (pl.id == shareIv) {
                //some set image/animation
                }
            }
        }
    }
     */
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onIterationListener: OnIterationListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        with(binding) {
            author.text = post.author
            publishedTime.text = post.published
            editingContent.text = post.content
            likeIv.setImageResource(
                if (post.likedByMe) R.drawable.ic_baseline_thumb_up_24pd
                else R.drawable.baseline_thumb_up_off_alt_24dp
            )
            likeIv.setOnClickListener {
                onIterationListener.onLikeLtn(post)
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
                onIterationListener.onShareLtn(post)
                shareIv.setImageResource(R.drawable.baseline_share_blue_24dp)
                GlobalScope.launch {
                    delay(1500) // In ms
                    //Code after sleep
                    shareIv.setImageResource(R.drawable.ic_baseline_share_24dp)
                }
            }
            likeCount.text = countToString(post.likes)
            shareCount.text = countToString(post.shares)

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.edit -> {
                                onIterationListener.onEditLtn(post)
                                true
                            }
                            R.id.remove -> {
                                onIterationListener.onRemoveLtn(post)
                                true
                            } else -> false
                        }
                    }
                }.show()
            }
        }
    }
}

object PostDiffCallBack: DiffUtil.ItemCallback<Post>() { //object without data better that class without data
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = (oldItem.id == newItem.id)
    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = (oldItem == newItem)
    /*
    override fun getChangePayload(oldItem: Post, newItem: Post): Any? { //for anti-flick view
        return super.getChangePayload(oldItem, newItem)
    }
    */
}
