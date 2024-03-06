package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val authorId: Long = 0,
    val author: String,
    val authorAvatar: String = "404",
    val content: String,
    val published: Long = 0,
    val likedByMe: Boolean = false,
    val likes: Long = 0,
    val shares: Long = 0,
    val isSaved: Boolean = false,
    val isToShow: Boolean = true,
    val isInShowFilter: Boolean = true,
    val video: String? = null,
    val attachment: Attachment? = null,

    val ownedByMe: Boolean = false
)

data class Attachment(
    val url: String,
    val description: String? = null,
    val type: AttachmentType
)

enum class AttachmentType {
    IMAGE
}

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

