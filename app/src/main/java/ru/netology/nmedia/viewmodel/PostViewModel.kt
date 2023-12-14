package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedState
import ru.netology.nmedia.repository.PostRepoImpl
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

//viewmodel exist in 1 Activity!!!
//class PostViewModel : ViewModel() {
class PostViewModel(application: Application) : AndroidViewModel(application) {
    //application extends context!!!
    private val repository: PostRepository = PostRepoImpl()

    private val privateCurrentState = MutableLiveData(FeedState())
    val currentState: LiveData<FeedState>
        get() = privateCurrentState

    val edited = MutableLiveData(empty)

    private val privatePostCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = privatePostCreated

    private val privatePostCanceled = SingleLiveEvent<Unit>()
    val postCanceled: LiveData<Unit>
        get() = privatePostCanceled

    init {
        load()
    }

    fun load() {
        thread {
            //currentState.value = FeedState(loading = true)
            privateCurrentState.postValue(FeedState(loading = true))
            try {
                val posts = repository.getAll()
                privateCurrentState.postValue(FeedState(posts = posts, empty = posts.isEmpty()))
            } catch (e: Exception) {
                privateCurrentState.postValue(FeedState(error = true))
            }
        }
    }

    fun changeContentAndSave(content: String) {
        edited.value?.let { //it of post
            val text = content.trim()
            if (it.content == text) {
                return
            }
            edited.value = edited.value?.copy(content = text)
            thread {
                edited.value?.let { //it of post
                    repository.save(it)//cntl+alt+b to fun code
                    edited.postValue(empty)
                    privatePostCreated.postValue(Unit)
                }
                load()
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun cancelEdit() {
        edited.value = empty
        privatePostCanceled.postValue(Unit)
    }

    fun likeById(id: Long) {
        thread {
            repository.likeById(id)
            load()
        }
    }

    fun unLikeById(id: Long) {
        thread {
            repository.unLikeById(id)
            load()
        }
    }

    //fun shareById(id: Long) = thread {repository.shareById(id)}
    fun removeById(id: Long) = thread {repository.removeById(id)}
}
