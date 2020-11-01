package com.bytecoders.iptvservice.mobileconfig

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    val viewModel: MainActivityViewModel by viewModels { MainActivityViewModelFactory(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

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
