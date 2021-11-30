package com.example.fundoonotes.listeners

import java.lang.Exception

interface UserAuthListener {
    fun onAuthCompleteListener(status: Boolean = false, message: String)
}