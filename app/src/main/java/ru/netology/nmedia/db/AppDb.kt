package ru.netology.nmedia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.AttachmentConverter
import ru.netology.nmedia.entity.PostEntity

@Database(entities = [PostEntity::class], version = 1)
@TypeConverters(AttachmentConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract val postDao: PostDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        //buildDatabase migrated to DI
    }
}

/*
class DbHelper(
    context: Context,
    dbVersion: Int,
    dbName: String,
    private val DDLs: Array<String>
): SQLiteOpenHelper(context, dbName, null, dbVersion) {
    override fun onCreate(db: SQLiteDatabase?) {
        DDLs.forEach {
            db?.execSQL(it)
        }
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        //super.onUpgrade(db, oldVersion, newVersion)
        TODO("Not yet implemented")
    }

    override fun onDowngrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        //super.onDowngrade(db, oldVersion, newVersion)
        TODO("Not yet implemented")
    }
}
*/