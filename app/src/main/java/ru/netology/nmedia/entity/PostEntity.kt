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
    val authorId: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: Long,
    val likedByMe: Boolean = false,
    val likes: Long = 0,
    val shares: Long = 0,
    val isSaved: Boolean = false,
    val isToShow: Boolean = true,
    val isInShowFilter: Boolean = true,
    val video: String? = null,

    val ownedByMe: Boolean = false,
    @Embedded
    val attachment: AttachmentEmbeddable? = null
) {
    fun toDto(): Post = Post(
        id = id,
        authorId = authorId,
        author = author,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likes = likes,
        shares = shares,
        isSaved = isSaved,
        video = video,
        isToShow = isToShow,
        isInShowFilter = isInShowFilter,
        attachment = attachment?.toAttDto(),
    )

    companion object {
        fun fromDtoToEnt(dto: Post): PostEntity = with(dto) {
            PostEntity(
                id = id,
                authorId = authorId,
                author = author,
                authorAvatar = authorAvatar,
                content = content,
                published = published,
                likedByMe = likedByMe,
                likes = likes,
                shares = shares,
                isSaved = isSaved,
                video = video,
                isToShow = isToShow,
                isInShowFilter = isInShowFilter,
                attachment = attachment?.let {
                    AttachmentEmbeddable.fromDtoToEntAtt(
                        it
                    )
                }
            )
        }
    }
}

data class AttachmentEmbeddable(
    val url: String,
    val description: String?,
    val type: AttachmentTypeEntity
) {
    fun toAttDto(): Attachment = Attachment(
        url = url,
        description = description,
        type = AttachmentType.valueOf(type.name)
    )

    companion object {
        fun fromDtoToEntAtt(dto: Attachment): AttachmentEmbeddable = with(dto) {
            AttachmentEmbeddable(
                url = url,
                description = description,
                type = AttachmentTypeEntity.valueOf(type.name)
            )
        }
    }
}

enum class AttachmentTypeEntity {
    IMAGE
}