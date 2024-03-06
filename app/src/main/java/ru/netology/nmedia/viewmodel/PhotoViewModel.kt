package ru.netology.nmedia.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.model.PhotoModel
import java.io.File

class PhotoViewModel(application: Application) : AndroidViewModel(application) {

    private val noPhoto = PhotoModel()

    private val privatePhoto = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = privatePhoto

    fun changePhoto(uri: Uri?, file: File?) {
        privatePhoto.value = PhotoModel(uri, file)
    }

    fun clearPhoto() {
        privatePhoto.value = null
    }
}