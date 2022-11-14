package com.gta.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.gta.presentation.R
import com.gta.presentation.databinding.ActivityMainBinding
import com.gta.presentation.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.fcv_main) as NavHostFragment }
    private val navController by lazy { navHostFragment.navController }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.tbMain)

        setupWithBottomNavigation()

        setupWithAppBar()
    }

    private fun setupWithAppBar() {
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.mapFragment, R.id.chattingFragment, R.id.myPageFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupWithBottomNavigation() {
        binding.bnvMain.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mapFragment, R.id.chattingFragment, R.id.myPageFragment -> showBottomNav()
                else -> hideBottomNav()
            }
        }
    }

    private fun showBottomNav() {
        binding.bnvMain.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        binding.bnvMain.visibility = View.GONE
    }
}
