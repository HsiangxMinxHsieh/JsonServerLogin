package com.timmy.login.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import com.timmy.api.model.UserModel
import com.timmy.api.model.UserModels
import com.timmy.const.AppConst
import com.timmy.ext.addTo
import com.timmy.login.model.LoginModel
import com.timmy.login.view.LoginView
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers.io
import java.util.concurrent.TimeUnit


class LoginViewModel(val view: LoginView, val model: LoginModel) : ViewModel() {
    val userList by lazy { UserModels() }
    val compositeDisposable by lazy { CompositeDisposable() }

    init {
        view.showLoadingView(false)
        view.showErrorMessage("")
    }

    fun getAllData() {

        RxJavaPlugins.setErrorHandler {
            it.printStackTrace()
//            Log.e("getAllData", "取API值錯誤，堆棧如下：", it)
            view.showErrorMessage("執行錯誤，請檢查取值網址是否正確。")
        }

        model.getAllDataFromAPI().subscribeOn(io())
            .observeOn(mainThread()).doOnSuccess {
                println("收到的回應是=>$it")
                userList.addAll(it)
                println("添加完畢的userList是=>$userList")
            }.subscribe().addTo(compositeDisposable)
    }

    //找到userList中，是否有輸入的Account和Password的user。
    fun judgeEnterIsInList(account: String, password: String) {
        val user = userList.find { it.account == account && it.password == password }
        if (user != null) { // 登入成功
            waittingFourSecondAndAction(SignStatus(AppConst.LoignStatus.Login, user))
        } else { // 查無此人，登入失敗
            waittingFourSecondAndAction(SignStatus(AppConst.LoignStatus.PasswordError, null))
        }
    }

    // 先找可不可以登入，如果發現帳號相同但密碼不同的話直接顯示帳密錯誤，不然則嘗試註冊後到MainActivity。
    fun tryToLoginOrSignIn(account: String, password: String) {
        val user = userList.find { it.account == account }
        if (user != null && user.password != password) { // 有找到這個user但密碼不同
            waittingFourSecondAndAction(SignStatus(AppConst.LoignStatus.PasswordError, null))
            return
        } else if (user != null && user.password == password) { // 呼叫登入方法
            waittingFourSecondAndAction(SignStatus(AppConst.LoignStatus.Login, user))
        } else {
            // 帳號密碼都不同，執行註冊。
            val newUser = UserModel(account, password)
            model.signUp(newUser).subscribeOn(io())
                .observeOn(mainThread()).doOnSuccess {
                    userList.add(it)
                    println("添加完畢的userList是=>$userList")
                    waittingFourSecondAndAction(SignStatus(AppConst.LoignStatus.SignUp, newUser))
                }.doOnError {
                    waittingFourSecondAndAction(SignStatus(AppConst.LoignStatus.HtmlError, null))
                }.subscribe().addTo(compositeDisposable)
        }
    }

    // 統一在這裡等四秒，裡面判斷是否登入成功。
    private fun waittingFourSecondAndAction(status: SignStatus) {
        Observable.timer(4000L, TimeUnit.MILLISECONDS).observeOn(mainThread()).subscribe(object : Observer<Long> {
            override fun onSubscribe(d: Disposable) {
                println("訂閱成功，即將顯示ProgressBar。 Thread是=>${Thread.currentThread().name}")
                val action = if (status.status == AppConst.LoignStatus.SignUp) "註冊" else "登入"
                // 必須等取值完成以後才顯示進行動作，不然若發生意外錯誤會導致登入動作畫面被該意外錯誤覆蓋。
                Observable.timer(10L, TimeUnit.MILLISECONDS).observeOn(mainThread()).subscribe {
                    view.showLoadingView(true)
                    view.showErrorMessage("${action}中，請稍待。")
                }.addTo(compositeDisposable)
            }

            override fun onNext(i: Long) {
                println("onNext  Thread是=>${Thread.currentThread().name}")
            }

            override fun onComplete() {
                println("等待完成，即將進入登入成功頁面頁面。 Thread是=>${Thread.currentThread().name}")
                view.showLoadingView(false)
                when (status.status) {
                    AppConst.LoignStatus.Login, AppConst.LoignStatus.SignUp -> {
                        view.toLoginSuccessPage(status.user ?: return)
                    }
                    AppConst.LoignStatus.PasswordError -> {
                        view.showErrorMessage("帳號或密碼錯誤。")
                    }
                    AppConst.LoignStatus.HtmlError -> {
                        view.showErrorMessage("執行錯誤，請檢查取值網址是否正確。")
                    }
                }
            }

            override fun onError(e: Throwable) {
                println("等待期間發生錯誤！堆棧如下：Thread是=>${Thread.currentThread().name}")
                e.printStackTrace()
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
        compositeDisposable.clear()
    }

    //現在是登入還是註冊的紀錄class
    data class SignStatus(
        val status: String = "",
        val user: UserModel? = null
    )
}