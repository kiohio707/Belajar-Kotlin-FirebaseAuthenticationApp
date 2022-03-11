package com.example.firebaseauthenticationapp.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.example.firebaseauthenticationapp.R
import com.example.firebaseauthenticationapp.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    lateinit var auth: FirebaseAuth
    lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()

        setContentView(binding.root)

        setSupportActionBar(binding.tb)
        binding.tb.setTitleTextColor(Color.WHITE)


        val navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment).navController

        /*val appBarConfiguration =
            AppBarConfiguration.Builder(R.id.menu_home, R.id.menu_profile).build()*/

        appBarConfiguration =
            AppBarConfiguration(navController.graph)


        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.botNav.setupWithNavController(navController)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                auth.signOut()
                Intent(this@HomeActivity, LoginActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
                true
            }
            else -> return true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_container)

        return navController.navigateUp()
    }

}