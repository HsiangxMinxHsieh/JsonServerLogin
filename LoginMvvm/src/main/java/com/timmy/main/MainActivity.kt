package com.timmy.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.timmy.api.model.UserModel
import com.timmy.base.BaseActivity
import com.timmy.login.activity.LoginActivity
import com.timmy.main.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    companion object {
        const val KEY_USER_BUNDLE = "KEY_USER_BUNDLE"
    }

    lateinit var user: UserModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initValue()
        initEvent()
    }

    private fun initData() {
        //邏輯上判斷，不可能存在是null的可能，但還是判斷一下
        user = (intent?.extras?.get(KEY_USER_BUNDLE) as? UserModel) ?: UserModel()
    }

    private fun initValue() {
        mBinding.tvTitle.text = "歡迎！${user.account}"
    }

    private fun initEvent() {
        mBinding.ivBack.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity.finish()
        }
    }
}