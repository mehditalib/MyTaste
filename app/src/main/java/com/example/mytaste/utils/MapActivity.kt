package com.example.mytaste.utils

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import com.example.mytaste.async.RetrieveRestaurants
import com.example.mytaste.pojo.RestaurantsList


class MapActivity : AppCompatActivity() {
    lateinit var map: MapView
    lateinit var viewModel: RetrieveRestaurants

    private val TAG = "MapActivity"
    private val LOCATION_REQUEST_CODE = 10001
    lateinit var fusedLocationProviderClient : FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        setContentView(com.example.mytaste.R.layout.activity_map)
        map = findViewById<MapView>(com.example.mytaste.R.id.worldMap)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setBuiltInZoomControls(true);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        var mapController : IMapController = map.controller
        mapController.setZoom(18.0)

        var items = ArrayList<OverlayItem>()
        var home = OverlayItem("Rallo's office", "my office", GeoPoint(43.65020, 7.00517))
        //var m :  Drawable = home.getMarker(0)
        items.add(home)
        items.add(OverlayItem("Resto", "Chez Babar", GeoPoint(43.64950, 7.00517)))
        val listenerInter = object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem>{
            override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
                return true
            }

            override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                return false
            }
        }
        var mOverlay = ItemizedOverlayWithFocus<OverlayItem>(applicationContext, items, listenerInter)

        mOverlay.setFocusItemsOnTap(true)
        map.overlays.add(mOverlay)
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onStart() {
        super.onStart()
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation()
        }
        else{
            askLocationPermission()
        }
    }

    private fun getLastLocation(){
        Log.d(TAG, "Start lastLocation")
        //val locationTask = fusedLocationProviderClient.lastLocation
        val locationTask = fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, object : CancellationToken(){
            override fun isCancellationRequested(): Boolean {
                return false
            }

            override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                return CancellationTokenSource().token
            }
        })

        locationTask.addOnSuccessListener { location ->
            if (location != null) {
                // We have a location
                Log.d(TAG, "onSuccess : " + locationTask.result.latitude + ", " + locationTask.result.longitude)
                map.controller.setCenter(GeoPoint(locationTask.result.latitude, locationTask.result.longitude))

                val myLocationoverlay = MyLocationNewOverlay(map)
                myLocationoverlay.enableFollowLocation()
                myLocationoverlay.enableMyLocation()

                val icon  = Bitmap.createBitmap(50,50, Bitmap.Config.ARGB_8888, true)
                val paint = Paint()
                val iconT = Canvas(icon)
                iconT.drawColor(Color.TRANSPARENT)
                paint.color = Color.argb(255, 0,111,222)
                iconT.drawCircle(25F, 25F, 25F, paint)
                myLocationoverlay.setDirectionArrow(icon, icon)
                myLocationoverlay.setPersonIcon(icon)
                map.overlays.add(myLocationoverlay)

                displayRestaurants(locationTask.result.latitude, locationTask.result.longitude)

            } else {
                Log.d(TAG, "onSuccess: Location was null...")
                map.controller.setCenter(GeoPoint(50.636842412658126, 3.0635913872047054))
                displayRestaurants(50.636842412658126, 3.0635913872047054)
            }
        }

        locationTask.addOnFailureListener { e -> Log.e(TAG, "onFailure : " + e.localizedMessage) }
    }

    private fun askLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                Log.d(TAG, "askLocationPermission: you should show an alert dialog...")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        }
        else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == LOCATION_REQUEST_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Permission granted
                Log.d(TAG, "Start onRequest")
                getLastLocation()
            }
            else{
                // Permission not granted
                map.controller.setCenter(GeoPoint(50.636842412658126, 3.0635913872047054))
                displayRestaurants(50.636842412658126, 3.0635913872047054)
            }
        }
    }

    fun displayRestaurants(latitude : Double, longitude : Double){
        Thread {
            RetrieveRestaurants().setCurrentRestaurants(latitude, longitude)
            var items = ArrayList<OverlayItem>()

            for (i in RestaurantsList.restaurants.indices) {
                items.add(
                    OverlayItem(
                        RestaurantsList.restaurants[i].name,
                        RestaurantsList.restaurants[i].address,
                        RestaurantsList.restaurants[i].localisation
                    )
                )
            }

            val listenerInter = object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
                    return true
                }

                override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                    return false
                }
            }

            var mOverlay =
                ItemizedOverlayWithFocus<OverlayItem>(applicationContext, items, listenerInter)

            mOverlay.setFocusItemsOnTap(true)
            map.overlays.add(mOverlay)
        }.start()
    }
}