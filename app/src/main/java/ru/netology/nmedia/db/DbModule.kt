package ru.netology.nmedia.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.dao.DraftPostDao
import ru.netology.nmedia.dao.PostDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)//depen-t for all app
@Module
class DbModule {

    @Singleton//set lifecycle
    @Provides
    fun provideBaseDb(
        @ApplicationContext
        context: Context//uses cxt whole app
    ): AppDb =
        Room.databaseBuilder(
        context,
        AppDb::class.java,
        "appdb.db"
    )
        .fallbackToDestructiveMigration()
        .allowMainThreadQueries()
        .build()

    @Singleton
    @Provides
    fun provideDraftDb(
        @ApplicationContext
        context: Context
    ): AppDraftDb =
        Room.databaseBuilder(
            context,
            AppDraftDb::class.java,
            "appDraftDb.db"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

    @Provides
    fun providePostDao(
        appDb: AppDb
    ) : PostDao = appDb.postDao

    @Provides
    fun provideDraftPostDao(
        appDraftDb: AppDraftDb
    ) : DraftPostDao = appDraftDb.draftPostDao
}

