package com.tugas.herodex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tugas.herodex.data.model.Hero
import com.tugas.herodex.data.repository.HeroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HeroViewModel : ViewModel() {
    private val repository = HeroRepository()

    private val _heroes = MutableStateFlow<List<Hero>>(emptyList())
    val heroes: StateFlow<List<Hero>> = _heroes.asStateFlow()

    private val _squad = MutableStateFlow<List<Hero>>(emptyList())
    val squad: StateFlow<List<Hero>> = _squad.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError.asStateFlow()

    init {
        fetchHeroes()
    }

    fun fetchHeroes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getHeroes()
                _heroes.value = result
                _isError.value = false
            } catch (e: Exception) {
                _isError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToSquad(hero: Hero) {
        if (!_squad.value.any { it.nama == hero.nama }) {
            _squad.value = _squad.value + hero
        }
    }

    fun removeFromSquad(hero: Hero) {
        _squad.value = _squad.value.filterNot { it.nama == hero.nama }
    }

    fun toggleFavorite(hero: Hero) {
        _favorites.value = if (_favorites.value.contains(hero.nama)) {
            _favorites.value - hero.nama
        } else {
            _favorites.value + hero.nama
        }
    }

    fun isInSquad(hero: Hero): Boolean {
        return _squad.value.any { it.nama == hero.nama }
    }
}
