package com.timmy.login.di

import com.timmy.login.model.LoginModel
import com.timmy.login.model.LoginModelImpl
import com.timmy.login.view.LoginView
import com.timmy.login.viewmodel.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


val loginModule = module {
    single(named(LoginModel::class.java.simpleName)) {
        LoginModelImpl()
    }

    viewModel { (loginView: LoginView) ->
        LoginViewModel(loginView, get(named(LoginModel::class.java.simpleName)))
    }
}