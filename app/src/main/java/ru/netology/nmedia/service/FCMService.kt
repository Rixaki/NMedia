package ru.netology.nmedia.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.AppActivity
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val gson = Gson()

    private val channelId = "server"

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(channelId, name, importance).apply {
                    description = descriptionText
                    setSound(
                        null,
                        null
                    )//may un-stable work with IMPORTANCE_DEFAULT
                }
            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.data[action]?.let {
            when (Actions.createValue(it)) {
                //when (Actions.valueOf(it)) {
                Actions.LIKE -> handleLike(
                    gson.fromJson(
                        message.data[content],
                        Like::class.java
                    )
                )

                Actions.NEW_POST -> handleNewPost(
                    gson.fromJson(
                        message.data[content],
                        NewPost::class.java
                    )
                )

                else -> {
                    handleUndefined(
                        gson.fromJson(
                            message.data[content],
                            Dummy::class.java
                        )
                    )
                }
            }
        }
    }

    private fun handleLike(like: Like) {
        val intent: Intent = Intent(this, AppActivity::class.java)
        val pi: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_netology_bw_48)
            .setContentText(
                getString(
                    R.string.notification_user_liked,
                    like.userName,
                    like.postAuthor
                )
            )
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this).notify(
                Random.nextInt(100_000),
                notification
            )
        }
    }

    private fun handleNewPost(post: NewPost) {
        val intent: Intent = Intent(this, AppActivity::class.java)
        val pi: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_netology_bw_48)
            .setContentText(
                getString(
                    R.string.notification_new_post,
                    post.postAuthor
                )
            )
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    getString(
                        R.string.notification_new_post_full_form,
                        post.postAuthor,
                        post.content
                    )
                )
            )
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this).notify(
                Random.nextInt(100_000),
                notification
            )
        }
    }

    private fun handleUndefined(dymmy: Dummy) {
        val intent: Intent = Intent(this, AppActivity::class.java)
        val pi: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_netology_bw_48)
            .setContentText(
                getString(
                    R.string.notification_undefined
                )
            )
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this).notify(
                Random.nextInt(100_000),
                notification
            )
        }
    }

    override fun onNewToken(token: String) {
        println(token)
    }
}

enum class Actions {
    LIKE, NEW_POST, ANOTHER;

    //valueOf analog with safe-call
    companion object {
        inline fun createValue(x: String): Actions {
            return when (x) {
                "LIKE" -> LIKE
                "NEW_POST" -> NEW_POST
                else -> ANOTHER
            }
        }
    }
}

/*
//NOT WORKING WITH:
//FATAL EXCEPTION: Firebase-Messaging-Intent-Handle
//Process: ru.netology.nmedia, PID: 11445
//java.lang.IllegalArgumentException: No enum constant ru.netology.nmedia.service.Actions.type3

inline fun <reified Actions : Enum<Actions>> valueOf(type: String): Actions {
    return try {
        java.lang.Enum.valueOf(Actions::class.java, type)
    } catch (e: IllegalArgumentException) {
        java.lang.Enum.valueOf(Actions::class.java, "ANOTHER")
    }
}
*/

data class Like(
    val userId: Int,
    val userName: String,
    val postId: Int,
    val postAuthor: String
)

data class NewPost(
    val postId: Int,
    val postAuthor: String,
    val content: String
)

data class Dummy(
    val count: Int = 0
)