package com.tugas.herodex.model
import androidx.annotation.DrawableRes

data class Hero(
    val nama: String,
    val deskripsi: String,
    val kekuatan: String,
    @DrawableRes val imageRes: Int
)