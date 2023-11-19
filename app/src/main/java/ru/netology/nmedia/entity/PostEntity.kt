package ru.netology.nmedia.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity (
    @PrimaryKey(autoGenerate = true) val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val likes: Long = 0,
    val shares: Long = 0,
    val video: String? = null
) {
    fun toDto(): Post = Post(
        id = id,
        author = author,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likes = likes,
        shares = shares,
        video = video
    )

    companion object {
        fun fromDtoToEnt(dto: Post): PostEntity = with(dto) {
            PostEntity(
                id = id,
                author = author,
                content = content,
                published = published,
                likedByMe = likedByMe,
                likes = likes,
                shares = shares,
                video = video
            )
        }
    }
}
