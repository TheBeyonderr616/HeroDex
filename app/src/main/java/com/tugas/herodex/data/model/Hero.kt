package com.tugas.herodex.data.model

import com.google.gson.annotations.SerializedName

data class Hero(
    @SerializedName("nama") val nama: String,
    @SerializedName("deskripsi") val deskripsi: String,
    @SerializedName("kekuatan") val kekuatan: String,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("kategori") val kategori: String? = null,
    @SerializedName("afiliasi") val afiliasi: String? = "Unknown",
    @SerializedName("kemunculan_pertama") val kemunculanPertama: String? = "Unknown"
)
