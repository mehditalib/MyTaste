package com.example.mytaste.utils

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.mytaste.R
import com.example.mytaste.databinding. ActivityListBinding
import com.example.mytaste.fragments.RestaurantFragmentRecyclerView
import com.example.mytaste.interfaces.RestaurantListener
import com.example.mytaste.pojo.Restaurant
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.firebase.auth.FirebaseAuth


class ListActivity : AppCompatActivity(), RestaurantListener, View.OnClickListener{

    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private lateinit var restaurantFragmentRecyclerView: RestaurantFragmentRecyclerView

    //ViewBinding
    private lateinit var binding: ActivityListBinding
    //ActionBar
    private lateinit var actionBar: ActionBar
    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        //config ActionBar
        actionBar = supportActionBar!!
        actionBar.title = "My Taste"


        //init firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        var intent = intent
        if (intent != null) {
            val extras = intent.extras
            if (extras != null) {
                // Set the container with a recycler view
                restaurantFragmentRecyclerView = RestaurantFragmentRecyclerView(this)
                supportFragmentManager.beginTransaction().add(
                    R.id.containerList,
                    restaurantFragmentRecyclerView
                ).commit()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // refresh our localisation
        getLocation()
    }

    private fun checkUser() {
        //check user login status
        if (firebaseAuth.currentUser == null){
            //user not logged in
            finish()
        }
    }

    private fun getLocation() {
        // Check if we are allowed
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // Ask permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }
        // Get the current localisation
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val locationTask = fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY,object : CancellationToken() {
            override fun isCancellationRequested(): Boolean {
                return false
            }

            override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                return CancellationTokenSource().token
            }
        })

        locationTask.addOnSuccessListener { location ->
            // Store localisation in preferenes to use it in hte ap activity
            var preferences = this.getSharedPreferences("datas", MODE_PRIVATE)
            var editor = preferences.edit()
            var latitude : String
            var longitude : String


            if (location != null) {
                // We have a location
                latitude = locationTask.result.latitude.toString()
                longitude = locationTask.result.longitude.toString()

                editor.putLong("myLat", locationTask.result.latitude.toRawBits())
                editor.putLong("myLon", locationTask.result.longitude.toRawBits())
                editor.putBoolean("myLoc", true)
                // Show the option
                findViewById<View>(R.id.buttonMyPosition).visibility = View.VISIBLE
            } else {
                // We don't have a location use the default values (center of Lille)
                latitude = "50.636842412658126"
                longitude = "3.0635913872047054"
                editor.putBoolean("myLoc", false)
            }

            editor.putString("centerLat", latitude)
            editor.putString("centerLon", longitude)
            editor.commit()
        }

        locationTask.addOnFailureListener { e -> Log.e(this.javaClass.name, "onFailure : " + e.localizedMessage) }
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
        // Logout button
        if (view == findViewById(R.id.buttonDisconnect)) {
            firebaseAuth.signOut()
            checkUser()
        }
        // Open the center on my/default localisation
        else if (view == findViewById(R.id.buttonGoToMap)) {
            var preferences = this.getSharedPreferences("datas", MODE_PRIVATE)
            var editor = preferences.edit()
            var latitude : String
            var longitude : String
            // Recover info from preferences
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
        // optionnal button to make an other request to the api
        else if(view == findViewById(R.id.buttonMyPosition)){
            var preferences = getSharedPreferences("datas", MODE_PRIVATE)

            if(preferences.getBoolean("myLoc", false)) {
                // I have my localisation
                var latitude = Double.Companion.fromBits(preferences.getLong("myLat", 50.636842412658126.toRawBits())).toString()
                var longitude = Double.Companion.fromBits(preferences.getLong("myLon", 3.0635913872047054.toRawBits())).toString()

                // update data
                var editor = preferences.edit()
                editor.putString("centerLat", latitude)
                editor.putString("centerLon", longitude)
                editor.commit()

                restaurantFragmentRecyclerView.setCoord(latitude, longitude)
                restaurantFragmentRecyclerView.getRestaurants()
            }
        }
    }

    // Click on the a holder button. Open map Activity center on the restaurants
    override fun onGoMap(r: Restaurant) {
        var preferences = this.getSharedPreferences("datas", MODE_PRIVATE)
        var editor = preferences.edit()
        var latitude = r.localisation.latitude.toString()
        var longitude = r.localisation.longitude.toString()

        editor.putString("centerLat", latitude)
        editor.putString("centerLon", longitude)
        editor.commit()

        startActivity(Intent(this, MapActivity::class.java))
        return
    }
}