package ru.netology.nmedia.dto

import ru.netology.nmedia.entity.PostEntity

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val likes: Long = 0,
    val shares: Long = 0,
    val video: String? = null
)

/*
data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likedByMe: Boolean = false,
    var likes: Long = 0,
    var shares: Long = 0
)
*/

