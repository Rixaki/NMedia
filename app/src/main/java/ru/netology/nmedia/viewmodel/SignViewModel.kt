package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.AppApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.auth.Login
import ru.netology.nmedia.auth.Registration
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.error.ApiError
import javax.inject.Inject

@HiltViewModel
class SignViewModel @Inject constructor(
    private val appAuth: AppAuth,
    private val appApi: AppApi
) : ViewModel() {

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
        appAuth.setAuth(
            id = authState.id,
            token = authState.token,
            avatar = authState.avatarUrl
        )
    }

    /*
    fun clearAuth() {
        privateAuth.value = noAuth
    }
     */

    fun login(login: String, pass: String) : Unit {
        viewModelScope.launch(SupervisorJob()) {
            privateResponse.value = Login(appApi).login(login, pass)
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
        viewModelScope.launch() {
            privateResponse.value =
                Registration(appApi).register(login, pass, name, uploadAvatar)
            //retrofit support flow switching for api requests
            //viewModelScope include superjob+dispatcher.main
        }
    }
}