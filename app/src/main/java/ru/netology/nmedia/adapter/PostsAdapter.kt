package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.funcs.countToString
import ru.netology.nmedia.util.load


interface OnIterationListener {
    fun onLikeLtn(post: Post) {}
    fun onShareLtn(post: Post) {}
    fun onEditLtn(post: Post) {}
    fun onRemoveLtn(post: Post) {}
    fun onPlayVideoLtn(post: Post) {}
    fun onRootLtn(post: Post) {}
    fun onReuploadLtn(post: Post) {}
}

class PostsAdapter(
    private val onIterationListener: OnIterationListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallBack) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        val view = FragmentPostBinding.inflate(
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
    //private val binding: FragmentPostInScrollviewBinding,
    private val binding: FragmentPostBinding,
    private val onIterationListener: OnIterationListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        with(binding) {
            author.text = post.author
            publishedTime.text = post.published.toString()
            cardContent.text = post.content

            unSaved.visibility = if (post.isSaved) View.GONE else View.VISIBLE
            unSaved.setOnClickListener {
                onIterationListener.onReuploadLtn(post)
            }

            val baseAvaUrl = "http://10.0.2.2:9999/avatars/"
            avatar.load(
                url = baseAvaUrl + post.authorAvatar,
                placeholderIndex = R.drawable.baseline_account_circle_48,
                options = RequestOptions().circleCrop()
            )

            likeIv.isChecked = post.likedByMe
            likeIv.text = countToString(post.likes)

            shareIv.text = countToString(post.shares)
            viewIv.text = countToString(12)

            likeIv.setOnClickListener {
                //likeIv.isChecked = !post.likedByMe
                likeIv.text =
                    countToString(post.likes + (if (post.likedByMe) -1 else 1))
                onIterationListener.onLikeLtn(post)
            }

            shareIv.setOnClickListener {
                onIterationListener.onShareLtn(post)
            }

            if (post.attachment != null) {
                val baseAttUrl = "http://10.0.2.2:9999/images/"
                attachmentIv.visibility = View.VISIBLE

                with(post.attachment) {
                    attachmentIv.load(
                        url = baseAttUrl + this.url,
                        options = RequestOptions().fitCenter(),
                        toFullWidth = true
                    )
                    attachmentIv.contentDescription = this.description
                }
            } else {
                attachmentIv.visibility = View.GONE
            }

            videoWallpaper.setOnClickListener {
                onIterationListener.onPlayVideoLtn(post)
            }

            playVideo.setOnClickListener {
                onIterationListener.onPlayVideoLtn(post)
            }

            if (!post.video.isNullOrBlank()) {
                videoGroup.visibility = View.VISIBLE
            } else {
                videoGroup.visibility = View.GONE
            }

            postConstrainLayout.setOnClickListener {
                onIterationListener.onRootLtn(post)
            }

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
                            }

                            else -> false
                        }
                    }
                }.show()
            }
        }//with binding
    }

    /*
    likeIv.setOnClickListener {
        onIterationListener.onLikeLtn(post)
        if (!post.likedByMe) {
            //likeCount.setTextColor(0xFF0000FF.toInt())
            GlobalScope.launch {
                delay(1500) // In ms
                //Code after sleep
                //likeCount.setTextColor(0xFF777777.toInt())
            }
        }
    }
    */
}

object PostDiffCallBack :
    DiffUtil.ItemCallback<Post>() { //object without data better that class without data
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
        (oldItem.id == newItem.id)

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
        (oldItem == newItem)
    /*
    override fun getChangePayload(oldItem: Post, newItem: Post): Any? { //for anti-flick view
        return super.getChangePayload(oldItem, newItem)
    }
    */
}
