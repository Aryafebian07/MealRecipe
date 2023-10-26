package com.niveon.mealrecipe.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.niveon.mealrecipe.R
import com.niveon.mealrecipe.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var navHost: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHost = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        binding.bottomNav.setupWithNavController(navHost.navController)
        navHost.navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.detailFragment) {
                binding.bottomNav.visibility = View.GONE
            } else {
                binding.bottomNav.visibility = View.VISIBLE
            }
        }
    }
    override fun onNavigateUp(): Boolean {
        return navHost.navController.navigateUp() || super.onNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}