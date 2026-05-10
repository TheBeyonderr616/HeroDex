package com.tugas.herodex.network

import com.tugas.herodex.model.Hero
import retrofit2.http.GET

interface ApiService {
    @GET("marvel_heroes.json")
    suspend fun getHeroes(): List<Hero>
}