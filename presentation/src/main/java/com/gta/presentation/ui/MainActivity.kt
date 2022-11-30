package com.gta.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.gta.presentation.R
import com.gta.presentation.databinding.ActivityMainBinding
import com.gta.presentation.secret.NAVER_MAP_CLIENT_ID
import com.gta.presentation.ui.base.BaseActivity
import com.gta.presentation.ui.login.LoginActivity
import com.gta.presentation.util.repeatOnStarted
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val viewModel: MainViewModel by viewModels()

    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.fcv_main) as NavHostFragment }
    private val navController by lazy { navHostFragment.navController }
    private val appBarConfiguration =
        AppBarConfiguration(setOf(R.id.mapFragment, R.id.chattingListFragment, R.id.myPageFragment))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCollector()
        setSupportActionBar(binding.tbMain)
        setupWithBottomNavigation()
        setupWithNaverMaps()
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun initCollector() {
        repeatOnStarted(this) {
            viewModel.changeAuthStateEvent.collectLatest { state ->
                if (state) {
                    startLoginActivity()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_top_notification -> {
                Snackbar.make(binding.bnvMain, "클릭", Snackbar.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupWithBottomNavigation() {
        binding.bnvMain.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mapFragment -> {
                    supportActionBar?.hide()
                    showBottomNav()
                }
                R.id.chattingListFragment, R.id.myPageFragment -> {
                    supportActionBar?.show()
                    showBottomNav()
                }
                R.id.paymentFragment -> {
                    supportActionBar?.hide()
                    hideBottomNav()
                }
                else -> {
                    supportActionBar?.show()
                    hideBottomNav()
                }
            }
        }
    }

    private fun setupWithNaverMaps() {
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(NAVER_MAP_CLIENT_ID)
    }

    private fun showBottomNav() {
        binding.bnvMain.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        binding.bnvMain.visibility = View.GONE
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
