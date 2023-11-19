package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepoFilesImpl
import ru.netology.nmedia.repository.PostRepoInMemImpl
import ru.netology.nmedia.repository.PostRepoSQLiteImpl
import ru.netology.nmedia.repository.PostRepoSharedPrefsImpl
import ru.netology.nmedia.repository.PostRepository

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = ""
)

//viewmodel exist in 1 Activity!!!
//class PostViewModel : ViewModel() {
class PostViewModel(application: Application): AndroidViewModel(application) {
    //application extends context!!!
    //private val repository: PostRepository = PostRepoInMemImpl()
    private val repository: PostRepository = PostRepoSharedPrefsImpl(application)
    //private val repository: PostRepository = PostRepoFilesImpl(application)
    /*
    private val repository: PostRepository = PostRepoSQLiteImpl(
        AppDb.getInstance(application).postDao
    )
    */

    val data = repository.getAll()
    val edited = MutableLiveData(empty)

    fun changeContentAndSave(content: String) {
        edited.value?.let { //it of post
            val text = content.trim()
            if (it.content == text) {
                return
            }
            edited.value?.let {
                repository.save(it.copy(content = text))//cntl+alt+b to fun code
            }
            edited.value = empty
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun cancelEdit() {
        edited.value = empty
    }

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)
}