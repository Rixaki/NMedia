package ru.netology.nmedia.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            FragmentFeedBinding.inflate(layoutInflater, container, false)

        val viewModel: PostViewModel by activityViewModels()
        //println("vm - $viewModel")

        binding.newPostButton.setOnClickListener {
            //newPostContract.launch(null)
            findNavController().navigate(R.id.action_feedFragment_to_newOrEditPostFragment)
        }

        val adapter = PostsAdapter(object : OnIterationListener {
            override fun onLikeLtn(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShareLtn(post: Post) {
                //viewModel.shareById(post.id)
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                intent.putExtra("postId", post.id.toLong())

                //val chooserIntent = Intent.createChooser(intent, getString(R.string.chooser_share_post))
                val chooserIntent = Intent.createChooser(
                    intent,
                    null
                )//ACTION_SEND have not optional title
                viewModel.shareById(post.id)
                startActivity(chooserIntent)
            }

            override fun onEditLtn(post: Post) {
                viewModel.edit(post)
                //editPostContract.launch(post.content)
                findNavController().navigate(R.id.action_feedFragment_to_newOrEditPostFragment, Bundle().apply {
                    textArg = post.content
                })
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
                findNavController().navigate(R.id.action_feedFragment_to_postFragment, Bundle().apply {
                    longArg = post.id
                })
            }
        })

        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val hasNewPost: Boolean = adapter.currentList.size < posts.size
            adapter.submitList(posts) {// update
                if (hasNewPost) {
                    binding.list.smoothScrollToPosition(0)//submitlist is ansync!!!
                }
            }
        }
        return binding.root
    }
}