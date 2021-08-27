package com.timmy.application

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import com.timmy.login.di.loginModule


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initDi()
    }

    // 初始化DI(Koin)注入工具
    private fun initDi() {
        startKoin {
            // Android context
            androidContext(this@App)
            // modules
            val list = listOf(loginModule)
            modules(list)
        }
    }

}