package com.example.fundoonotes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundoonotes.model.UserAuthService
import com.example.fundoonotes.util.*

class LoginViewModel(val userAuthService: UserAuthService) : ViewModel() {
    private val _loginStatus = MutableLiveData<Status>()
    val loginStatus = _loginStatus as LiveData<Status>

    private val _resetPasswordStatus = MutableLiveData<String>()
    val resetPasswordStatus = _resetPasswordStatus as LiveData<String>

    private val _logoutStatus = MutableLiveData<Boolean>()
    val logoutStatus = _logoutStatus as LiveData<Boolean>

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn = _isUserLoggedIn as LiveData<Boolean>

    fun checkUserExistence() {
        userAuthService.getLoginStatus{
            _isUserLoggedIn.value = it
        }
    }

    fun loginToFundoo(email: String, password: String) {
        _loginStatus.value = Loading
        when {
            email.isEmpty() -> _loginStatus.value =
                Failed("Eamil Id is required", FailingReason.EMAIL)
            password.isEmpty() -> _loginStatus.value =
                Failed("Password Id is required", FailingReason.PASSWORD)
            else -> {
                userAuthService.userLogin(email, password) {
                    when (it.status) {
                        true -> _loginStatus.value = Success(SUCCESS_MSG, it.idToken, it.localId)
                        false -> _loginStatus.value = Failed(FAIL_MSG, FailingReason.OTHER)
                    }
                }
            }
        }
    }

    fun loginWithApi(email: String, password: String) {
        _loginStatus.value = Loading
        when {
            email.isEmpty() -> _loginStatus.value =
                Failed("Eamil Id is required", FailingReason.EMAIL)
            password.isEmpty() -> _loginStatus.value =
                Failed("Password Id is required", FailingReason.PASSWORD)
            else -> {
                userAuthService.loginWithRestApi(email, password) {
                    when (it.status) {
                        true -> _loginStatus.value = Success(SUCCESS_MSG, it.idToken, it.localId)
                        false -> _loginStatus.value = Failed(FAIL_MSG, FailingReason.OTHER)
                    }
                }
            }
        }
    }

    fun resetPassword(emailId: String) {
        userAuthService.resetPassword(emailId) {
            when (it) {
                true -> _resetPasswordStatus.value = "Reset link sent to given email Id"
                false -> _resetPasswordStatus.value = "ERROR !! Reset link is not sent."
            }
        }
    }

    fun userLogOut() {
        _logoutStatus.value = true
        userAuthService.logOut()
    }

    companion object {
        private const val SUCCESS_MSG: String = "Login Successful."
        private const val FAIL_MSG: String = "Login Unsuccessful."
    }
}