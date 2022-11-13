package com.example.mytaste.async

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mytaste.pojo.Restaurant
import com.example.mytaste.pojo.RestaurantsList
import com.example.mytaste.utils.MapActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import org.osmdroid.util.GeoPoint


class RetrieveRestaurants : ViewModel() {
    val TAG = "MapActivity"

    fun setCurrentRestaurants(latitude: Double, longitude: Double){
        var client = OkHttpClient().newBuilder().build()
        var lat1 = latitude - 1
        var lat2 = latitude + 1
        var lon1 = longitude - 1
        var lon2 = longitude + 1


        var request: Request = Request.Builder()
            .url("https://api.geoapify.com/v2/places?categories=catering.restaurant&limit=20&filter=rect:$lon1,$lat1,$lon2,$lat2&apiKey=a334d23b1ca34d7fa7bcdc660a9d4813")
            .method("GET", null)
            .build()

        var response: Response = client.newCall(request).execute()
        var jsonObj = JSONTokener(response.body!!.string()).nextValue() as JSONObject
        var datas = jsonObj.getJSONArray("features")
        lateinit var restaurantsList : MutableList<Restaurant>

        for(i in 0 until datas.length()){
            var elem = datas[i] as JSONObject
            if(elem.get("type") == "Feature") {
                var resto = elem.get("properties") as JSONObject
                var resource = (resto.get("datasource") as JSONObject).get("raw") as JSONObject

                var loc = GeoPoint(resto.get("lat") as Double, resto.get("lon") as Double)
                if((elem.get("geometry") as JSONObject).get("type") == "Point"){
                    var coordinates = (elem.get("geometry") as JSONObject).get("coordinates") as Array<Double>
                    loc = GeoPoint(coordinates[1],coordinates[0])
                }

                restaurantsList.add(
                    Restaurant(
                        resto.get("name") as String,
                        loc,
                        resource.get("description") as String,
                        resto.get("address_line2") as String,
                        resource.get("phone") as String,
                        resource.get("email") as String,
                        resource.get("website") as String
                    )
                )
            }
        }


        Log.d(TAG, "setCurrentRestaurants : gata")
        RestaurantsList.restaurants = restaurantsList
    }
    //2.0635913872047054,49.636842412658126,4.0635913872047054,51.636842412658126
    //https://api.geoapify.com/v2/places?categories=catering.restaurant&limit=20&filter=rect:2.0635913872047054,49.636842412658126,4.0635913872047054,51.636842412658126&apiKey=a334d23b1ca34d7fa7bcdc660a9d4813
}