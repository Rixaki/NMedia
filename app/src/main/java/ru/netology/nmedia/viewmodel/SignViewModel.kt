package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.auth.Login
import ru.netology.nmedia.auth.Registration
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.error.ApiError

class SignViewModel(application: Application) : AndroidViewModel(application) {

    private val noAuth = AuthState()
    private val privateAuth = MutableStateFlow(noAuth)
    val auth: StateFlow<AuthState>
        get() = privateAuth.asStateFlow()

    private val noResponse : Result<AuthState> = Result.failure<AuthState>(
        ApiError(
            code = "Initial value")
    )
    private val privateResponse = MutableStateFlow(noResponse)
    val response: StateFlow<Result<AuthState>>
        get() = privateResponse.asStateFlow()

    fun clearResponse() {
        privateResponse.value = noResponse
    }

    fun changeAuth(authState: AuthState) {
        privateAuth.value = authState
        AppAuth.getInstance().setAuth(
            id = authState.id,
            token = authState.token,
            avatar = authState.avatarUrl
        )
    }

    fun clearAuth() {
        privateAuth.value = noAuth
    }

    fun login(login: String, pass: String) : Unit {
        val deferredResponse: Deferred<Result<AuthState>> =
            CoroutineScope(Dispatchers.Default).async {
                return@async Login.login(login, pass)
            }

        viewModelScope.launch(SupervisorJob()) {
            privateResponse.value = deferredResponse.await()
            val newState = privateResponse.value.getOrNull()
            //println("newstate id ${newState?.id}")
            if (privateResponse.value.isSuccess && newState != null) {
                changeAuth(newState)
            }
        }
    }

    fun register(
        login: String,
        pass: String,
        name: String,
        uploadAvatar: MediaUpload?
    ): Unit {
        val deferredResponse: Deferred<Result<AuthState>> =
            CoroutineScope(Dispatchers.Default).async {
                return@async Registration.register(login, pass, name, uploadAvatar)
            }

        viewModelScope.launch(SupervisorJob()) {
            privateResponse.value = deferredResponse.await()
            //println("value: ${privateResponse.value.getOrNull()}")
        }
    }
}