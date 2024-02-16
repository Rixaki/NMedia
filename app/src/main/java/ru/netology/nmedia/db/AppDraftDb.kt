package ru.netology.nmedia.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.nmedia.dao.DraftPostDao
import ru.netology.nmedia.entity.AttachmentConverter
import ru.netology.nmedia.entity.PostEntity

@Database(entities = [PostEntity::class], version = 1)
@TypeConverters(AttachmentConverter::class)
abstract class AppDraftDb : RoomDatabase() {
    abstract val draftPostDao: DraftPostDao

    companion object {
        @Volatile
        private var instance: AppDraftDb? = null

        fun getInstance(context: Context): AppDraftDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                AppDraftDb::class.java,
                "appDraftDb.db"
            )
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
    }
}