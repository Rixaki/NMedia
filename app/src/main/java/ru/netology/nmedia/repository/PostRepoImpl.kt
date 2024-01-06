package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.dto.Post


class PostRepoImpl() : PostRepository {
    override fun getAll(): List<Post> {
        //sync version
        return PostApiService.service.getAll()
            .execute()
            .let {
                it.body() ?: throw RuntimeException("body is null")
            }
    }

    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
        //return client.newCall(request)
        return PostApiService.service.getAll()
            .enqueue(
                object : Callback<List<Post>> {
                    override fun onResponse(
                        call: Call<List<Post>>,
                        response: Response<List<Post>>
                    ) {
                        if (!response.isSuccessful) {
                            callback.onError(
                                Exception(
                                    response.errorBody()?.string()
                                )
                            )
                            return
                        }

                        val body = response.body()
                            ?: throw RuntimeException("body is null")
                        try {
                            callback.onSuccess(body)
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }

                    override fun onFailure(
                        call: Call<List<Post>>,
                        t: Throwable
                    ) {
                        callback.onError(t)
                    }
                }
            )
    }

    override fun save(post: Post, callback: PostRepository.Callback<Post>) {
        //async version
        return PostApiService.service.save(post)
            .enqueue(
                object : Callback<Post> {
                    override fun onResponse(
                        call: Call<Post>,
                        response: Response<Post>
                    ) {
                        if (!response.isSuccessful) {
                            callback.onError(
                                Exception(
                                    response.errorBody()?.string()
                                )
                            )
                            return
                        }

                        val body = response.body()
                            ?: throw RuntimeException("body is null")
                        try {
                            callback.onSuccess(body)
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }

                    override fun onFailure(
                        call: Call<Post>,
                        t: Throwable
                    ) {
                        callback.onError(t)
                    }
                }
            )

        //sync version
        //PostApiService.service.save(post)
        //    .execute()
    }
    //val call = client.newCall(request)
    //val response = call.execute()
    //val body = requireNotNull(response.body)
    //val responceText = body.string()
    //return gson.fromJson(responceText, Post::class.java)

    override fun likeById(id: Long, callback: PostRepository.Callback<Post>) {
        //async version
        return PostApiService.service.like(id)
            .enqueue(
                object : Callback<Post> {
                    override fun onResponse(
                        call: Call<Post>,
                        response: Response<Post>
                    ) {
                        if (!response.isSuccessful) {
                            callback.onError(
                                Exception(
                                    response.errorBody()?.string()
                                )
                            )
                            return
                        }

                        val body = response.body()
                            ?: throw RuntimeException("body is null")
                        try {
                            callback.onSuccess(body)
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }

                    override fun onFailure(
                        call: Call<Post>,
                        t: Throwable
                    ) {
                        callback.onError(t)
                    }
                }
            )

        //sync version
        //PostApiService.service.like(id)
        //    .execute()
    }

    override fun unLikeById(id: Long, callback: PostRepository.Callback<Post>) {
        //async version
        return PostApiService.service.unlike(id)
            .enqueue(
                object : Callback<Post> {
                    override fun onResponse(
                        call: Call<Post>,
                        response: Response<Post>
                    ) {
                        if (!response.isSuccessful) {
                            callback.onError(
                                Exception(
                                    response.errorBody()?.string()
                                )
                            )
                            return
                        }

                        val body = response.body()
                            ?: throw RuntimeException("body is null")
                        try {
                            callback.onSuccess(body)
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }

                    override fun onFailure(
                        call: Call<Post>,
                        t: Throwable
                    ) {
                        callback.onError(t)
                    }
                }
            )

        //sync version
        //PostApiService.service.unlike(id)
        //    .execute()
    }

    //override fun shareById(id: Long) {}

    override fun removeById(id: Long, callback: PostRepository.Callback<Unit>) {
        //async version
        PostApiService.service.deletePostById(id)
            .enqueue(
                object : Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>
                    ) {
                        if (!response.isSuccessful) {
                            callback.onError(
                                Exception(
                                    response.errorBody()?.string()
                                )
                            )
                            return
                        }

                        val body = response.body()
                            ?: throw RuntimeException("body is null")
                        try {
                            callback.onSuccess(body)
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }

                    override fun onFailure(
                        call: Call<Unit>,
                        t: Throwable
                    ) {
                        callback.onError(t)
                    }
                }
            )

        //sync version
        //PostApiService.service.deletePostById(id)
        //    .execute()
    }
}
