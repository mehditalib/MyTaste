package com.example.mytaste.geoapify

import android.content.Context
import com.example.mytaste.pojo.Restaurant
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import org.osmdroid.util.GeoPoint


class RetrieveRestaurants {
    lateinit var context : Context

    fun getCurrentRestaurants(latitude : Double, longitude : Double) : MutableList<Restaurant> {
        // request settings  Find 200 restaurants in a large zoe around the center
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

        // Recover data for the application
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
}