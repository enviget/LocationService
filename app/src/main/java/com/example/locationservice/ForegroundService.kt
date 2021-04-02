package com.example.locationservice

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*

class ForegroundService : Service() {

    var myLong: Double = 0.0
    var myLat: Double = 0.0
    val NOTIFICATION_CHANNEL_ID = 1234
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        requestLocationUpdates()
    }

    private fun requestLocationUpdates() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val permission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        val location: Location = locationResult.lastLocation
                        myLat = location.latitude
                        myLong = location.longitude
                        sendBroadcast()

                        Log.d("Location Service", "location update $location")
                    }
                }, null
            )
        }
    }

    fun sendBroadcast(){
        val broadcastIntent = Intent()
        broadcastIntent.action = "location data"
        broadcastIntent.putExtra("latitude", myLat)
        broadcastIntent.putExtra("longitude", myLong)
        broadcastIntent.setClass(this, MyMap::class.java)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {

        val channelName = "location service"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channelId = "serviceeee"
        val channel = NotificationChannel(channelId, channelName, importance)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        var intent = Intent(this, MyMap::class.java)
        var pendingIntent = PendingIntent.getActivity(this, 22, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, channelId)
        val notification: Notification =
            builder
                .setOngoing(true)
                .setContentInfo("Background location service is running")
                .setContentTitle("Location service")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .build()



        startForeground(NOTIFICATION_CHANNEL_ID, notification)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}