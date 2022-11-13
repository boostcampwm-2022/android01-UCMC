package com.gta.presentation.ui

import android.content.Intent
import android.os.Bundle
import com.gta.presentation.databinding.ActivityLoginBinding
import com.gta.presentation.ui.base.BaseActivity

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startMainActivity()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
