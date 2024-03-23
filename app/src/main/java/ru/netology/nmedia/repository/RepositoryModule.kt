package ru.netology.nmedia.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {
    //Binds - create link: inject constructor & interface
    @Singleton
    @Binds
    fun bindsPostRepository(impl: PostRepoImpl): PostRepository
}