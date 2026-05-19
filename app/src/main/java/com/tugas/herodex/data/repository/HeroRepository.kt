package com.tugas.herodex.data.repository

import com.tugas.herodex.data.api.RetrofitClient
import com.tugas.herodex.data.model.Hero

class HeroRepository {
    suspend fun getHeroes(): List<Hero> {
        return try {
            RetrofitClient.instance.getHeroes()
        } catch (e: Exception) {
            emptyList()
        }
    }
}