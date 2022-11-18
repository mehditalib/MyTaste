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
import com.example.mytaste.utils.ListActivity


class RestaurantFragmentRecyclerView : RestaurantChangeListener, Fragment {

    // Recycler View setting
    private var ARG_COLUMN_COUNT = "column-count"
    private var mColumnCount = 1

    private lateinit var recylerView : RecyclerView
    private lateinit var retrieveRestaurantsAsyncTask: RetrieveRestaurantsAsyncTask
    private lateinit var listener : RestaurantListener

    private var safeInstance : ListActivity

    // Localisation to for the request
    var latitude: String = "50.636842412658126"
    var longitude: String = "3.0635913872047054"

    constructor(safeInstance : ListActivity) {
        this.safeInstance = safeInstance
    }

    // Set the localisation of the request
    fun setCoord(latitude: String, longitude: String){
        this.latitude = latitude
        this.longitude = longitude
    }

    // Apply to the api
    fun getRestaurants(){
        retrieveRestaurantsAsyncTask = RetrieveRestaurantsAsyncTask(this)
        retrieveRestaurantsAsyncTask.execute(latitude, longitude)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(arguments != null){
            mColumnCount = arguments?.getInt(ARG_COLUMN_COUNT)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.fragment_details_list_recycler, container, false)

        if(view is RecyclerView){
            recylerView = view
        }
        return view
    }


    override fun onStart() {
        super.onStart()
        if(RestaurantsList.restaurants == null){
            // Apply to the api
            retrieveRestaurantsAsyncTask = RetrieveRestaurantsAsyncTask(this)
            retrieveRestaurantsAsyncTask.execute(latitude, longitude)
        }
    }

    override fun onRestaurantRetrieved() {
        // We are going to display the recycler view so we stop to display the loading animation on the UI thread
        safeInstance.runOnUiThread(Runnable {
            safeInstance.findViewById<View>(R.id.laodingAnim).visibility = View.GONE
        })

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