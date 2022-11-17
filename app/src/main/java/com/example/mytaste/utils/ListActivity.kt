package com.example.mytaste.utils

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.example.mytaste.R
import com.example.mytaste.async.RetrieveRestaurantsAsyncTask
import com.example.mytaste.fragments.RestaurantFragmentRecyclerView
import com.example.mytaste.geoapify.RetrieveRestaurants
import com.example.mytaste.interfaces.RestaurantListener
import com.example.mytaste.pojo.Restaurant
import com.example.mytaste.pojo.RestaurantsList
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import io.ktor.http.*


class ListActivity : AppCompatActivity(), RestaurantListener, View.OnClickListener{

    private val TAG = "ListActivity"
    lateinit var fusedLocationProviderClient : FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maintmp)

        var intent = intent
        if (intent != null) {
            val extras = intent.extras
            if (extras != null) {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                supportFragmentManager.beginTransaction().add(
                    R.id.containerList,
                    RestaurantFragmentRecyclerView()
                ).commit()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        getLocation()

    }

    private fun getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }
        val locationTask = fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY,object : CancellationToken() {
            override fun isCancellationRequested(): Boolean {
                return false
            }

            override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                return CancellationTokenSource().token
            }
        })

        locationTask.addOnSuccessListener { location ->
            var preferences = this.getSharedPreferences("datas", MODE_PRIVATE)
            var editor = preferences.edit()
            var latitude : String
            var longitude : String

            if (location != null) {
                // We have a location
                Log.d(
                    TAG,
                    "onSuccess find Localisation : " + locationTask.result.latitude + ", " + locationTask.result.longitude
                )
                latitude = locationTask.result.latitude.toString()
                longitude = locationTask.result.longitude.toString()
                editor.putLong("myLat", locationTask.result.latitude.toRawBits())
                editor.putLong("myLon", locationTask.result.longitude.toRawBits())
                editor.putBoolean("myLoc", true)
            } else {
                Log.d(TAG, "onSuccess: Location was null...")
                latitude = "50.636842412658126"
                longitude = "3.0635913872047054"
                editor.putBoolean("myLoc", false)
            }
            editor.putString("centerLat", latitude)
            editor.putString("centerLon", longitude)
            editor.commit()
        }

        locationTask.addOnFailureListener { e -> Log.e(TAG, "onFailure : " + e.localizedMessage) }
        return
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Permission granted
                getLocation()
            }
        }
    }

    override fun onClick(view: View) {
        if (view == findViewById(R.id.buttonDisconnect)) {
            TODO("Implement logout")
            return
        }
        if (view == findViewById(R.id.buttonGoToMap)) {
            var preferences = this.getSharedPreferences("datas", MODE_PRIVATE)
            var editor = preferences.edit()
            var latitude : String
            var longitude : String
            if(preferences.getBoolean("myLoc", false)) {
                latitude = Double.Companion.fromBits(preferences.getLong("myLat", 50.636842412658126.toRawBits())).toString()
                longitude = Double.Companion.fromBits(preferences.getLong("myLon", 3.0635913872047054.toRawBits())).toString()
            }
            else{
                latitude = "50.636842412658126"
                longitude = "3.0635913872047054"
            }

            editor.putString("centerLat", latitude)
            editor.putString("centerLon", longitude)
            editor.commit()
            startActivity(Intent(this, MapActivity::class.java))
            return
        }

    }

    override fun onGoMap(r: Restaurant) {
        var preferences = this.getSharedPreferences("datas", MODE_PRIVATE)
        var editor = preferences.edit()
        var latitude = r.localisation.latitude.toString()
        var longitude = r.localisation.longitude.toString()
        Log.d(this.javaClass.name, "lat : ${r.localisation}")
        editor.putString("centerLat", latitude)
        editor.putString("centerLon", longitude)
        editor.commit()

        startActivity(Intent(this, MapActivity::class.java))
        return
    }
}