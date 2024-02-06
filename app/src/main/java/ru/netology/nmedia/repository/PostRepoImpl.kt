package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.dao.DraftPostDao
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException


class PostRepoImpl(
    private val postDao: PostDao,
    private val draftPostDao: DraftPostDao
) : PostRepository {

    override val data: LiveData<List<Post>> = postDao.getAll().map {
        it.map { entity ->
            entity.toDto()
        }
    }

    override suspend fun uploadDraft(id: Long) {
        try {
            val draftPost = draftPostDao.getPostById(id).toDto()

            val response = PostApiService.service.save(draftPost)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message()
            )
            draftPostDao.removeById(id)
            postDao.removeById(id)
            postDao.insert(PostEntity.fromDtoToEnt(body.copy(isSaved = true)))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getAll() {
        try {
            val response = PostApiService.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message()
            )
            val postsEnt = body.map {
                PostEntity.fromDtoToEnt(it).copy(isSaved = true)
            }
            postDao.insert(postsEnt) //update local db
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) {
        try {
            val response = PostApiService.service.like(id)//api like
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            } else {
                if (!postDao.getPostById(id).likedByMe) {
                    postDao.likeById(id)
                }//local like, postDao.likeById only change flag
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> {
                    throw NetworkError
                }

                else -> {
                    throw UnknownError
                }
            }
        }
    }

    override suspend fun unLikeById(id: Long) {
        try {
            val response = PostApiService.service.unlike(id)//api unlike
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            } else {
                if (postDao.getPostById(id).likedByMe) {
                    postDao.likeById(id)
                }//local unlike, postDao.likeById only change flag
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> {
                    throw NetworkError
                }

                else -> {
                    throw UnknownError
                }
            }
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = PostApiService.service.deletePostById(id)//api delete
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            } else {
                postDao.removeById(id)//local delete
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> {
                    throw NetworkError
                }

                else -> {
                    throw UnknownError
                }
            }
        }
    }

    override suspend fun saveWithApi(post: Post) {
        try {
            val response = PostApiService.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message()
            )
            postDao.insert(PostEntity.fromDtoToEnt(body.copy(isSaved = true)))
        } catch (e: Exception) {
            when (e) {
                is IOException -> {
                    throw NetworkError
                }

                else -> {
                    throw UnknownError
                }
            }
        }
    }

    override suspend fun saveWithDb(post: Post) {
        try {
            var draftNewIndex = 0L
            if (post.id != 0L) {
                postDao.insert(PostEntity.fromDtoToEnt(post.copy(isSaved = false)))
                draftPostDao.insert(PostEntity.fromDtoToEnt(post))
            } else {
                val draftDaoSize =
                    draftPostDao.getAll().value?.size?.toLong() ?: 0L
                val daoSize = postDao.getAll().value?.size?.toLong() ?: 0L
                val dataSize = data.value?.size?.toLong() ?: 0L
                draftNewIndex = maxOf(draftDaoSize, daoSize, dataSize).plus(1L)
                draftPostDao.insert(
                    PostEntity.fromDtoToEnt(
                        post.copy(id = draftNewIndex)
                    )
                )
                postDao.insert(
                    PostEntity.fromDtoToEnt(
                        post.copy(id = draftNewIndex, isSaved = false)
                    )
                )
            }
            val response = PostApiService.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message()
            )
            draftPostDao.removeById(if (post.id != 0L) post.id else draftNewIndex)
            postDao.removeById(if (post.id != 0L) post.id else draftNewIndex)
            postDao.insert(PostEntity.fromDtoToEnt(body.copy(isSaved = true)))
        } catch (e: Exception) {
            when (e) {
                is IOException -> {
                    throw NetworkError
                }

                else -> {
                    throw UnknownError
                }
            }
        }
    }
}
