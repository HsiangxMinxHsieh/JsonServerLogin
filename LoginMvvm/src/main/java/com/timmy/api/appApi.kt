package com.timmy.api

import okhttp3.OkHttpClient
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit


object appApi {
    fun getService(): RetroService {
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(1000L, TimeUnit.MILLISECONDS)
            .connectTimeout(500L, TimeUnit.MILLISECONDS)
            .build()

        val result = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
            .create(RetroService::class.java)
        println("retrofit 物件 是=>$result")
        return result

    }
}