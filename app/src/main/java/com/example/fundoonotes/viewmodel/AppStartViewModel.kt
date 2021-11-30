package com.example.fundoonotes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundoonotes.model.UserAuthService

class AppStartViewModel(private val userAuthService: UserAuthService) : ViewModel() {
    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn = _isUserLoggedIn as LiveData<Boolean>

    fun checkUserExistence() {
        userAuthService.getLoginStatus{
            _isUserLoggedIn.value = it
        }
    }
}