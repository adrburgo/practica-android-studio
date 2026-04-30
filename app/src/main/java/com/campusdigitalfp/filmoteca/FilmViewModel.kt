package com.campusdigitalfp.filmoteca

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FilmViewModel : ViewModel() {
    // Repositorio encargado de Firestore
    private val repository = FilmRepository()

    // Obtenemos la instancia de Auth para saber quién es el usuario actual
    private val auth = FirebaseAuth.getInstance()

    private val _films = MutableStateFlow<List<Film>>(emptyList())
    val films: StateFlow<List<Film>> = _films

    init {
        loadUserFilms()
    }

    fun updateFilmImage(filmId: String, imagenUri: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            viewModelScope.launch {
                repository.updateFilmImage(userId, filmId, imagenUri)
            }
        } else {
            _films.value = emptyList()
        }
    }

    // Función para cargar películas según el usuario actual
    fun loadUserFilms() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            viewModelScope.launch {
                // Ahora le pasamos el userId al repositorio
                repository.getFilms(userId).collect { list ->
                    _films.value = list
                }
            }
        } else {
            _films.value = emptyList()
        }
    }

    fun addFilm(film: Film) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            viewModelScope.launch {
                repository.addFilm(userId, film)
            }
        }
    }

    fun updateFilm(film: Film) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            viewModelScope.launch {
                repository.updateFilm(userId, film)
            }
        }
    }

    fun deleteFilm(filmId: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            viewModelScope.launch {
                repository.deleteFilm(userId, filmId)
            }
        }
    }
}