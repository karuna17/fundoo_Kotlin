package com.example.fundoonotes.api

import com.example.fundoonotes.listeners.LoginListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginLoader {

    fun getLoginDone(listener: LoginListener, email: String, password: String) {
        FundoClient.getInstance()?.getMyApi()?.loginFundoUser(LoginRequest(email, password, true))
            ?.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        listener.onLogin(response.body(), true)
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    listener.onLogin(null, false)
                }
            })
    }
}