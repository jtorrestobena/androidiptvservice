package com.bytecoders.iptvservice.mobileconfig

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        viewModel = ViewModelProvider(this, MainActivityViewModelFactory(application))
                .get(MainActivityViewModel::class.java)

        viewModel.iptvClient.clientServiceLifecycle.observe(this, Observer{
            Toast.makeText(applicationContext, "Status: $it", Toast.LENGTH_SHORT).show()
        })

        viewModel.iptvClient.messagesLiveData.observe(this, Observer {
            Toast.makeText(applicationContext, "Message: $it", Toast.LENGTH_SHORT).show()
        })
    }

}
