package com.tugas.herodex.data.api

import com.tugas.herodex.data.model.Hero
import retrofit2.http.GET

interface ApiService {
    @GET("marvel_heroes.json")
    suspend fun getHeroes(): List<Hero>
}