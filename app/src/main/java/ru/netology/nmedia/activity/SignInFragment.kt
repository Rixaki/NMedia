package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val authModel by activityViewModels<SignViewModel>()

        val binding = FragmentSignInBinding
            .inflate(layoutInflater, container, false)

        binding.btnLogin.setOnClickListener{
            val login = binding.txtLogin.text.toString()
            val pass = binding.txtPassword.text.toString()

            if (login.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Login must not be empty.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Request for login...",
                    Toast.LENGTH_SHORT
                ).show()
                lifecycleScope.launch(SupervisorJob()) {
                    val response = authModel.login(login, pass)
                    val id = response.id
                    val token = response.token
                    if (id != 0L && token != null) {
                        AppAuth.getInstance().removeAuth()
                        AppAuth.getInstance().setAuth(id, token)
                        Toast.makeText(
                            requireContext(),
                            "Login successes (id=${id}).",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigateUp()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Login and password didn`t match.",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.txtPassword.setText(null)
                    }
                }
            }
        }

        binding.signUpHintButton.setOnClickListener {
            findNavController().navigate(R.layout.fragment_sign_up)
        }

        return binding.root
    }
}