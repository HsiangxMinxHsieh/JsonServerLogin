package com.timmy.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserModels : ArrayList<UserModel>()

data class UserModel(
    @SerializedName("account")
    val account: String = "",
    @SerializedName("password")
    val password: String = "",
    @SerializedName("id")
    val id: Int = 0
) : Serializable