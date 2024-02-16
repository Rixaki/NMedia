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
interface PostDao {
    //@Query("SELECT * FROM PostEntity ORDER BY id DESC")
    @Query(
        """
            SELECT * FROM PostEntity 
            ORDER BY
            CASE id WHEN id < 0 THEN 1 ELSE 0 END DESC,
            ABS(id) DESC
            """
    )
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    fun getPostById(id: Long): PostEntity

    @Query("SELECT MAX(id) FROM PostEntity WHERE isToShow = 1")
    fun getMaxIdAmongShown(): Long

    @Query("SELECT id FROM PostEntity WHERE id = :id")
    fun isIdExists(id: Long): Int //0 or 1 selected items

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

    /*
    @Query("""
       UPDATE PostEntity SET
                    shares = shares + 1
                WHERE id = :id
            """)
    suspend fun update(post: PostEntity, id: Long = post.id)
     */

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