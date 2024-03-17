package ru.netology.nmedia.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PushToken
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://10.0.2.2:9999/api/slow/"

private val logging = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .addInterceptor { chain ->
        AppAuth.getInstance().authState.value.token?.let { token ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", token)
                .build()
            return@addInterceptor chain.proceed(newRequest)
        }
        chain.proceed(chain.request())//work without token
    }
    .let {
        if (BuildConfig.DEBUG) it.addInterceptor(logging) else it
    }
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface AppApi {
    //POST COMMAND
    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    //POST COMMAND
    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    //POST COMMAND
    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    //POST COMMAND
    @DELETE("posts/{id}")
    suspend fun deletePostById(@Path("id") id: Long): Response<Unit>

    //POST COMMAND
    @POST("posts/{id}/likes")
    suspend fun like(@Path("id") id: Long): Response<Post>

    //POST COMMAND
    @Multipart
    @POST("media")
    suspend fun upload(@Part media: MultipartBody.Part): Response<Media>
    //@PartMap for few media objects
    //auto parse for 200-code with :Media

    //POST COMMAND
    @DELETE("posts/{id}/likes")
    suspend fun unlike(@Path("id") id: Long): Response<Post>

    //USER COMMAND
    //https://demonuts.com/android-login-registration-using-retrofit/
    @Multipart
    @POST("users/registration")
    suspend fun signUp(
        @Part("login") login: RequestBody,
        @Part("pass") pass: RequestBody,
        @Part("name") name: RequestBody,
        @Part avatar: MultipartBody.Part?,
    ): Response<AuthState>
    //throwable UserRegisteredException()

    //USER COMMAND
    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun signIn(
        @Field("login") login: String,
        @Field("pass") pass: String,
    ): Response<AuthState>
    //throwable PasswordNotMatchException()
    //throwable NotFoundException() (not user)

    //USER COMMAND
    @Multipart
    @POST("avatars")
    suspend fun uploadAvatar(@Part media: MultipartBody.Part): Response<Media>

    //TOKEN COMMAND
    @POST("users/push-tokens")
    suspend fun sendPushToken(@Body pushToken: PushToken): Unit
}

object ApiService {
    val service: AppApi by lazy {
        retrofit.create()
    }
}
