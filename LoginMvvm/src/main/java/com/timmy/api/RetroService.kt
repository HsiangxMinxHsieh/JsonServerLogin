package com.timmy.api

import com.google.gson.JsonObject
import com.timmy.api.model.UserModel
import com.timmy.api.model.UserModels
import io.reactivex.Single
import retrofit2.http.*

interface RetroService {
    @GET("users")
    fun getAllData(): Single<UserModels> // 取得所有資料供登入

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("users")
    fun signUp(@Body jsonObject: JsonObject): Single<UserModel> // 註冊

}