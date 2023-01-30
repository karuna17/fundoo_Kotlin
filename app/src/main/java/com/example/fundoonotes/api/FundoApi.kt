package com.example.fundoonotes.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FundoApi {
    @POST("./accounts:signInWithPassword?key=AIzaSyD-H1XI97sO0o0zQ91Z8y1qyY8oge91cMI")
    fun loginFundoUser(@Body request:LoginRequest): Call<LoginResponse>
}