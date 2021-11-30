package com.example.fundoonotes.util

sealed class Status

data class Success(val message: String, var idToken: String = "", var localId: String = "") : Status()
data class Failed(val message: String, val reason: FailingReason) : Status()
object Loading : Status()