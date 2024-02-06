package ru.netology.nmedia.entity;

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Post

@Entity
@TypeConverters(AttachmentConverter::class)
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: Long,
    val likedByMe: Boolean = false,
    val likes: Long = 0,
    val shares: Long = 0,
    val isSaved: Boolean = false,
    val video: String? = null,
    @Embedded
    val attachment: Attachment? = null
) {
    fun toDto(): Post = Post(
        id = id,
        author = author,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likes = likes,
        shares = shares,
        isSaved = isSaved,
        video = video,
        attachment = attachment
    )

    companion object {
        fun fromDtoToEnt(dto: Post): PostEntity = with(dto) {
            PostEntity(
                id = id,
                author = author,
                authorAvatar = authorAvatar,
                content = content,
                published = published,
                likedByMe = likedByMe,
                likes = likes,
                shares = shares,
                isSaved = isSaved,
                video = video,
                attachment = attachment
            )
        }
    }
}

data class AttachmentEntity(
    val url: String,
    val description: String,
    val type: AttachmentTypeEntity
) {
    fun toAttDto(): Attachment = Attachment(
        url = url,
        description = description,
        type = AttachmentType.valueOf(type.typeName)
    )

    companion object {
        fun fromDtoToEntAtt(dto: Attachment): AttachmentEntity = with(dto) {
            AttachmentEntity(
                url = url,
                description = description,
                type = AttachmentTypeEntity.valueOf(type.typeName)
            )
        }
    }
}

enum class AttachmentTypeEntity(val typeName: String) {
    IMAGE("image")
}