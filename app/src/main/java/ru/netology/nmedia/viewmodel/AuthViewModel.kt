package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.auth.Login
import ru.netology.nmedia.auth.Registration
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.service.FCMService

class AuthViewModel : ViewModel() {
    val data: Flow<AuthState> = AppAuth.getInstance()
        .authState

    val authenticated: Boolean
        get() = AppAuth.getInstance().authState.value.id != 0L
}