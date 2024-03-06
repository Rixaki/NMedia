package ru.netology.nmedia.auth

import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException

class Registration {
    companion object{
        suspend fun register(
            login: String,
            pass: String,
            name: String,
            uploadAvatar: MediaUpload?
        ): AuthState {
            try {
                val fileAvatar = if (uploadAvatar != null) {
                    MultipartBody.Part.createFormData(
                        "file",
                        uploadAvatar.file.name,
                        uploadAvatar.file.asRequestBody()
                    )
                } else null

                val response =
                    ApiService.service.signUp(login, pass, name, fileAvatar)
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