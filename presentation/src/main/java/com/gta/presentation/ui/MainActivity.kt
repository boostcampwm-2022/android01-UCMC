package com.gta.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.gta.presentation.R
import com.gta.presentation.databinding.ActivityMainBinding
import com.gta.presentation.secret.NAVER_MAP_CLIENT_ID
import com.gta.presentation.ui.base.BaseActivity
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.fcv_main) as NavHostFragment }
    private val navController by lazy { navHostFragment.navController }
    private val appBarConfiguration = AppBarConfiguration(setOf(R.id.mapFragment, R.id.chattingFragment, R.id.myPageFragment))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.tbMain)

        setupWithBottomNavigation()

        setupWithNaverMaps()
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupWithBottomNavigation() {
        binding.bnvMain.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mapFragment -> {
                    supportActionBar?.hide()
                    showBottomNav()
                }
                R.id.chattingFragment, R.id.myPageFragment -> {
                    supportActionBar?.show()
                    showBottomNav()
                }
                else -> hideBottomNav()
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

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
