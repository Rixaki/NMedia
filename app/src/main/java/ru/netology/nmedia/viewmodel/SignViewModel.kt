package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.auth.Login
import ru.netology.nmedia.auth.Registration
import ru.netology.nmedia.dto.MediaUpload

class SignViewModel(application: Application) : AndroidViewModel(application) {

    private val noAuth = AuthState()

    private val privateAuth = MutableStateFlow(noAuth)
    val auth: StateFlow<AuthState>
        get() = privateAuth.asStateFlow()

    fun changeAuth(authState: AuthState) {
        privateAuth.value = authState
    }

    fun clearAuth() {
        privateAuth.value = noAuth
    }

    suspend fun login(login: String, pass: String): AuthState {
        return Login.login(login, pass)
    }

    suspend fun register(
        login: String,
        pass: String,
        name: String,
        uploadAvatar: MediaUpload?
    ): AuthState {
        return Registration.register(login, pass, name, uploadAvatar)
    }
}