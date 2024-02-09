package ru.netology.nmedia.repository

import CombinedLiveData
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
import java.lang.NullPointerException


class PostRepoImpl(
    private val postDao: PostDao,
    private val draftPostDao: DraftPostDao
) : PostRepository {

    /*
    After edit post by api, it remains in postDao with old "filling",
    except for isSaved = false flag (see saveWithDb method). But it isn`t
    shown in ui (due to isSaved flag), in position of edited post from api,
    will write draft post from draftPostDao with old id (MORE than 0), and
    draftPostDao (as well as) contains edited posts by ui (new posts) with
    id LESS than 0.
     */
    override val data: LiveData<List<Post>> = postDao.getAll().map {
        it.map { entity ->
            try {
                if (entity.isSaved) {
                    entity.toDto()
                } else {
                    draftPostDao.getPostById(entity.id).toDto()
                }
            } catch (e: NullPointerException) {
                entity.toDto()
            }
        }
    }

    /*
    posts in draftPostDao with id, which exists in postDao aren`t shown in ui,
    because these posts are reserve for cancel edit operations (not released)
     */
    override val draftData: LiveData<List<Post>> = draftPostDao.getAll().map { it ->
        it.map { entity ->
            try {
                if(postDao.getPostById(entity.id).id == 0L){}//for throwable check
                entity.copy(id = 0L).toDto()
            } catch (e: NullPointerException) {
                entity.toDto()//
            }
        }.filter { post -> post.id != 0L }
    }

    override val mergedData = CombinedLiveData(data, draftData)

    override suspend fun uploadDraft(id: Long) {
        try {
            val draftPost = if (id > 0) {
                postDao.getPostById(id).toDto()
            } else {
                draftPostDao.getPostById(id).toDto()
            }

            val response = PostApiService.service
                .save(if (id > 0) draftPost else draftPost.copy(id = 0L))
            //id<0 have new posts
            //for uploading new post to api, id=0 (due to api arch)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message()
            )
            draftPostDao.removeById(id)
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
                draftPostDao.removeById(id)
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
            var newDraftId = 0L
            if (post.id != 0L) {
                postDao.insert(
                    postDao.getPostById(post.id).copy(isSaved = false))//vanish from ui without changes
                draftPostDao.insert(
                    PostEntity.fromDtoToEnt(
                        post.copy(id = post.id, isSaved = false)))//id > 0
            } else {
                newDraftId = (-1)*(draftPostDao.getDaoSize() + 1L)
                draftPostDao.insert(
                    PostEntity.fromDtoToEnt(
                        post.copy(id = newDraftId, isSaved = false)//id < 0
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
            postDao.removeById(post.id)
            postDao.insert(PostEntity.fromDtoToEnt(body.copy(isSaved = true)))
            draftPostDao.removeById(if (post.id == 0L) newDraftId else post.id)
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
