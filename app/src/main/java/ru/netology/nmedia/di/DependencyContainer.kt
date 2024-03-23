/*
package ru.netology.nmedia.di

import android.content.Context
import androidx.room.Room
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.api.AppApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.db.AppDraftDb
import ru.netology.nmedia.repository.PostRepoImpl
import ru.netology.nmedia.repository.PostRepository
import java.util.concurrent.TimeUnit

class DependencyContainer(
    private val context: Context
) {
    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999/api/slow/"

        //IDEA Warning ignore due to using app context without(!) activity context
        @Volatile
        private var instance: DependencyContainer? = null

        fun initApp(context: Context) {
            instance = DependencyContainer(context).also { instance = it }
        }

        fun getInstance(): DependencyContainer {
            return instance!!
        }
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val appAuth = AppAuth(context)

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            appAuth.authState.value.token?.let { token ->
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

    private val appBaseDb =
        Room.databaseBuilder(
            context,
            AppDb::class.java,
            "appdb.db"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

    private val appDraftDb =
        Room.databaseBuilder(
            context,
            AppDraftDb::class.java,
            "appDraftDb.db"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

    private val postDao = appBaseDb.postDao
    private val draftDao = appDraftDb.draftPostDao
    val appApi = retrofit.create<AppApi>()

    val repository: PostRepository = PostRepoImpl(
        postDao,
        draftDao,
        appApi,
        )
}
 */