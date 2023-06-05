package com.example.appFinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.appFinal.databinding.ActivityHomeBinding

class Home : AppCompatActivity() {

    //初始化
    private lateinit var binding : ActivityHomeBinding
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //顯示layout，先顯示HomePage
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //切換不同頁面功能
        navController = Navigation.findNavController(this, R.id.activity_home_nav_fragment)
        setupWithNavController(binding.bottomNavigationView, navController)
    }
}