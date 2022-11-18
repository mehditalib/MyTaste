package com.example.mytaste.interfaces

import com.example.mytaste.pojo.Restaurant

interface RestaurantListener {
    // Listener on holders, open the map center on the restaurant precise
    fun onGoMap(r : Restaurant)
}