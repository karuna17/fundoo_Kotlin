package com.example.fundoonotes.listeners

import com.example.fundoonotes.api.LoginResponse

interface LoginListener {
    fun onLogin(response: LoginResponse?,status:Boolean)
}