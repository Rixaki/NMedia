package ru.netology.nmedia.repository

import CombinedLiveData2
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {

    val data: LiveData<List<Post>>
    val draftData: LiveData<List<Post>>
    val mergedData: CombinedLiveData2

    suspend fun uploadDraft(id: Long)
    suspend fun getAll()

    suspend fun likeById(id: Long)
    suspend fun unLikeById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun cancelDraftById(id: Long)

    suspend fun saveWithApi(post: Post)
    suspend fun saveWithDb(post: Post)

    /*
        fun getAll(): List<Post>
        fun getAllAsync(callback: Callback<List<Post>>)
        fun likeById(id: Long, callback: Callback<Post>)
        fun unLikeById(id: Long, callback: Callback<Post>)
        fun removeById(id: Long, callback: Callback<Unit>)
        fun save(post: Post, callback: Callback<Post>)
         */

    /*
    interface Callback<T> {
        fun onSuccess(data: T)
        fun onError(throwable: Throwable)
    }
     */
}