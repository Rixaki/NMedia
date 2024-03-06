package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.request.RequestOptions
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentAttachmentPostBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.util.load
import ru.netology.nmedia.viewmodel.PostViewModel

class AttachmentFragment : Fragment() {
    companion object {
        var Bundle.urlArg: String? by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            FragmentAttachmentPostBinding
                .inflate(layoutInflater, container, false)

        val viewModel: PostViewModel by activityViewModels()

        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(
                    menu: Menu,
                    menuInflater: MenuInflater
                ) {
                    menuInflater.inflate(R.menu.attachment_post_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when (menuItem.itemId) {
                        R.id.back_up -> {
                            findNavController().navigateUp()
                            true
                        }

                        else -> false
                    }
            }, viewLifecycleOwner
        )//viewlifecycle owner need for work menu only
        //when this fragment is active

        //TODO: REQUEST URL IMAGE FROM SERVER
        val baseAttUrl = "http://10.0.2.2:9999/media/"
        binding.imageAttachment.load(
            url = baseAttUrl + arguments?.urlArg,
            placeholderIndex = R.drawable.baseline_broken_image_48,
            options = RequestOptions()
        )
        println("url image - ${baseAttUrl + arguments?.urlArg}")

        return binding.root
    }
}