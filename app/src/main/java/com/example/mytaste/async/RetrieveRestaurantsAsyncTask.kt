package com.example.mytaste.async

import android.os.AsyncTask
import android.util.Log
import com.example.mytaste.geoapify.RetrieveRestaurants
import com.example.mytaste.interfaces.RestaurantChangeListener
import com.example.mytaste.pojo.Restaurant
import com.example.mytaste.pojo.RestaurantsList
import com.example.mytaste.utils.MyTasteApplication

class RetrieveRestaurantsAsyncTask : AsyncTask<String, Void, MutableList<Restaurant>> {

    private var listener: RestaurantChangeListener

    constructor(listener: RestaurantChangeListener) {
        this.listener = listener
    }

    override fun onPostExecute(restaurants: MutableList<Restaurant>?) {
        if (restaurants != null) {
            RestaurantsList.restaurants = restaurants
            listener.onRestaurantRetrieved()
            Log.d("RetrieveTweetsAsyncTask", "Nombre de restaurants : " + restaurants.size);
        }
        super.onPostExecute(restaurants)
    }

    override fun doInBackground(vararg coordonnees: String): MutableList<Restaurant> {
        return RetrieveRestaurants().getCurrentRestaurants()
    }
}