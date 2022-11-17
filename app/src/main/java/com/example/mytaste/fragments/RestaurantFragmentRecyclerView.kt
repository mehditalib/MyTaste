package com.example.mytaste.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.mytaste.R
import com.example.mytaste.async.RetrieveRestaurantsAsyncTask
import com.example.mytaste.interfaces.RestaurantChangeListener
import com.example.mytaste.interfaces.RestaurantListener
import com.example.mytaste.pojo.RestaurantsList

class RestaurantFragmentRecyclerView : RestaurantChangeListener, Fragment{

    private var ARG_COLUMN_COUNT = "column-count"
    private var mColumnCount = 1

    private lateinit var recylerView : RecyclerView
    private lateinit var retrieveRestaurantsAsyncTask: RetrieveRestaurantsAsyncTask
    private lateinit var listener : RestaurantListener
    var latitude : String
    var longitude : String

    constructor(){
        this.latitude = ""
        this.longitude = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(arguments != null){
            mColumnCount = arguments?.getInt(ARG_COLUMN_COUNT)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view : View = inflater.inflate(R.layout.fragment_details_list_recycler, container, false)

        if(view is RecyclerView){
            recylerView = view
            //recylerView.adapter = DetailsRecyclerViewAdapter(listener)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        if(RestaurantsList.restaurants == null){
            Log.d(this.javaClass.name, "Request Restaurants API")

            retrieveRestaurantsAsyncTask = RetrieveRestaurantsAsyncTask(this)
            retrieveRestaurantsAsyncTask.execute()
        }
    }

    override fun onRestaurantRetrieved() {
        if(RestaurantsList.restaurants != null){
            recylerView.adapter = DetailsRecyclerViewAdapter(listener)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is RestaurantListener){
            listener = context
        }
    }
}