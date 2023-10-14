package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.nmedia.repository.PostRepoInMemImpl
import ru.netology.nmedia.repository.PostRepository

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepoInMemImpl()

    val data = repository.getAll()

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
}