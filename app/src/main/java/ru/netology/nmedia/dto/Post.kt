package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val likes: Long,
    val shares: Long
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

