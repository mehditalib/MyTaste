package com.example.mytaste.interfaces

import com.example.mytaste.pojo.Restaurant

interface RestaurantListener {
    fun onGoMap(r : Restaurant)
}