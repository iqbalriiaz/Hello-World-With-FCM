package com.iqbalriiaz.helloworldwithfcm

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.iqbalriiaz.helloworldwithfcm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationManager: NotificationManagerCompat

    // Set up the activity
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up click listener for changing Text View
        binding.btnChangeText.setOnClickListener {
            binding.dynamicTextView.setText(R.string.changed_text)
        }

        // Set up click listener for triggering push notification
        binding.btnSendNotification.setOnClickListener {
            if (isInternetConnected()) {
                subscribeToTopic()
                Log.d("MainActivity", "btnSendNotification clicked")
            } else {
                "No internet connection".showToast()
            }
        }

        // Create notification manager
        notificationManager = NotificationManagerCompat.from(this)

        // Create notification channel for Android 8.0 and above
        createNotificationChannel()
    }

    // Subscribe to a Firebase Messaging topic
    private fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("test_topic")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("MainActivity", "Subscribed to topic")
                    sendTestPushNotification()
                } else {
                    Log.e("MainActivity", "Failed to subscribe to topic", task.exception)
                }
            }
    }

    // Send a test push notification
    private fun sendTestPushNotification() {
        // Create notification builder
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Test Notification")
            .setContentText("This is a test push notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Show the notification
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    // Create a notification channel for Android 8.0 and above
    private fun createNotificationChannel() {
        // Check if the device is running Android 8.0 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Channel"
            val description = "Channel for test notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                this.description = description
            }

            // Register the notification channel with the system
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Check if the device is connected to the internet
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun isInternetConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Extension function to show a Toast message
    private fun String.showToast() {
        Toast.makeText(this@MainActivity, this, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val CHANNEL_ID = "test_channel"
        private const val NOTIFICATION_ID = 1
    }
}
