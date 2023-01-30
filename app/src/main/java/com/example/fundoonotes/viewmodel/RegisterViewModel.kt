package com.example.fundoonotes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundoonotes.model.User
import com.example.fundoonotes.model.UserAuthService
import com.example.fundoonotes.util.Failed
import com.example.fundoonotes.util.FailingReason
import com.example.fundoonotes.util.Status
import com.example.fundoonotes.util.Success

class RegisterViewModel(private val userAuthService: UserAuthService) : ViewModel() {
    private val _userRegistrationStatus = MutableLiveData<Status>()
    val userRegistrationStatus = _userRegistrationStatus as LiveData<Status>

    fun registerUser(user: User) {
        when {
            user.name.isEmpty() -> {
                _userRegistrationStatus.value = Failed("Please Enter your first name.", FailingReason.NAME)
            }
            user.email.isEmpty() -> {
                _userRegistrationStatus.value = Failed("Please Enter Email Id", FailingReason.EMAIL)
            }
            user.password.isEmpty() -> {
                _userRegistrationStatus.value = Failed("Please Enter Password", FailingReason.EMAIL)
            }
            else -> {
                userAuthService.userRegister(user) {
                    when (it) {
                        false -> _userRegistrationStatus.value =
                            Failed(
                                "Registration unsuccessful, Please try again",
                                FailingReason.OTHER
                            )
                        true -> _userRegistrationStatus.value = Success("Registration Successful")
                    }
                }
            }
        }
    }
}