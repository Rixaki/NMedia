package ru.netology.nmedia.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.DraftPostDao
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
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

    /*
    After edit post by api, it remains in postDao with old "filling",
    except for isSaved = false flag (see saveWithDb method). But it isn`t
    shown in ui (due to isSaved flag), in position of edited post from api,
    will write draft post from draftPostDao with old id (MORE than 0), and
    draftPostDao (as well as) contains edited posts by ui (new posts) with
    id LESS than 0.
     */
    override val data = postDao.getAll().map {
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
    }//.flowOn(Dispatchers.Default)//default by viewmodel

    /*
    posts in draftPostDao with id, which exists in postDao aren`t shown in ui,
    because these posts are reserve for cancel edit operations (not released)
    UPDATE: cancel edit for api posts was released
     */
    override val draftData = draftPostDao.getAll().map { it ->
        it.map { entity ->
            if (postDao.isIdExists(entity.id) == 0) {
                entity.toDto()
            } else {
                entity.copy(id = 0L).toDto()//will filtered
            }
        }.filter { post -> post.id != 0L }
    }.flowOn(Dispatchers.Default)

    /*
    override val mergedData = CombinedLiveData(data, draftData) {
            data1, data2 -> data2+data1
    }
     */

    override val mergedData = data.combine(draftData) { dataList, draftList ->
        draftList.reversed() + dataList
    }

    override fun onlySavedShow() {
        postDao.onlySavedShow()
        draftPostDao.onlySavedShow()
        /*
        postDao.getAll().map {
            it.map { entity ->
                entity.copy(isInShowFilter = entity.isSaved).toDto()
            }
        }
        draftPostDao.getAll().map {
            it.map { entity ->
                entity.copy(isInShowFilter = entity.isSaved).toDto()
            }
        }
         */
    }

    override fun onlyDraftShow() {
        postDao.onlyDraftShow()
        draftPostDao.onlyDraftShow()
        /*
        postDao.getAll().map {
            it.map { entity ->
                entity.copy(isInShowFilter = !entity.isSaved).toDto()
            }
        }
        draftPostDao.getAll().map {
            it.map { entity ->
                entity.copy(isInShowFilter = !entity.isSaved).toDto()
            }
        }
         */
    }

    override fun noFilterShow() {
        postDao.noFilterShow()
        draftPostDao.noFilterShow()
        /*
        postDao.getAll().map {
            it.map { entity ->
                entity.copy(isInShowFilter = true).toDto()
            }
        }
        draftPostDao.getAll().map {
            it.map { entity ->
                entity.copy(isInShowFilter = true).toDto()
            }
        }
         */
    }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val response = ApiService.service.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body()
                ?: throw ApiError(response.code(), response.message())
            for (postRsp in body) {
                if (postDao.isIdExists(postRsp.id) == 0) {
                    postDao.insert(
                        PostEntity.fromDtoToEnt(postRsp).copy(isToShow = false)
                    )//insert for not exists id
                }
            }
            emit(body.size)
        }
    }
        .catch { flowOf(value = 0) }
    //.catch{ e -> throw AppError.from(e) }//it crashing due to throwing
    //.flowOn(Dispatchers.Default)//default in viewmodel
    //unactual flow will cancel end break wlile(true)

    override fun showAll() {
        postDao.showAllFresh()
        /*
        postDao.getAll().map {
            it.map { entity ->
                entity.copy(isToShow = true).toDto()
            }
        }
         */
    }

    override fun getMaxIdAmongShown(): Long {
        return try {
            postDao.getMaxIdAmongShown()
        } catch (e: NullPointerException) {
            0L
        }
    }

    override fun getSizeOfDrafts(): Long {
        return try {
            draftPostDao.getDaoSize()
        } catch (e: Exception) {
            0L
        }
    }

    override suspend fun uploadDraft(id: Long) {
        try {
            val draftPost = draftPostDao.getPostById(id).toDto()

            val response = ApiService.service
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
            val response = ApiService.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message()
            )
            val postsEnt = body.map {
                PostEntity.fromDtoToEnt(it)
                    .copy(isSaved = true, isToShow = true, isInShowFilter = true)
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
            val response = ApiService.service.like(id)//api like
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
            val response = ApiService.service.unlike(id)//api unlike
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
            val response = ApiService.service.deletePostById(id)//api delete
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

    override suspend fun cancelDraftById(id: Long) {
        try {
            draftPostDao.removeById(id)
            postDao.insert(
                postDao.getPostById(id).copy(isSaved = true)
            )
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithApi(post: Post) {
        try {
            val response = ApiService.service.save(post)
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
            val draftExistsMaxCounter = 20L
            var newDraftId = 0L
            if (post.id != 0L) {
                postDao.insert(
                    postDao.getPostById(post.id).copy(isSaved = false)
                )//vanish from ui without changes
                draftPostDao.insert(
                    PostEntity.fromDtoToEnt(
                        post.copy(id = post.id, isSaved = false)
                    )
                )//id > 0
            } else {
                newDraftId = (-1) * (getSizeOfDrafts() + 1L)
                loop@ while (true) {
                    //IDEA WARNING about null condition IS FAKE
                    if (draftPostDao.getPostById(newDraftId) == null) {
                        break@loop
                    } else {
                        newDraftId -= 1L
                    }
                }

                draftPostDao.insert(
                    PostEntity.fromDtoToEnt(
                        post.copy(
                            id = newDraftId % draftExistsMaxCounter,
                            isSaved = false
                        )//id < 0
                    )
                )
            }
            val response = ApiService.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message()
            )
            postDao.removeById(post.id)
            postDao.insert(
                PostEntity.fromDtoToEnt(
                    body.copy(
                        isSaved = true,
                        isToShow = true
                    )
                )
            )
            //IDEA Warning about non-null value is fake
            draftPostDao.removeById(
                if (post.id == 0L) newDraftId % draftExistsMaxCounter else post.id
            )

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

    override suspend fun saveWithDb(post: Post, upload: MediaUpload?) {
        try {
            val postWithAttachment = if (upload != null) {
                // TODO: supporting for other types
                val media = upload(upload)//getting id after saving to dao
                post.copy(
                    attachment = Attachment(
                        url = media.id,
                        type = AttachmentType.IMAGE
                    )
                )
            } else post
            println("url = ${postWithAttachment.attachment?.url}")
            saveWithDb(postWithAttachment)
        } catch (e: Exception) {
            //upload != null and media == null case
            saveWithDb(
                post.copy(
                    attachment = Attachment(
                        url = upload?.file?.name ?: "attachment404",
                        type = AttachmentType.IMAGE
                    )
                )
            )
            //println("url = ${upload?.file?.name}")
            when (e) {
                is ApiError -> {
                    throw e
                }

                is IOException -> {
                    throw NetworkError
                }

                else -> {
                    throw UnknownError
                }
            }
        }
    }

    override suspend fun upload(upload: MediaUpload): Media {
        /*
        return ApiService.service.upload(
            MultipartBody.Part.createFormData(
                "file",
                upload.file.name,
                upload.file.asRequestBody()
            )
        )
        */
        try {
            val media =
                MultipartBody.Part.createFormData(
                    "file", upload.file.name, upload.file.asRequestBody()
                )

            val response = ApiService.service.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(
                response.code(),
                response.message()
            )
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
