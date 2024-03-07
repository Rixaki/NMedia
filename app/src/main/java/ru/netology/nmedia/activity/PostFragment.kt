package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.AttachmentFragment.Companion.urlArg
import ru.netology.nmedia.activity.NewOrEditPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnIterationListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.LongArg
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel


class PostFragment : Fragment() {

    companion object {
        var Bundle.longArg: Long by LongArg
        //"by LongArg" instead of:
        //get() = getString(key)
        //set(value)= putString(key, value)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: PostViewModel by activityViewModels()
        //println("vm - $viewModel")

        val authModel by viewModels<AuthViewModel>()

        val binding =
            FragmentPostBinding.inflate(layoutInflater, container, false)

        val id = requireArguments().longArg ?: 0L

        //val adapter = PostsAdapter(object : OnIterationListener {
        val viewHolder = PostViewHolder(binding, object : OnIterationListener {
            @SuppressLint("ResourceType")
            override fun onLikeLtn(post: Post) {
                if (authModel.authenticated) {
                    if (post.likedByMe) {
                        viewModel.unLikeById(post.id)
                    } else {
                        viewModel.likeById(post.id)
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Sign In for like post.",
                        Toast.LENGTH_LONG
                    ).show()
                    findNavController().navigate(R.id.action_global_to_signInFragment)
                }
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
                //viewModel.shareById(post.id)
                startActivity(chooserIntent)
            }

            override fun onEditLtn(post: Post) {
                viewModel.edit(post)
                //editPostContract.launch(post.content)
                findNavController().navigate(
                    R.id.action_postFragment_to_newOrEditPostFragment,
                    Bundle().apply {
                        textArg = post.content
                    })
            }

            override fun onRemoveLtn(post: Post) {
                viewModel.removeById(post.id)
                findNavController().navigateUp()
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

            override fun onAttachmentLtn(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_attachmentFragment,
                    Bundle().apply {
                        if (post.attachment != null) {
                            urlArg = post.attachment.url
                        }
                    })
            }

            override fun onReuploadLtn(post: Post) {
                viewModel.saveLocal(post.id)
            }
        })// val viewHolder

        val posts = viewModel.data.value?.posts
        val post: Post? = posts?.find { it.id == id }
        if (post == null) {
            findNavController().navigateUp()
        } else {
            viewHolder.bind(post)
        }

        val startForProfileImageResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                val resultCode = result.resultCode
                val data = result.data

                if (resultCode == Activity.RESULT_OK) {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!

                    binding.attachmentIv.setImageURI(fileUri)
                } else if (resultCode == ImagePicker.RESULT_ERROR) {
                    Snackbar.make(
                        binding.root,
                        ImagePicker.getError(data),
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    Snackbar.make(
                        binding.root,
                        "Task Cancelled",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

        /*
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val hasNewPost: Boolean = adapter.currentList.size < posts.size
            adapter.submitList(posts) {// update
                if (hasNewPost) {
                    binding.list.smoothScrollToPosition(0)//submitlist is ansync!!!
                }
            }
        }
        */

        return binding.root
    }//onCreateView
}