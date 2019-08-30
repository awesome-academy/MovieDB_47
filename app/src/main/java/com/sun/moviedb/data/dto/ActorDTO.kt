package com.sun.moviedb.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Created by nguyenxuanhoi on 2019-08-29.
 * @author nguyen.xuan.hoi@sun-asterisk.com
 */
data class ActorDTO(
    @SerializedName("biography") val biography: String,
    @SerializedName("birthday") val birthday: String,
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("place_of_birth") val placeOfBirth: String,
    @SerializedName("popularity") val popularity: Float,
    @SerializedName("profile_path") val profilePath: String
)
