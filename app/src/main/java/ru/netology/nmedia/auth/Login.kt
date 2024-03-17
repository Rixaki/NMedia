package ru.netology.nmedia.auth

import android.widget.Toast
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException

class Login {
    companion object {
        suspend fun login(login: String, pass: String): Result<AuthState> {
            try {
                val response = ApiService.service.signIn(
                    login,
                    pass
                )

                println("token: ${response.body()?.token ?: "null"}")

                return response.body()?.let { Result.success(it) } ?: Result.failure(
                    ApiError(
                        response.code(),
                        response.message()
                    )
                )
            } catch (e: Exception) {
                //e.printStackTrace()
                when (e) {
                    is IOException -> {
                        return Result.failure(IOException())
                    }
                    is ApiError -> {
                        return Result.failure(ApiError())
                    }
                    else -> {
                        return Result.failure(UnknownError())
                    }
                }
            }
        }
    }
}