package com.example.mytaste.pojo

import com.google.gson.annotations.SerializedName
import org.osmdroid.util.GeoPoint

class Restaurant(

    @SerializedName("name")
    var name: String,
    @SerializedName("loc")
    var localisation: GeoPoint,
    @SerializedName("description")
    var description: String,
    @SerializedName("address")
    var address: String,
    @SerializedName("phone_number")
    var phone: String,
    @SerializedName("email")
    var email : String,
    @SerializedName("website")
    var website : String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Restaurant

        if (name != other.name) return false
        if (localisation != other.localisation) return false
        if (description != other.description) return false
        if (address != other.address) return false
        if (phone != other.phone) return false
        if (email != other.email) return false
        if (website != other.website) return false

        return true
    }
}