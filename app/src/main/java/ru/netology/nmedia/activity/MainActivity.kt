package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnIterationListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.AndroidUtils.focusAndShowKeyboard
import ru.netology.nmedia.util.hideKeyBoard
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editBarGroup.visibility = View.GONE

        val viewModel: PostViewModel by viewModels()
        val adapter = PostsAdapter(object : OnIterationListener{
            override fun onLikeLtn(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShareLtn(post: Post) {
                viewModel.shareById(post.id)
            }

            override fun onEditLtn(post: Post) {
                viewModel.edit(post)
            }

            override fun onRemoveLtn(post: Post) {
                viewModel.removeById(post.id)
            }

        })

        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            val hasNewPost: Boolean = adapter.currentList.size < posts.size
            adapter.submitList(posts) {// update
                if (hasNewPost) {
                    binding.list.smoothScrollToPosition(0)//submitlist is ansync!!!
                }
            }
        }

        viewModel.edited.observe(this) {post ->
            if (post.id != 0L) {
                binding.editBarGroup.visibility = View.VISIBLE
                binding.editedHas.text = post.content
                binding.content.setText(post.content)
                binding.content.focusAndShowKeyboard()
            }
        }

        binding.save.setOnClickListener {//it of View
            val text: String = binding.content.text.toString()
            if (text.isBlank()) {
                Toast.makeText(this, R.string.empty_content, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            viewModel.changeContent(text)
            viewModel.save()

            with(binding.content) {
                setText("")
                clearFocus()
            }
            AndroidUtils.hideKeyBoard(it)
            //it.hideKeyBoard()

            /*
            viewModel.edited.observe(this) {post ->
                if (post.id != 0L) {
                    binding.editBarGroup.visibility = View.GONE
                }
            }
            */
            binding.editBarGroup.visibility = View.GONE
        }

        binding.cancelEdit.setOnClickListener {
            with(binding.content) {
                viewModel.cancelEdit()
                setText("")
                clearFocus()
            }
            AndroidUtils.hideKeyBoard(it)
            //it.hideKeyBoard()
            /*
            viewModel.edited.observe(this) {post ->
                if (post.id != 0L) {
                    binding.editBarGroup.visibility = View.GONE
                }
            }
            */
            binding.editBarGroup.visibility = View.GONE
        }
    }
}