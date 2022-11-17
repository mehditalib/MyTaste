package com.example.mytaste.utils

import android.annotation.SuppressLint
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore.Images.Media.getBitmap
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
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

    private val TAG = "MapActivity"


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(this.javaClass.name, "Call constructor")

        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        setContentView(R.layout.activity_map)
        map = findViewById(R.id.worldMap)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
        map.setMultiTouchControls(true)
        map.controller.setZoom(18.0)

        var preferences = getSharedPreferences("datas", MODE_PRIVATE)
        //map.controller.setCenter(GeoPoint(Double.Companion.fromBits(preferences.getLong("centerLat", 50.636842412658126.toRawBits())), Double.Companion.fromBits(preferences.getLong("centerLon", 3.0635913872047054.toRawBits()))))
        Log.d(this.javaClass.name, "${preferences.getString("centerLat", "50.636842412658126")!!.toDouble()}  ${preferences.getString("centerLon", "3.0635913872047054")!!.toDouble()}")
        map.controller.setCenter(GeoPoint(preferences.getString("centerLat", "50.636842412658126")!!.toDouble(), preferences.getString("centerLon", "3.0635913872047054")!!.toDouble()))
        displayRestaurants()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {

        Log.d(this.javaClass.name, "Resume")
        super.onResume()
        map.onResume()
        /*var preferences = getSharedPreferences("datas", MODE_PRIVATE)
        ///map.controller.setCenter(GeoPoint(Double.Companion.fromBits(preferences.getLong("centerLat", 50.636842412658126.toRawBits())), Double.Companion.fromBits(preferences.getLong("centerLon", 3.0635913872047054.toRawBits()))))
        Log.d(this.javaClass.name, "AnimateTo my position")
        map.controller.animateTo(GeoPoint(preferences.getString("centerLat", "50.636842412658126")!!.toDouble(), preferences.getString("centerLon", "3.0635913872047054")!!.toDouble()))
        displayRestaurants()*/
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayMyLocation(){
        Log.d(this.javaClass.name, "dispayMyLoc")

        var preferences = getSharedPreferences("datas", MODE_PRIVATE)
        if(preferences.getBoolean("myLoc", false)) {
            findViewById<View>(R.id.backToMyPisition).visibility = View.VISIBLE

            val myLocationOverlay = MyLocationNewOverlay(map)
            myLocationOverlay.enableMyLocation()
            val marker : Marker = Marker(map);
            marker.position = GeoPoint(Double.Companion.fromBits(preferences.getLong("myLat", 50.636842412658126.toRawBits())), Double.Companion.fromBits(preferences.getLong("myLon", 3.0635913872047054.toRawBits())))
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.icon = resources.getDrawable(R.drawable.ic_mapa)
            marker.isDraggable = true;
            map.overlays.add(marker)
        }
    }

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

        displayMyLocation()
    }

    override fun onClick(view: View) {
        if(view == findViewById(R.id.buttonReturnList)){
            this.finish()
        }
        else if(view == findViewById(R.id.backToMyPisition)){
            Log.d(this.javaClass.name, "onClick go to my position")
            var preferences = getSharedPreferences("datas", MODE_PRIVATE)
            if(preferences.getBoolean("myLoc", false)) {
                map.controller.animateTo(GeoPoint(Double.Companion.fromBits(preferences.getLong("myLat", 50.636842412658126.toRawBits())), Double.Companion.fromBits(preferences.getLong("myLon", 3.0635913872047054.toRawBits()))))
            }
            else{
                map.controller.animateTo(GeoPoint(50.636842412658126,3.0635913872047054))
            }
        }
    }
}