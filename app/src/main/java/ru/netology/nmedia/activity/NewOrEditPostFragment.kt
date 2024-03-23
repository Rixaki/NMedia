package ru.netology.nmedia.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewOrEditPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

//class NewOrEditPostActivity() : AppCompatActivity() {
@AndroidEntryPoint
class NewOrEditPostFragment() : Fragment() {
    /*
    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

     */
    private val viewModel: PostViewModel by activityViewModels()
    //vm by viewModels exists in 1 fr-nt, vm by actVMs exists in 1 act-ty
    //another script - link vm to parent fragment

    companion object {
        var Bundle.textArg: String? by StringArg
        //"by StringArg" instead of:
        //get() = getString(KEY_TEXT)
        //set(value)= putString(KEY_TEXT, value)
    }

    //private val dependencyContainer = DependencyContainer.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                Toast.makeText(
                    requireContext(),
                    getString(R.string.newAndEdit_toast_empty),
                    Toast.LENGTH_LONG
                ).show()
                findNavController().navigateUp()
                //setResult(Activity.RESULT_CANCELED, Intent())
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.newAndEdit_toast_request),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.changeContentAndSave(text)
                //setResult(RESULT_OK, Intent().putExtra(Intent.EXTRA_TEXT, text))
            }
            //finish()
            //findNavController().navigateUp()//instead of finish(), make removing to 1 activity back
        }

        binding.cancelButton.setOnClickListener {
            viewModel.cancelEdit()
        }

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        viewModel.changePhoto(uri, uri?.toFile())
                    }
                }
            }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.CAMERA)
                .createIntent(pickPhotoLauncher::launch)
        }


        binding.gallery.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.clearPhoto.setOnClickListener {
            viewModel.clearPhoto()
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            try {
                val uri = it.uri//throwable with NullPointException

                if (it.uri != null) {
                    binding.newAttachmentMedia.isVisible = true
                    binding.clearPhoto.isVisible = true
                    binding.newAttachmentMedia.setImageURI(it.uri)
                }
            } catch (e: NullPointerException) {
                binding.newAttachmentMedia.visibility = View.GONE
                binding.clearPhoto.visibility = View.GONE
                return@observe
            }
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            //viewModel.load()
            AndroidUtils.hideKeyBoard(requireView())
            Toast.makeText(
                requireContext(),
                getString(R.string.action_saved),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigateUp()
        }

        viewModel.postCanceled.observe(viewLifecycleOwner) {
            AndroidUtils.hideKeyBoard(requireView())
            findNavController().navigateUp()
        }

        return binding.root
    }
}