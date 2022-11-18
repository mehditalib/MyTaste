package com.example.mytaste.async

import android.os.AsyncTask
import com.example.mytaste.geoapify.RetrieveRestaurants
import com.example.mytaste.interfaces.RestaurantChangeListener
import com.example.mytaste.pojo.Restaurant
import com.example.mytaste.pojo.RestaurantsList

class RetrieveRestaurantsAsyncTask : AsyncTask<String, Void, MutableList<Restaurant>> {

    private var listener: RestaurantChangeListener

    constructor(listener: RestaurantChangeListener) {
        this.listener = listener
    }

    override fun onPostExecute(restaurants: MutableList<Restaurant>?) {
        if (restaurants != null) {
            // Restaurants find in the zone
            RestaurantsList.restaurants = restaurants
            listener.onRestaurantRetrieved()
        }
        super.onPostExecute(restaurants)
    }

    override fun doInBackground(vararg coordonnees: String): MutableList<Restaurant> {
        // run geoapify to get restaurants
        return RetrieveRestaurants().getCurrentRestaurants(coordonnees[0].toDouble(), coordonnees[1].toDouble())
    }
}