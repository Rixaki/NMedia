package ru.netology.nmedia.auth

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.annotations.SerializedName
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.AppApi
import ru.netology.nmedia.dto.PushToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    private val prefs =
        context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    private val privateAuthState: MutableStateFlow<AuthState>

    companion object {
        private const val KEY_ID = "id"
        private const val KEY_TOKEN = "token"
        private const val KEY_AVATAR = "avatarUrl"
    }

    init {
        val id = prefs.getLong(KEY_ID, 0L)
        val token = prefs.getString(KEY_TOKEN, null)
        val avatar = prefs.getString(KEY_AVATAR, null)

     //

        if (id == 0L || token == null) {
            privateAuthState = MutableStateFlow(AuthState())
            with(prefs.edit()) {
                clear()
                apply()
            }
        } else {
            privateAuthState = MutableStateFlow(AuthState(id, token, avatar))
        }
        sendPushToken()//guarantee for token sending with app start
    }

    val authState: StateFlow<AuthState> = privateAuthState.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String?, avatar: String? = null) {
        privateAuthState.value = AuthState(id, token, avatar)
        with(prefs.edit()) {
            putLong(KEY_ID, id)
            putString(KEY_TOKEN, token)
            putString(KEY_AVATAR, avatar)
            apply()
        }
        sendPushToken()
    }

    @Synchronized
    fun removeAuth() {
        privateAuthState.value = AuthState()
        with(prefs.edit()) {
            clear()
            commit()
        }
        sendPushToken()
    }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun getAppApi(): AppApi
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val tokenDto = PushToken(
                    token ?: FirebaseMessaging.getInstance().token.await()
                )
                val entryPoint =
                    EntryPointAccessors.fromApplication(
                    context,
                    AppAuthEntryPoint::class.java
                )
                entryPoint.getAppApi().sendPushToken(tokenDto)
                //ApiService.service.sendPushToken(tokenDto)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

data class AuthState(
    @SerializedName("id") val id: Long = 0L,
    @SerializedName("token") val token: String? = null,
    @SerializedName("avatar") val avatarUrl: String? = null,
)