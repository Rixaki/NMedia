package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit


class PostRepoImpl() : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
        private val listPostType = object : TypeToken<List<Post>>() {}.type
        private val postType = object : TypeToken<Post>() {}.type
    }

    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
        val request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            //.execute()
            .enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseText = response.body?.string()

                        if (responseText == null) {
                            callback.onError(RuntimeException("body is null"))
                            return
                        }

                        try {//maybe unexpected type of json-responce
                            callback.onSuccess(
                                gson.fromJson(
                                    responseText,
                                    listPostType
                                )
                            )
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }
                }
            )
    }

    override fun save(post: Post, callback: PostRepository.Callback<Post>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .post(gson.toJson(post).toRequestBody(jsonType))
            .build()

        println(gson.toJson(post))

        return client.newCall(request)
            //.execute()
            .enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseText = response.body?.string()

                        if (responseText == null) {
                            callback.onError(RuntimeException("body is null"))
                            return
                        }

                        try {//maybe unexpected type of json-responce
                            callback.onSuccess(
                                gson.fromJson(
                                    responseText,
                                    postType
                                )
                            )
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }
                }
            )


        //val call = client.newCall(request)
        //val response = call.execute()
        //val body = requireNotNull(response.body)
        //val responceText = body.string()
        //return gson.fromJson(responceText, Post::class.java)
    }

    override fun likeById(id: Long, callback: PostRepository.Callback<Post>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/$id/likes")
            .post(gson.toJson("").toRequestBody(jsonType))
            .build()

        return client.newCall(request)
            //.execute()
            .enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseText = response.body?.string()

                        if (responseText == null) {
                            callback.onError(RuntimeException("body is null"))
                            return
                        }

                        try {//maybe unexpected type of json-responce
                            callback.onSuccess(
                                gson.fromJson(
                                    responseText,
                                    postType
                                )
                            )
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }
                }
            )

        //val call = client.newCall(request)
        //val response = call.execute()
        //val body = requireNotNull(response.body)
        //val responceText = body.string()
        //return gson.fromJson(responceText, Post::class.java)
    }

    override fun unLikeById(id: Long, callback: PostRepository.Callback<Post>) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id/likes")
            .build()

        return client.newCall(request)
            //.execute()
            .enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseText = response.body?.string()

                        if (responseText == null) {
                            callback.onError(RuntimeException("body is null"))
                            return
                        }

                        try {//maybe unexpected type of json-responce
                            callback.onSuccess(
                                gson.fromJson(
                                    responseText,
                                    postType
                                )
                            )
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }
                }
            )

        //val call = client.newCall(request)
        //val response = call.execute()
        //val body = requireNotNull(response.body)
        //val responceText = body.string()
        //return gson.fromJson(responceText, Post::class.java)
    }

    //override fun shareById(id: Long) {}

    override fun removeById(id: Long, callback: PostRepository.Callback<Unit>) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseText = response.body?.string()

                        if (responseText == null) {
                            callback.onError(RuntimeException("body is null"))
                            return
                        }
                        if (!response.isSuccessful) {
                            callback.onError(RuntimeException("Responce code isn't 200-s"))
                            return
                        }

                        callback.onSuccess(Unit)
                    }
                }
            )
    }
}
