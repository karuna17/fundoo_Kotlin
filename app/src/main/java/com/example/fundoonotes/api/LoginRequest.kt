package com.example.fundoonotes.api

data class LoginRequest(
    var email: String = "",
    var password: String = "",
    var returnSecureToken: Boolean
)
