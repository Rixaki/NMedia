package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.nmedia.repository.PostRepoInMemImpl
import ru.netology.nmedia.repository.PostRepository

class PostViewModel: ViewModel() {
    private val repository: PostRepository = PostRepoInMemImpl()

    val data = repository.get()

    fun like() = repository.like()
    fun share() = repository.share()
}