package com.campusdigitalfp.filmoteca

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FilmViewModel : ViewModel() {

    private val repository = FilmRepository()

    private val _films = MutableStateFlow<List<Film>>(emptyList())
    val films: StateFlow<List<Film>> = _films

    init {
        viewModelScope.launch {
            repository.getFilms().collect { list ->
                    _films.value = list
            }
        }
    }

    fun addFilm(film: Film) {
        viewModelScope.launch {
            repository.addFilm(film)
        }
    }

    fun updateFilm(film: Film) {
        viewModelScope.launch {
            repository.updateFilm(film)
        }
    }

    fun deleteFilm(filmId: String) {
        viewModelScope.launch {
            repository.deleteFilm(filmId)
        }
    }
}