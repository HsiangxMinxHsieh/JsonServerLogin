package com.timmy.login.activity

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.timmy.api.model.UserModel
import com.timmy.base.BaseActivity
import com.timmy.login.view.LoginView
import com.timmy.login.viewmodel.LoginViewModel
import com.timmy.main.MainActivity
import com.timmy.main.R
import com.timmy.main.databinding.ActivityLoginBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate), LoginView {

//        private val viewModel: LoginViewModel by lazy { LoginViewModel(this, LoginModelImpl()) } // 無koin
    private val viewModel: LoginViewModel by viewModel { parametersOf(activity) } // 有koin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(activity, R.layout.activity_login)
        initData()
        initEvent()
    }

    private fun initData() {
        viewModel.getAllData()
    }

    private fun initEvent() {
        mBinding.btnLogin.setOnClickListener {
            judgeEnterAndDoAction { viewModel.judgeEnterIsInList(mBinding.edtAccount.content(), mBinding.edtPassword.content()) }
        }
        mBinding.btnSignIn.setOnClickListener {
            judgeEnterAndDoAction { viewModel.tryToLoginOrSignIn(mBinding.edtAccount.content(), mBinding.edtPassword.content()) }
        }
    }

    private fun judgeEnterAndDoAction(action: () -> Unit = {}) {
        if (mBinding.edtAccount.content().isEmpty() || mBinding.edtPassword.content().isEmpty()) {
            Toast.makeText(context, "帳號與密碼皆不可為空。", Toast.LENGTH_SHORT).show()
        } else {
            action.invoke()
        }
    }

    override fun toLoginSuccessPage(user: UserModel) {
        val intent = Intent(activity, MainActivity::class.java)
        intent.putExtra(MainActivity.KEY_USER_BUNDLE, user)
        startActivity(intent)
        activity.finish()
    }

    override fun showErrorMessage(message: String) {
        mBinding.tvError.text = message
    }

    override fun showLoadingView(open: Boolean) {
        mBinding.pgLoading.isVisible = open
    }

    fun EditText.content() = this.text.toString()
}