package com.example.fundoonotes.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundoonotes.model.UserAuthService

class ProfileViewModel(private val userAuthService: UserAuthService) : ViewModel() {
    private val _logoutStatus = MutableLiveData<Boolean>()
    val logoutStatus = _logoutStatus as LiveData<Boolean>

    fun logout() {
        _logoutStatus.value = true
        userAuthService.logOut()
    }
    fun uploadImageToFirebase(uri: Uri) {
        userAuthService.uploadImageToFirebase(uri)
    }
}