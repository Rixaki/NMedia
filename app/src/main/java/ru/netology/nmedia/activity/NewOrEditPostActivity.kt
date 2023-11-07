package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityNewOrEditPostBinding

class NewOrEditPostActivity() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewOrEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle : Bundle? = intent.extras
        val startText = bundle?.getString("input")
        binding.content.setText(startText)
        binding.content.requestFocus()

        binding.save.setOnClickListener {
            val postId = binding.content.id
            val text = binding.content.text.toString()

            if (text.isBlank()) {
            } else {
                setResult(RESULT_OK, Intent().putExtra(Intent.EXTRA_TEXT, text))
            }

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