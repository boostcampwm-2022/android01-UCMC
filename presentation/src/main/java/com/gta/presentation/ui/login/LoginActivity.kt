package com.gta.presentation.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.gta.presentation.databinding.ActivityLoginBinding
import com.gta.presentation.ui.MainActivity
import com.gta.presentation.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    private val viewModel: LoginViewModel by viewModels()

    private val requestActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val authIntent = it.data ?: return@registerForActivityResult
            val account = Auth.GoogleSignInApi.getSignInResultFromIntent(authIntent)?.signInAccount
            viewModel.signinWithToken(account?.idToken)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCollector()
        binding.btnLoginGoogle.setOnClickListener {
            googleLogin()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkCurrentUser()
    }

    private fun initCollector() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginEvent.collectLatest { state ->
                    if (state) {
                        startMainActivity()
                    }
                }
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun googleLogin() {
        requestActivity.launch(googleSignInClient.signInIntent)
    }
}
