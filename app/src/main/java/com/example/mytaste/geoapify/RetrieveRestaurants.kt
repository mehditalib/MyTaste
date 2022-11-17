package com.example.mytaste.geoapify

import android.util.Log
import androidx.lifecycle.ViewModel
import android.content.SharedPreferences
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.mytaste.BuildConfig.DEBUG
import com.example.mytaste.pojo.Restaurant
import com.example.mytaste.pojo.RestaurantsList
import com.example.mytaste.utils.MyTasteApplication
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import org.osmdroid.util.GeoPoint
import kotlin.properties.Delegates


class RetrieveRestaurants {
    val TAG = "RetrieveRestaurants"
    lateinit var context : Context

    fun getCurrentRestaurants() : MutableList<Restaurant> {

        //var preferences: SharedPreferences? = MyTasteApplication().getPreferences()
        var latitude : Double = 50.636842412658126
        var longitude : Double = 3.06359138720470

        /*if (preferences != null && preferences!!.getBoolean("myLoc", false)) {
            latitude = Double.Companion.fromBits(preferences!!.getLong("centerLat", 50.636842412658126.toRawBits()))
            longitude = Double.Companion.fromBits(preferences!!.getLong("centerLon", 3.0635913872047054.toRawBits()))
        }*/

        Log.d(TAG, "Start API get Resto $latitude $longitude")

        var client = OkHttpClient().newBuilder().build()
        var lat1 = latitude - 1
        var lat2 = latitude + 1
        var lon1 = longitude - 1
        var lon2 = longitude + 1


        var request: Request = Request.Builder()
            .url("https://api.geoapify.com/v2/places?categories=catering.restaurant&limit=200&filter=rect:$lon1,$lat1,$lon2,$lat2&apiKey=a334d23b1ca34d7fa7bcdc660a9d4813")
            .method("GET", null)
            .build()

        var response: Response = client.newCall(request).execute()
        var jsonObj = JSONTokener(response.body!!.string()).nextValue() as JSONObject
        var datas = jsonObj.getJSONArray("features")
        var restaurantsList : MutableList<Restaurant> = mutableListOf<Restaurant>()

        for(i in 0 until datas.length()){
            var elem = datas[i] as JSONObject
            if(elem.has("type") && elem.get("type") == "Feature" && elem.has("properties")) {
                var resto = elem.get("properties") as JSONObject

                var resource : JSONObject = if(resto.has("datasource")){
                    (resto.get("datasource") as JSONObject).get("raw") as JSONObject
                } else{
                    JSONObject()
                }

                if(elem.has("geometry") || resto.has("lat") && resto.has("lon")) {
                    var loc = if (elem.has("geometry") && (elem.get("geometry") as JSONObject).get("type") == "Point") {
                        var coordinates = (elem.get("geometry") as JSONObject).get("coordinates") as JSONArray
                        GeoPoint(coordinates[1] as Double, coordinates[0] as Double)
                    } else {
                        GeoPoint(resto.get("lat") as Double, resto.get("lon") as Double)
                    }

                    if (resto.has("name")) {

                        var newResto = Restaurant(
                            resto.get("name") as String,
                            loc,
                            if(resource.has("description")) resource.get("description") as String else "",
                            if(resto.has("address_line2")) resto.get("address_line2") as String else "",
                            if(resource.has("phone")) resource.get("phone") as String else "",
                            if(resource.has("email")) resource.get("email") as String else "",
                            if(resource.has("website")) resource.get("website") as String else ""
                        )

                        if(!restaurantsList.contains(newResto)){
                            restaurantsList.add(newResto)
                        }
                    }
                }
            }
        }

        return restaurantsList
    }
    //2.0635913872047054,49.636842412658126,4.0635913872047054,51.636842412658126
    //https://api.geoapify.com/v2/places?categories=catering.restaurant&limit=20&filter=rect:2.0635913872047054,49.636842412658126,4.0635913872047054,51.636842412658126&apiKey=a334d23b1ca34d7fa7bcdc660a9d4813
}