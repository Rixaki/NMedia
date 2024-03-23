package ru.netology.nmedia.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

//import ru.netology.nmedia.di.DependencyContainer

@HiltAndroidApp
class NMediaApplication : Application()
/*{
    override fun onCreate() {
        super.onCreate()
        DependencyContainer.initApp(this)
    }
}
 */