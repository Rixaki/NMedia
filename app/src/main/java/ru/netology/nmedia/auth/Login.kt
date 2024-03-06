package ru.netology.nmedia.auth

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
                    throw ApiError(response.code(), response.message())
                }

                return response.body() ?: throw ApiError(
                    response.code(),
                    response.message()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                when (e) {
                    is IOException -> {
                        throw NetworkError
                    }

                    else -> {
                        throw UnknownError
                    }
                }
            }
        }
    }
}