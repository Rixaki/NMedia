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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.viewmodel.PhotoViewModel
import ru.netology.nmedia.viewmodel.SignViewModel

class SignUpFragment : Fragment() {
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

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setMenuVisibility(false)

        val authModel by activityViewModels<SignViewModel>()
        val viewModel by activityViewModels<PhotoViewModel>()

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
                    "Login and name must not be empty.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (pass == confPass) {
                    Toast.makeText(
                        requireContext(),
                        "Request for registration...",
                        Toast.LENGTH_SHORT
                    ).show()
                    lifecycleScope.launch(SupervisorJob()) {
                        val avatarMedia = viewModel.photo.value?.file?.let {
                            file -> MediaUpload(file)
                        }//nullable

                        val response = authModel.register(
                            name = name,
                            login = login,
                            pass = pass,
                            uploadAvatar = avatarMedia
                            )
                        val id = response.id
                        val token = response.token
                        //avatarUrl field save error info in exception case
                        val avatar = response.avatarUrl
                        if (id != 0L && token != null) {
                            Toast.makeText(
                                requireContext(),
                                "Registration successes (id=${id}). You Signed In!",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(R.id.action_global_to_feedFragment)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Registration unsuccessful. Error log - $avatar",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.txtPassword.setText(null)
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Password not match with Confirm password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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