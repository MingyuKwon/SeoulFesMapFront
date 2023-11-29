package com.example.seoulfesmap

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.seoulfesmap.Data.RetrofitClient
import com.example.seoulfesmap.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_profile
            )
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home -> {
                }
                R.id.navigation_dashboard -> {
                }
                R.id.navigation_notifications -> {
                }
                R.id.navigation_profile -> {
                }
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        loadDataFromServer()
    }

    private fun loadDataFromServer() {
        lifecycleScope.launch {
            showLoading(true)
            RetrofitClient.InitFesDataList()
            RetrofitClient.initVisitedFes()
            RetrofitClient.initChallenge()
            showLoading(false)

        }
    }

    fun showLoading(open : Boolean)
    {
        if(open)
        {
            binding.loadingContainer.visibility = View.VISIBLE
        }else
        {
            binding.loadingContainer.visibility = View.GONE

        }
    }

}