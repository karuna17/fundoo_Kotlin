package com.example.fundoonotes.listeners

data class AuthListener(
    val status: Boolean,
    val idToken: String = "",
    val localId: String = ""
)
