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

    private var posts: List<Post> = emptyList()

    init {
        load()
    }

    fun load() {
        //start loading
        privateCurrentState.postValue(FeedState(loading = true))

        //thread{...}, thread in async method
        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(data: List<Post>) {
                posts = data
                privateCurrentState.postValue(
                    FeedState(
                        posts = data,
                        empty = posts.isEmpty()
                    )
                )
            }

            override fun onError(throwable: Throwable) {
                privateCurrentState.postValue(FeedState(error = true))
            }
        })
    }

    fun loadOfPost(toLoadPost: Post) {
        privateCurrentState.postValue(FeedState(loading = true))
        if (!posts.map { it.id }.contains(toLoadPost.id)) { //newPost
            posts = listOf(toLoadPost) + posts
        } else { //editPost
            posts = posts.map { if (it.id != toLoadPost.id) it else toLoadPost }
        }
        privateCurrentState.postValue(
            FeedState(
                posts = posts,
                empty = posts.isEmpty()
            )
        )
    }

    fun changeContentAndSave(content: String) {
        edited.value?.let { editedPost -> //it of post
            val text = content.trim()
            if (editedPost.content == text) {
                return
            }
            edited.value = edited.value?.copy(content = text)
            edited.value?.let { changedPost ->
                repository.save(
                    changedPost,
                    object : PostRepository.Callback<Post> {
                        override fun onSuccess(data: Post) {
                            loadOfPost(data)
                            privatePostCreated.postValue(Unit)
                        }

                        override fun onError(throwable: Throwable) {
                            privateCurrentState.postValue(FeedState(error = true))
                            privatePostCanceled.postValue(Unit)
                        }
                    })//save, cntl+alt+b to fun code
            }
            edited.postValue(empty)
            //privatePostCreated.postValue(Unit)
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
        val post =
            posts.find { it.id == id }//antisticking before request answer (only with throw id, not post)
        if (post?.likedByMe == false) {
            repository.likeById(id, object : PostRepository.Callback<Post> {
                override fun onSuccess(data: Post) {
                    loadOfPost(data)
                }

                override fun onError(throwable: Throwable) {
                    privateCurrentState.postValue(FeedState(error = true))
                }
            })
            posts =
                posts.map { if (it.id == id) it.copy(likedByMe = true) else it }
        }
    }

    fun unLikeById(id: Long) {
        val post =
            posts.find { it.id == id }//antisticking before request answer (only with throw id, not post)
        if (post?.likedByMe == true) {
            repository.unLikeById(id, object : PostRepository.Callback<Post> {
                override fun onSuccess(data: Post) {
                    loadOfPost(data)
                }

                override fun onError(throwable: Throwable) {
                    privateCurrentState.postValue(FeedState(error = true))
                }
            })
            posts =
                posts.map { if (it.id == id) it.copy(likedByMe = false) else it }
        }
    }

    //fun shareById(id: Long) = thread {repository.shareById(id)}
    fun removeById(id: Long) {
        val oldList = posts
        posts = posts.filter { it.id != id }
        repository.removeById(id, object : PostRepository.Callback<Unit> {
            override fun onSuccess(data: Unit) {
                privateCurrentState.postValue(
                    FeedState(
                        posts = posts,
                        empty = posts.isEmpty()
                    )
                )
            }

            override fun onError(throwable: Throwable) {
                privateCurrentState.postValue(
                    FeedState(
                        posts = oldList,
                        error = true,
                        empty = oldList.isEmpty()
                    )
                )
            }
        })
    }
}
