package com.example.fundoonotes.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object FundoClient {
    private lateinit var myApi: FundoApi
    private var client: OkHttpClient
    private var instance: FundoClient? = null

    init {
        val httpClient = OkHttpClient.Builder()
        client = httpClient.connectTimeout(40, TimeUnit.SECONDS)
            .readTimeout(40, TimeUnit.SECONDS)
            .writeTimeout(40, TimeUnit.SECONDS).build()
    }

    fun getInstance(): FundoClient? {
        if (instance == null) {
            instance = FundoClient
        }
        return instance
    }

    fun getMyApi(): FundoApi {
        val retrofit = Retrofit.Builder().baseUrl("https://identitytoolkit.googleapis.com/v1/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(client).build()
        myApi = retrofit.create(FundoApi::class.java)
        return myApi
    }

}



