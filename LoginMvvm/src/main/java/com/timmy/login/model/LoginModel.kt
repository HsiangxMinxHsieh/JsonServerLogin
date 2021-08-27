package com.timmy.login.model

import com.google.gson.JsonObject
import com.timmy.api.appApi
import com.timmy.api.model.UserModel
import com.timmy.api.model.UserModels
import com.timmy.ext.toJson
import io.reactivex.Single

interface LoginModel {
    fun getAllDataFromAPI(): Single<UserModels>
    fun signUp(user: UserModel): Single<UserModel>
}


class LoginModelImpl : LoginModel {
    override fun getAllDataFromAPI(): Single<UserModels> {
        val result = appApi.getService().getAllData()
        return result
    }

    override fun signUp(user: UserModel): Single<UserModel> {
        val data = JsonObject().apply {
            addProperty("account", user.account)
            addProperty("password", user.password)
        }
        return appApi.getService().signUp(data)
    }
}