package com.bytecoders.iptvservice.mobileconfig

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
        viewModel = ViewModelProvider(this, MainActivityViewModelFactory(application))
                .get(MainActivityViewModel::class.java)

        when(intent?.action) {
            Intent.ACTION_SEND -> {
                Log.d(TAG, "Got intent single item $intent")
                intent.getStringExtra(Intent.EXTRA_TEXT)?.let(viewModel::validateURL)
            }
            Intent.ACTION_SEND_MULTIPLE -> {
                Log.d(TAG, "Got intent with multiple items $intent this is now unsupported")
            }
            else -> {
                // Handle other intents, such as being started from the home screen
            }
        }
    }

}
