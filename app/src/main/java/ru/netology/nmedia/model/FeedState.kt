package ru.netology.nmedia.model

import ru.netology.nmedia.dto.Post

data class FeedState(
    val posts: List<Post> = emptyList(),
    val loading: Boolean = false,
    val error: Boolean = false,
    val empty: Boolean = false,
    val sizeOfLoaded: Int = 0
)