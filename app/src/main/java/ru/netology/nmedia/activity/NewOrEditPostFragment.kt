package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewOrEditPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

//class NewOrEditPostActivity() : AppCompatActivity() {
class NewOrEditPostFragment() : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
        //"by StringArg" instead of:
        //get() = getString(KEY_TEXT)
        //set(value)= putString(KEY_TEXT, value)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel by activityViewModels<PostViewModel>()
        //vm by viewModels exists in 1 fr-nt, vm by actVMs exists in 1 act-ty
        //another script - link vm to parent fragment

        val binding = FragmentNewOrEditPostBinding.inflate(
            layoutInflater,
            container,
            false
        )
        //container - for link with to-point in navigation, correct sizes

        //val bundle: Bundle? = intent.extras
        //val startText = bundle?.getString("content")

        //binding.content.setText(startText)

        /*
        with (binding.content) {
            setText(arguments?.textArg.orEmpty())
            requestFocus()
        }
         */
        binding.content.setText(arguments?.textArg.orEmpty())
        if (arguments?.textArg.isNullOrEmpty()) {
            binding.content.setText(savedInstanceState?.getString("textArg"))
        }
        binding.content.requestFocus()

        binding.save.setOnClickListener {
            val text = binding.content.text.toString()

            if (text.isBlank()) {
                //setResult(Activity.RESULT_CANCELED, Intent())
            } else {
                viewModel.changeContentAndSave(text)
                //setResult(RESULT_OK, Intent().putExtra(Intent.EXTRA_TEXT, text))
            }
            //finish()
            //findNavController().navigateUp()//instead of finish(), make removing to 1 activity back
        }

        binding.cancelButton.setOnClickListener {
            viewModel.cancelEdit()
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            //viewModel.load()
            AndroidUtils.hideKeyBoard(requireView())
            findNavController().navigateUp()
        }

        viewModel.postCanceled.observe(viewLifecycleOwner) {
            AndroidUtils.hideKeyBoard(requireView())
            findNavController().navigateUp()
        }

        return binding.root
    }
}