package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit


class PostRepoImpl() : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
        private val type = object : TypeToken<List<Post>>() {}.type
    }

    override fun getAll(): List<Post> {
        val request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        val call = client.newCall(request)
        val response = call.execute()
        val body = requireNotNull(response.body)
        val responceText = body.string()
        return gson.fromJson(responceText, type)
    }

    override fun save(post: Post) : Post {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .post(gson.toJson(post).toRequestBody(jsonType))
            .build()

        val call = client.newCall(request)
        val response = call.execute()
        val body = requireNotNull(response.body)
        val responceText = body.string()
        return gson.fromJson(responceText, Post::class.java)
    }

    override fun likeById(id: Long) : Post {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/$id/likes")
            .post(gson.toJson("").toRequestBody(jsonType))
            .build()

        val call = client.newCall(request)
        val response = call.execute()
        val body = requireNotNull(response.body)
        val responceText = body.string()
        //println(responceText)
        return gson.fromJson(responceText, Post::class.java)
    }

    override fun unLikeById(id: Long) : Post {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id/likes")
            .build()

        val call = client.newCall(request)
        val response = call.execute()
        val body = requireNotNull(response.body)
        val responceText = body.string()
        //println(responceText)
        return gson.fromJson(responceText, Post::class.java)
    }

    //override fun shareById(id: Long) {}

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
}
