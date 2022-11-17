package com.example.mytaste.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.mytaste.R
import com.example.mytaste.adapters.RestaurantsAdapter
import com.example.mytaste.async.RetrieveRestaurantsAsyncTask
import com.example.mytaste.interfaces.RestaurantChangeListener
import com.example.mytaste.interfaces.RestaurantListener
import com.example.mytaste.pojo.RestaurantsList
import com.example.mytaste.utils.MyTasteApplication

class RestaurantDetailsFragment : RestaurantChangeListener, Fragment{
    private lateinit var recyclerView : RecyclerView
    private lateinit var listener : RestaurantListener
    private lateinit var mRestaurantAsyncTask : RetrieveRestaurantsAsyncTask
    private lateinit var listView : ListView

    constructor(){}


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_list, container, false)

        if(view is RecyclerView) {
            recyclerView = view
            recyclerView.adapter = DetailsRecyclerViewAdapter(listener)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onRestaurantRetrieved() {
        if(RestaurantsList.restaurants != null){
            listView.adapter = RestaurantsAdapter()
        }
    }
}