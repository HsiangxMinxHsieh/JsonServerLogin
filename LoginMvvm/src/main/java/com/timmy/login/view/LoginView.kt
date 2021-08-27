package com.timmy.login.view

import com.timmy.api.model.UserModel

interface LoginView {
    fun toLoginSuccessPage(user: UserModel)
    fun showErrorMessage(message: String)
    fun showLoadingView(open: Boolean)
}