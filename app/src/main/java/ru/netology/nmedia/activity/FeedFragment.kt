package ru.netology.nmedia.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewOrEditPostFragment.Companion.textArg
import ru.netology.nmedia.activity.PostFragment.Companion.longArg
import ru.netology.nmedia.adapter.OnIterationListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel


//class MainActivity : AppCompatActivity() {

class FeedFragment : Fragment() {

    /*
    companion object {
        var Bundle.textArg: String? by StringArg
        //"by StringArg" instead of:
        //get() = getString(KEY_TEXT)
        //set(value)= putString(KEY_TEXT, value)
    }
    */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            FragmentFeedBinding.inflate(layoutInflater, container, false)

        val viewModel: PostViewModel by activityViewModels()

        binding.newPostButton.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newOrEditPostFragment)
        }

        val adapter = PostsAdapter(object : OnIterationListener {
            override fun onLikeLtn(post: Post) {
                if (post.likedByMe) {
                    viewModel.unLikeById(post.id)
                } else {
                    viewModel.likeById(post.id)
                }
            }

            override fun onShareLtn(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                intent.putExtra("postId", post.id.toLong())

                val chooserIntent = Intent.createChooser(
                    intent,
                    null
                )//ACTION_SEND have not optional title
                startActivity(chooserIntent)
            }

            override fun onEditLtn(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_newOrEditPostFragment,
                    Bundle().apply {
                        textArg = post.content
                    })
            }

            override fun onReuploadLtn(post: Post) {
                viewModel.saveLocal(post.id)
            }

            override fun onRemoveLtn(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onPlayVideoLtn(post: Post) {
                if (!post.video.isNullOrBlank()) {
                    val url = post.video
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    try {
                        startActivity(intent)
                    } catch (ex: ActivityNotFoundException) {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.video_play_error),
                            Snackbar.LENGTH_SHORT
                        ).show()
                        onPlayVideoLtn@ return
                    }
                }
            }

            override fun onRootLtn(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_postFragment,
                    Bundle().apply {
                        longArg = post.id
                    })
            }
        })

        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { feedModel ->
            val hasNewPost: Boolean =
                (adapter.currentList.size < feedModel.posts.size
                        || kotlin.math.abs(adapter.currentList.size - feedModel.posts.size) == 1)
                        && adapter.itemCount > 0
            adapter.submitList(feedModel.posts)

            binding.emptyText.isVisible = feedModel.empty
            if (hasNewPost) {
                binding.list.smoothScrollToPosition(0)//submitlist is ansync!!!
            }
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.statusText.clearAnimation()
            binding.statusText.visibility = View.VISIBLE
            if (state.loading) {
                binding.statusText.animate().alpha(1.0f)//visible
            } else {
                binding.statusText.animate().alpha(0.0f)//vanish
            }
            binding.progress.isVisible = state.loading
            if (state.error) {
                val snackbar = Snackbar.make(
                    binding.root,
                    getString(R.string.error_bar_start_text) + state.lastErrorAction,
                    10_000//milliseconds
                )
                snackbar
                    .setTextMaxLines(3)
                    .setAction("OK") {
                        snackbar.dismiss()
                    }
                    .show()

            }
            binding.swiperefresh.isRefreshing = state.refreshing
        }

        binding.swiperefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        return binding.root
    }//onCreateView
}