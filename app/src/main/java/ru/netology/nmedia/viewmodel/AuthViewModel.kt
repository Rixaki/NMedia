package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.auth.Login
import ru.netology.nmedia.auth.Registration
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.service.FCMService

class AuthViewModel : ViewModel() {
    val data: Flow<AuthState> = AppAuth.getInstance()
        .authState

    val authenticated: Boolean
        get() = AppAuth.getInstance().authState.value.id != 0L
}