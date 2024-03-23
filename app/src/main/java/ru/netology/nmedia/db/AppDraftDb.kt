package ru.netology.nmedia.db

import androidx.room.Database
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

        //buildDatabase migrated to DI
    }
}