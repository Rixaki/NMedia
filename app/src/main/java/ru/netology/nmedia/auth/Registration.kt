package ru.netology.nmedia.auth

import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.error.ApiError
import java.io.IOException

class Registration {
    companion object{
        suspend fun register(
            login: String,
            pass: String,
            name: String,
            uploadAvatar: MediaUpload?
        ): Result<AuthState> {
            try {
                val fileAvatar = if (uploadAvatar != null) {
                    MultipartBody.Part.createFormData(
                        "file",
                        uploadAvatar.file.name,
                        uploadAvatar.file.asRequestBody()
                    )
                } else null

                val response =
                    ApiService.service.signUp(
                        login.toRequestBody(),
                        pass.toRequestBody(),
                        name.toRequestBody(),
                        fileAvatar
                    )
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }

                return response.body()?.let { Result.success(it) } ?: Result.failure(
                    ApiError(
                        response.code(),
                        response.message()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
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