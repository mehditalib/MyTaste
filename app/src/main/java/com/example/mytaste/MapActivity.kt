package com.example.mytaste

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.ItemizedIconOverlay

import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.OverlayItem

class MapActivity : AppCompatActivity() {
    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(
            applicationContext))
        setContentView(R.layout.activity_map)
        map = findViewById<MapView>(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setBuiltInZoomControls(true);
        var startPoint = GeoPoint(43.65020, 7.00517)
        var mapController : IMapController = map.controller
        mapController.setZoom(18.0)
        mapController.setCenter(startPoint)

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

}