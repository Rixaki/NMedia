package ru.netology.nmedia.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity

@Database(entities = [PostEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract val postDao: PostDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context, AppDb::class.java, "appdb.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
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