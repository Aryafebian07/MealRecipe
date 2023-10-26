package com.niveon.mealrecipe.model

import com.google.gson.annotations.SerializedName

data class AreaList(
    @SerializedName("meals")
    val areas : List<Area>
) {
    data class Area(
        @SerializedName("strArea")
        val strArea: String?
    )
}