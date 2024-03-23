package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.viewmodel.PhotoViewModel
import ru.netology.nmedia.viewmodel.SignViewModel

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private val authModel by viewModels<SignViewModel>()
    private val viewModel by viewModels<PhotoViewModel>()

    //for hiding menu item
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this){
                this.isEnabled = true
                findNavController().navigate(R.id.action_global_to_feedFragment)
            }
    }

    @SuppressLint("ResourceType", "StringFormatMatches", "StringFormatInvalid")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setMenuVisibility(false)

        val binding = FragmentSignUpBinding
            .inflate(layoutInflater, container, false)

        binding.signUpButton.setOnClickListener{
            val name = binding.txtName.text.toString()
            val login = binding.txtLogin.text.toString()
            val pass = binding.txtPassword.text.toString()
            val confPass = binding.txtCnfPwd.text.toString()

            if (login.isBlank() || name.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.regi_toast_empty),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (pass == confPass) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.regi_toast_request),
                        Toast.LENGTH_SHORT
                    ).show()
                    val avatarMedia = viewModel.photo.value?.file?.let {
                        file -> MediaUpload(file)
                    }//nullable

                    authModel.register(
                        name = name,
                        login = login,
                        pass = pass,
                        uploadAvatar = avatarMedia
                    ) //result<auth>
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.regi_toast_unmatch),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        authModel.response.asLiveData().observe(viewLifecycleOwner) { response ->
            if (response.isSuccess) {
                val id = response.getOrNull()?.id
                Toast.makeText(
                    requireContext(),
                    getString(R.string.regi_toast_success, id),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_global_to_feedFragment)
            } else {
                val errorMsg =
                    response.exceptionOrNull()?.message ?: "no detected error"
                if (errorMsg != "Initial value") {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.regi_toast_unsuccess, errorMsg),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                binding.txtPassword.setText(null)
            }
        }

        binding.signInHintButton.setOnClickListener {
            findNavController().navigate(R.id.action_global_to_signInFragment)
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

        return binding.root
    }
}