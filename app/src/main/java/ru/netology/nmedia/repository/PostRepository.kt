package ru.netology.nmedia.repository

//import CombinedLiveData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post

interface PostRepository {

    val data: Flow<List<Post>>
    val draftData: Flow<List<Post>>

    //val mergedData: CombinedLiveData
    val mergedData: Flow<List<Post>>

    suspend fun uploadDraft(id: Long)
    suspend fun getAll()

    fun onlySavedShow()
    fun onlyDraftShow()
    fun noFilterShow()

    fun getNewerCount(id: Long): Flow<Int>
    fun showAll()
    fun getMaxIdAmongShown(): Long
    fun getSizeOfDrafts(): Long

    suspend fun likeById(id: Long)
    suspend fun unLikeById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun cancelDraftById(id: Long)

    suspend fun saveWithApi(post: Post)
    suspend fun saveWithDb(post: Post)

    suspend fun saveWithDb(post: Post, upload: MediaUpload?)
    suspend fun upload(upload: MediaUpload): Media

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