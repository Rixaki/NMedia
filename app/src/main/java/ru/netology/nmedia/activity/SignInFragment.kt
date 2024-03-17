package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.viewmodel.SignViewModel


class SignInFragment : Fragment() {
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

    @SuppressLint("ResourceType", "StringFormatMatches")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val signModel by activityViewModels<SignViewModel>()
        signModel.clearResponse()

        val binding = FragmentSignInBinding
            .inflate(layoutInflater, container, false)

        binding.btnLogin.setOnClickListener {
            val login = binding.txtLogin.text.toString()
            val pass = binding.txtPassword.text.toString()

            if (login.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.login_toast_empty),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.login_toast_request),
                    Toast.LENGTH_LONG
                ).show()

                signModel.login(login, pass)
            }
        }

        binding.signUpHintButton.setOnClickListener {
            findNavController().navigate(R.id.action_global_to_signUpFragment)
        }

        signModel.response.asLiveData().observe(viewLifecycleOwner) { response ->
            if (response.isSuccess) {
                val id = response.getOrNull()?.id
                Toast.makeText(
                    requireContext(),
                    getString(R.string.login_toast_success, id),
                    Toast.LENGTH_LONG
                ).show()
                findNavController().navigateUp()
            } else {
                val errorMsg =
                    response.exceptionOrNull()?.message
                        ?: "no detected error"
                if (errorMsg != "Initial value") {
                    Toast.makeText(
                        requireContext(),
                        getString(
                            R.string.login_toast_unsuccess,
                            errorMsg
                        ),
                        Toast.LENGTH_LONG
                    ).show()
                }
                binding.txtPassword.setText(null)
            }
        }


        return binding.root
    }
}

