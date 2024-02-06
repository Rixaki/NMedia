package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String = "404",
    val content: String,
    val published: Long = 0,
    val likedByMe: Boolean = false,
    val likes: Long = 0,
    val shares: Long = 0,
    val isSaved: Boolean = false,
    val video: String? = null,
    val attachment: Attachment? = null
)

data class Attachment(
    val url: String,
    val description: String,
    val type: AttachmentType
)

enum class AttachmentType(val typeName: String) {
    IMAGE("image")
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

