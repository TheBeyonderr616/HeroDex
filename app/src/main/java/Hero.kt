package com.tugas.herodex.model

import com.google.gson.annotations.SerializedName

data class Hero(
    @SerializedName("nama") val nama: String,
    @SerializedName("deskripsi") val deskripsi: String,
    @SerializedName("kekuatan") val kekuatan: String,
    @SerializedName("image_name") val imageName: String
)