/*
package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.repository.PostRepository

class ViewModelFactory(
    private val repository: PostRepository,
    private val appAuth: AppAuth,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(appAuth) as T
            }
            modelClass.isAssignableFrom(PhotoViewModel::class.java) -> {
                PhotoViewModel() as T
            }
            modelClass.isAssignableFrom(PostViewModel::class.java) -> {
                PostViewModel(repository, appAuth) as T
            }
            modelClass.isAssignableFrom(SignViewModel::class.java) -> {
                SignViewModel(appAuth) as T
            }
            else -> error("Unknown viewmodel class : $modelClass")
        }
}
 */