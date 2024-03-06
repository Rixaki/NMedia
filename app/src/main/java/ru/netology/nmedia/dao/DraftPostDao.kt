package ru.netology.nmedia.dao

//import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity


@Dao
interface DraftPostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    fun getPostById(id: Long): PostEntity

    @Query("UPDATE PostEntity SET isInShowFilter = isSaved")
    fun onlySavedShow(): Unit

    @Query("UPDATE PostEntity SET isInShowFilter = NOT(isSaved)")
    fun onlyDraftShow(): Unit

    @Query("UPDATE PostEntity SET isInShowFilter = 1")
    fun noFilterShow(): Unit

    @Query("SELECT COUNT(*) FROM PostEntity")
    fun getDaoSize(): Long

    @Upsert
    suspend fun save(post: PostEntity): Long

    @Query(
        """
                UPDATE PostEntity SET
                    likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
                    likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
                WHERE id = :id;
            """
    )
    suspend fun likeById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query(
        """
                UPDATE PostEntity SET
                    shares = shares + 1
                WHERE id = :id;
            """
    )
    suspend fun shareById(id: Long)
}