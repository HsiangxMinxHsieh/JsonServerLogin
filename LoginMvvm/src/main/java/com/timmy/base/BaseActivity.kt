package com.timmy.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding


abstract class BaseActivity<B : ViewBinding>(val bindingFactory: (LayoutInflater) -> B) : AppCompatActivity() {
    lateinit var mBinding: B

    open val activity by lazy { this }
    open val context: Context by lazy { this }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //隱藏標題列
        supportActionBar?.hide()
        mBinding = bindingFactory(layoutInflater)
        setContentView(mBinding.root)
    }
}