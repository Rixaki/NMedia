package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityNewOrEditPostBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class NewOrEditPostActivity() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewOrEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()

        val bundle : Bundle? = intent.extras
        val startText = bundle?.getString("content")
        val postId = bundle?.getLong("id")

        binding.content.setText(startText)
        binding.content.requestFocus()

        binding.save.setOnClickListener {
            val text = binding.content.text.toString()

            if (text.isBlank()) {
            } else {
                if(postId == 0L) {
                    setResult(RESULT_OK, Intent().putExtra(Intent.EXTRA_TEXT, text))
                } else {
                    //setResult(RESULT_OK, Intent().putExtra(Intent.ACTION_EDIT, text))
                    viewModel.changeContentAndSave(text)
                    setResult(RESULT_OK)
                }
            }
            viewModel.changeContentAndSave(text)
            finish()
        }

        binding.cancelButton.setOnClickListener {
            viewModel.cancelEdit()
            finish()
        }
    }
}

/*

        viewModel.edited.observe(this) { post ->
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
                Toast.makeText(this, R.string.empty_content, Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }

            viewModel.changeContentAndSave(text)

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
 */