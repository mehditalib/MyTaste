package com.example.mytaste.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytaste.R
import com.example.mytaste.interfaces.RestaurantListener
import com.example.mytaste.pojo.Restaurant
import com.example.mytaste.pojo.RestaurantsList
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DetailsRecyclerViewAdapter : RecyclerView.Adapter<DetailsRecyclerViewAdapter.ViewHolder> {
    private val mListener : RestaurantListener

    constructor(mListener : RestaurantListener){
        this.mListener = mListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant : Restaurant = RestaurantsList.restaurants!![position]

        // Fill TextViews with data
        holder.name.text = restaurant.name
        holder.address.text = restaurant.address
        holder.description.text = restaurant.description
        holder.email.text = restaurant.email
        holder.website.text = restaurant.website
        holder.phone.text = restaurant.phone
        holder.item = restaurant
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ViewHolder {
        // Need a new Holder
        var view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_details, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int { return if(RestaurantsList.restaurants != null) RestaurantsList.restaurants!!.size else 0}

    inner class ViewHolder : RecyclerView.ViewHolder {
        val name : TextView
        val address : TextView
        val description : TextView
        val email : TextView
        val website : TextView
        val phone : TextView
        var button : FloatingActionButton

        lateinit var item : Restaurant

        constructor(view : View) : super(view) {
            // Retrieve all fields to be filled
            name = view.findViewById(R.id.restoNameValue) as TextView
            address = view.findViewById(R.id.restoAddressValue) as TextView
            description = view.findViewById(R.id.restoDescriptionValue) as TextView
            email = view.findViewById(R.id.restoEmailValue) as TextView
            website = view.findViewById(R.id.restoWebsiteValue) as TextView
            phone = view.findViewById(R.id.restoPhoneValue) as TextView
            button = view.findViewById(R.id.buttonSeeMap) as FloatingActionButton

            button.setOnClickListener {
                if (mListener != null) {
                    // If you click on the button you will be redirected to the map
                    mListener.onGoMap(item)
                }
            }
        }
    }
}