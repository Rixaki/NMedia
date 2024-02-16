package ru.netology.nmedia.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.db.AppDraftDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepoImpl
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = 0,
)

//viewmodel exist in 1 Activity!!!
//class PostViewModel : ViewModel() {
@SuppressLint("CheckResult")
class PostViewModel(application: Application) : AndroidViewModel(application) {
    //application extends context!!!
    private val repository: PostRepository =
        PostRepoImpl(
            AppDb.getInstance(application).postDao,
            AppDraftDb.getInstance(application).draftPostDao
        )

    private val privateState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = privateState


    val data: LiveData<FeedModel> = repository.mergedData.map { totalList ->
        FeedModel(
            posts = totalList.filter{ it.isToShow },
            empty = totalList.isEmpty(),
            maxId = repository.getMaxIdAmongShown()
        )
    }.catch { it.printStackTrace() }
        .asLiveData(Dispatchers.Default)
    //context = viewModelScope.coroutineContext + Dispatchers.Default
    //collect{emit(feedModel)} in asLiveData

    val newerCount: MutableLiveData<Int> = data.switchMap {
        repository.getNewerCount(it.maxId)
            .asLiveData(Dispatchers.Default)
    } as MutableLiveData<Int>//mutable for "Fresh posts" GONE after refresh/load

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

    private fun load() {
        //start loading
        privateState.value = (FeedModelState(loading = true))
        viewModelScope.launch {
            try {
                repository.getAll()
                newerCount.value = 0//for "Fresh posts" GONE
                privateState.value = FeedModelState()
            } catch (e: Exception) {
                privateState.value = FeedModelState(
                    error = true,
                    lastErrorAction = "Error with list load."
                )
            }
        }
    }

    fun showAllLoad() {
        repository.showAll()//for postDao
    }

    fun refresh() {
        privateState.value = (FeedModelState(refreshing = true))
        viewModelScope.launch {
            try {
                repository.getAll()
                privateState.value = FeedModelState()
            } catch (e: Exception) {
                privateState.value = FeedModelState(
                    error = true,
                    lastErrorAction = "Error with list refresh."
                )
            }
        }
    }

    /*
    fun loadOfPost(toLoadPost: Post) {
        privateState.value = (FeedModelState(loading = true))
        var posts = data.value?.posts
        if (posts != null) {
            if (!posts.map { it.id }.contains(toLoadPost.id)) { //newPost
                posts = listOf(toLoadPost) + posts
            } else { //editPost
                posts = posts.map { if (it.id != toLoadPost.id) it else toLoadPost }
            }
        }
        privateState.value = FeedModelState()
    }
     */

    fun saveLocal(id: Long) {
        privateState.value = (FeedModelState(loading = true))
        viewModelScope.launch {
            try {
                repository.uploadDraft(id)
                privateState.value = FeedModelState()
            } catch (e: Exception) {
                privateState.value = FeedModelState(
                    error = true,
                    //lastErrorAction = "Error with draft upload."
                )
            }
        }
    }

    fun changeContentAndSave(inputContent: String) {
        if (edited.value?.content == inputContent) {
            return
        }
        edited.value = edited.value?.copy(
            content = inputContent,
            isSaved = false
        )
        edited.value?.let { editedPost ->
            viewModelScope.launch {
                try {
                    repository.saveWithDb(editedPost)
                    privatePostCreated.postValue(Unit)
                } catch (e: Exception) {
                    saveLocal(editedPost.id)
                    privateState.value = FeedModelState(
                        error = true,
                        lastErrorAction = "Error with add/edit post."
                    )
                    privatePostCanceled.postValue(Unit)
                }
            }
        }
        edited.postValue(empty)
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun cancelEdit() {
        edited.value = empty
        privatePostCanceled.postValue(Unit)
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            val posts = data.value?.posts
            val post =
                posts?.find { it.id == id }//antisticking before request answer (only with throw id, not post)
            if (post?.likedByMe == false) {
                try {
                    repository.likeById(id)
                    data.value?.posts?.map { if (it.id == id) it.copy(likedByMe = true) else it }
                } catch (e: Exception) {
                    privateState.value = FeedModelState(
                        error = true,
                        lastErrorAction = "Error with liking post."
                    )
                }
            }
        }
    }

    fun unLikeById(id: Long) {
        viewModelScope.launch {
            val posts = data.value?.posts
            val post =
                posts?.find { it.id == id }//antisticking before request answer (only with throw id, not post)
            if (post?.likedByMe == true) {
                try {
                    repository.unLikeById(id)
                    data.value?.posts?.map { if (it.id == id) it.copy(likedByMe = false) else it }
                } catch (e: Exception) {
                    privateState.value = FeedModelState(
                        error = true,
                        lastErrorAction = "Error with unliking post."
                    )
                }
            }
        }
    }

    //fun shareById(id: Long) = thread {repository.shareById(id)}

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                privateState.value = FeedModelState(loading = true)
                repository.removeById(id)
                privateState.value = FeedModelState()
                data.value?.posts?.filter { it.id != id }
            } catch (e: Exception) {
                saveLocal(id)
                privateState.value = FeedModelState(
                    error = true,
                    lastErrorAction = "Error with delete post."
                )
            }
        }
    }

    fun cancelDraftById(id: Long) {
        viewModelScope.launch {
            try {
                privateState.value = FeedModelState(loading = true)
                repository.cancelDraftById(id)
                privateState.value = FeedModelState()
                data.value?.posts?.filter { !((it.id == id) && !it.isSaved) }
            } catch (e: Exception) {
                //saveLocal(id)
                privateState.value = FeedModelState(
                    error = true,
                    lastErrorAction = "Error with delete draft."
                )
            }
        }
    }
}
