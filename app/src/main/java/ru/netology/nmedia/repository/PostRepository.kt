package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {

    //fun getAll(): LiveData<List<Post>>
    fun getAll(): List<Post>
    fun likeById(id: Long) : Post
    fun unLikeById(id: Long) : Post
    fun removeById(id: Long)
    //fun shareById(id: Long)
    fun save(post: Post) : Post
}