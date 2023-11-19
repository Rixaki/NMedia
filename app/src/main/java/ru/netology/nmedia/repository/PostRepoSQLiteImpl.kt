package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

class PostRepoSQLiteImpl(
    private val dao: PostDao
    //"private val" taking for using out of init-block/in class
) : PostRepository {
    private val data = dao.getAll()

    override fun getAll(): LiveData<List<Post>> = dao.getAll().map { posts ->
        posts.map(PostEntity::toDto)
    }

    override fun save(post: Post) {
        dao.save(PostEntity.fromDtoToEnt(post))
    }

    override fun likeById(id: Long) = dao.likeById(id)

    override fun shareById(id: Long) = dao.shareById(id)

    override fun removeById(id: Long) = dao.removeById(id)
}