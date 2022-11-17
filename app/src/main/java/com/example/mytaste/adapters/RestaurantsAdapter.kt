package com.example.mytaste.adapters

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.mytaste.R
import com.example.mytaste.pojo.Restaurant
import com.example.mytaste.pojo.RestaurantsList
import com.example.mytaste.utils.MyTasteApplication
import java.security.AccessController.getContext
import java.util.Objects

class RestaurantsAdapter : BaseAdapter {
    private var inflater : LayoutInflater = LayoutInflater.from(MyTasteApplication().getContext())

    constructor()

    override fun getCount(): Int {
        return if(RestaurantsList.restaurants != null){
            RestaurantsList.restaurants!!.size
        } else {
            0
        }
    }

    override fun getItem(p0: Int): Any? {
        return if(RestaurantsList.restaurants != null){
            RestaurantsList.restaurants!![p0]
        } else {
            null
        }
    }

    override fun getItemId(p0: Int): Long {
        return p0 as Long
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view = inflater.inflate(R.layout.fragment_details, null)
        val restaurant = getItem(p0) as Restaurant

        val name : TextView = view.findViewById(R.id.restoNameValue)
        val address : TextView = view.findViewById(R.id.restoAddressValue)
        val description : TextView = view.findViewById(R.id.restoDescriptionValue)
        val email : TextView = view.findViewById(R.id.restoEmailValue)
        val website : TextView = view.findViewById(R.id.restoEmailValue)
        val call : TextView = view.findViewById(R.id.restoPhoneValue)

        name.text = restaurant.name
        address.text = restaurant.address
        description.text = restaurant.description
        email.text = restaurant.email
        website.text = restaurant.website
        call.text = restaurant.phone

        return view
    }
}