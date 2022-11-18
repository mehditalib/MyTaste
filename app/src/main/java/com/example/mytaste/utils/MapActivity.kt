package com.example.mytaste.utils

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.mytaste.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import com.example.mytaste.pojo.RestaurantsList
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity(), View.OnClickListener{
    lateinit var map: MapView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //config ActionBar
        supportActionBar!!.title= "Carte"

        // Set the map
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        setContentView(R.layout.activity_map)
        map = findViewById(R.id.worldMap)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
        map.setMultiTouchControls(true)
        map.controller.setZoom(18.0)

        // Center the map on the asked point
        var preferences = getSharedPreferences("datas", MODE_PRIVATE)
        map.controller.setCenter(GeoPoint(preferences.getString("centerLat", "50.636842412658126")!!.toDouble(), preferences.getString("centerLon", "3.0635913872047054")!!.toDouble()))
        displayRestaurants()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    // Display a custom ping for my localisation
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayMyLocation(){

        val preferences = getSharedPreferences("datas", MODE_PRIVATE)
        if(preferences.getBoolean("myLoc", false)) {
            // I have a location
            // show the optionnal button
            findViewById<View>(R.id.backToMyPisition).visibility = View.VISIBLE

            val myLocationOverlay = MyLocationNewOverlay(map)
            myLocationOverlay.enableMyLocation()
            val marker : Marker = Marker(map);
            marker.position = GeoPoint(Double.Companion.fromBits(preferences.getLong("myLat", 50.636842412658126.toRawBits())), Double.Companion.fromBits(preferences.getLong("myLon", 3.0635913872047054.toRawBits())))
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            // Recover the custom marker
            marker.icon = resources.getDrawable(R.drawable.ic_mapa)
            marker.isDraggable = true;
            map.overlays.add(marker)
        }
    }

    // represente all the restaurants on the map
    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayRestaurants(){
        var items = ArrayList<OverlayItem>()

        if(RestaurantsList.restaurants != null) {
            for (i in RestaurantsList.restaurants!!.indices) {
                var newOverlayItem = OverlayItem(
                    RestaurantsList.restaurants!![i].name,
                    RestaurantsList.restaurants!![i].address,
                    RestaurantsList.restaurants!![i].localisation
                )

                if (!items.contains(newOverlayItem)) {
                    items.add(newOverlayItem)
                }
            }
        }

        val listenerInter = object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
            // We can single tap
            override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
                return true
            }
            // We can't double tap
            override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                return false
            }
        }

        var mOverlay =
            ItemizedOverlayWithFocus<OverlayItem>(applicationContext, items, listenerInter)

        mOverlay.setFocusItemsOnTap(true)
        map.overlays.add(mOverlay)

        displayMyLocation()
    }

    override fun onClick(view: View) {
        // We want to see the list. Go to the previous activity
        if(view == findViewById(R.id.buttonReturnList)){
            this.finish()
        }
        // We want to center the map on our location
        else if(view == findViewById(R.id.backToMyPisition)){
            var preferences = getSharedPreferences("datas", MODE_PRIVATE)
            if(preferences.getBoolean("myLoc", false)) {
                map.controller.animateTo(GeoPoint(Double.Companion.fromBits(preferences.getLong("myLat", 50.636842412658126.toRawBits())), Double.Companion.fromBits(preferences.getLong("myLon", 3.0635913872047054.toRawBits()))))
            }
            else{
                // If we don't have a location use the default point
                map.controller.animateTo(GeoPoint(50.636842412658126,3.0635913872047054))
            }
        }
    }
}