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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
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

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val authModel by activityViewModels<SignViewModel>()

        val binding = FragmentSignInBinding
            .inflate(layoutInflater, container, false)

        binding.btnLogin.setOnClickListener {
            val login = binding.txtLogin.text.toString()
            val pass = binding.txtPassword.text.toString()

            if (login.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Login must not be empty.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Request for login...",
                    Toast.LENGTH_LONG
                ).show()
                lifecycleScope.launch(SupervisorJob()) {
                    val response = authModel.login(login, pass)
                    val id = response.id
                    val token = response.token
                    //avatarUrl field save error info in exception case
                    val avatar = response.avatarUrl
                    if (id != 0L && token != null) {
                        AppAuth.getInstance().removeAuth()
                        AppAuth.getInstance().setAuth(id, token)
                        Toast.makeText(
                            requireContext(),
                            "Login successes as user with id=${id}.",
                            Toast.LENGTH_LONG
                        ).show()
                        findNavController().navigateUp()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Login and password didn`t match. Error log - $avatar",
                            Toast.LENGTH_LONG
                        ).show()
                        binding.txtPassword.setText(null)
                    }
                }
            }
        }

        binding.signUpHintButton.setOnClickListener {
            findNavController().navigate(R.id.action_global_to_signUpFragment)
        }

        return binding.root
    }
}

