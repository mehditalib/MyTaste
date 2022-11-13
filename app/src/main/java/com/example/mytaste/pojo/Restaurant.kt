package com.example.mytaste.pojo

import org.osmdroid.util.GeoPoint

class Restaurant(
    var name: String,
    var localisation: GeoPoint,
    var description: String,
    var address: String,
    var phone: String,
    var email : String,
    var website : String
)