package ru.netology.nmedia.auth

import android.widget.Toast
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException

class Login {
    companion object {
        suspend fun login(login: String, pass: String): AuthState {
            try {
                val response = ApiService.service.signIn(login, pass)
                if (!response.isSuccessful) {
                    //println("code - ${response.code()}")
                    //println("message - ${response.message()}")
                    throw ApiError(response.code(), response.message()) //example: 404
                }

                return response.body() ?: throw ApiError(
                    response.code(),
                    response.message()
                )
            } catch (e: Exception) {
                //e.printStackTrace()
                when (e) {
                    is IOException -> {
                        return AuthState(avatarUrl = "IOException")
                    }
                    is ApiError -> {
                        return AuthState(avatarUrl = "${ApiError().status}")
                    }
                    else -> {
                        return AuthState(avatarUrl = "UnknownError")
                    }
                }
            }
        }
    }
}