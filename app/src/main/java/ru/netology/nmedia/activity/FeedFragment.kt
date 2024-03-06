package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.AttachmentFragment.Companion.urlArg
import ru.netology.nmedia.activity.NewOrEditPostFragment.Companion.textArg
import ru.netology.nmedia.activity.PostFragment.Companion.longArg
import ru.netology.nmedia.adapter.OnIterationListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.funcs.countToString
import ru.netology.nmedia.viewmodel.AuthViewModel
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

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            FragmentFeedBinding.inflate(layoutInflater, container, false)

        val viewModel: PostViewModel by activityViewModels()
        val authModel by viewModels<AuthViewModel>()


        //TODO: WITHOUT addMenuProvider MENU NOT SHOW
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onPrepareMenu(menu: Menu) {
                menu.setGroupVisible(
                    R.id.authenticated,
                    authModel.authenticated
                )
                menu.setGroupVisible(
                    R.id.unauthenticated,
                    !authModel.authenticated
                )
            }

            //TODO: hidden main menu in signIn/signUp fragment
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                //TODO: HOMEWORK with fragment navigations
                return when (menuItem.itemId) {
                    R.id.signIn -> {
                        AppAuth.getInstance().setAuth(5, "x-token")
                        true
                    }

                    R.id.signUp -> {
                        true
                    }

                    R.id.signOut -> {
                        AppAuth.getInstance().removeAuth()
                        true
                    }

                    else -> false
                }
            }

        })//addMenuProvider

        binding.newPostButton.setOnClickListener {
            if (authModel.authenticated) {
                findNavController().navigate(R.id.action_feedFragment_to_newOrEditPostFragment)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Sign In for sending post.",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.layout.fragment_sign_in)
            }
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

            override fun onRemoveLtn(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onCancelDraft(post: Post) {
                viewModel.cancelDraftById(post.id)
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
        })

        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { feedModel ->
            val hasNewPost: Boolean =
                (adapter.currentList.filter { it.isSaved }.size < feedModel.posts.filter { it.isSaved }.size)
                        && adapter.itemCount > 0
            val hasNewDraft: Boolean =
                (adapter.currentList.filter { !it.isSaved }.size < feedModel.posts.filter { !it.isSaved }.size)
                        && adapter.itemCount > 0
            val scrollBlock = {
                if (hasNewPost) {
                    binding.onlySaved.callOnClick()
                    binding.list.smoothScrollToPosition(
                        adapter.currentList.filter { !it.isSaved }.size
                    )//submitlist is ansync!!!
                }
                if (hasNewDraft) {
                    binding.onlyDrafts.callOnClick()
                    binding.list.smoothScrollToPosition(0)//submitlist is ansync!!!
                }
            }
            println("Max id of loaded is ${feedModel.maxId}")
            adapter.submitList(feedModel.posts, scrollBlock)

            binding.emptyText.isVisible = feedModel.empty
            viewModel.newerCount.value
        }

        //INITIAL (onlySaved in postsGroupButton) STATE:
        binding.allPosts.callOnClick()

        //postsGroupButton: singleSelection="true"
        //postsGroupButton: selectionRequired="true"
        binding.postsGroupButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) {
                return@addOnButtonCheckedListener
            }
            when (checkedId) {
                R.id.onlySaved -> {
                    viewModel.onlySavedShow()
                        binding.list.smoothScrollToPosition(0)
                }

                R.id.onlyDrafts -> {
                    viewModel.onlyDraftShow()
                    binding.list.smoothScrollToPosition(0)
                }

                R.id.allPosts -> {
                    viewModel.noFilterShow()
                    binding.list.smoothScrollToPosition(0)
                }
            }
        }

        viewModel.newerCount.observe(viewLifecycleOwner) { count ->
            println(count)
            binding.freshPosts.text =
                getString(R.string.fresh_posts, countToString(count.toLong()))
            binding.freshPosts.visibility =
                if (count == 0) View.GONE else View.VISIBLE
        }

        binding.freshPosts.setOnClickListener {
            println("FRESH POSTS BUTTON CLICKED")
            viewModel.showAllLoad()//all isShown flags in postDao will true
            binding.onlySaved.callOnClick()
            viewModel.newerCount.value = 0//for "Fresh posts" GONE
            //scrolling will got by viewModel.data.observe
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
            viewModel.newerCount.value = 0//for "Fresh posts" GONE
        }

        return binding.root
    }//onCreateView
}