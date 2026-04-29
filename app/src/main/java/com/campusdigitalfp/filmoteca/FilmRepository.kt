package com.campusdigitalfp.filmoteca

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FilmRepository {

    private val db = FirebaseFirestore.getInstance()
    private fun getUserCollection(userId: String) =
        db.collection("users").document(userId).collection("films")

    // Obtener películas en tiempo real filtradas por usuario
    fun getFilms(userId: String): Flow<List<Film>> = callbackFlow {
        val collection = getUserCollection(userId)

        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val films = snapshot?.documents?.map { doc ->
                Film(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    director = doc.getString("director") ?: "",
                    // Usamos getLong para evitar errores de tipo y lo convertimos a Int
                    year = (doc.getLong("year") ?: 0).toInt(),
                    genre = (doc.getLong("genre") ?: 0).toInt(),
                    format = (doc.getLong("format") ?: 0).toInt(),
                    imdbUrl = doc.getString("imdbUrl") ?: "",
                    comments = doc.getString("comments") ?: ""
                )
            } ?: emptyList()

            trySend(films)
        }

        // Importante: se cierra el listener cuando se cancela el Flow
        awaitClose { listener.remove() }
    }

    // Añadir película a la colección del usuario
    suspend fun addFilm(userId: String, film: Film): String {
        val data = filmToMap(film)
        val ref = getUserCollection(userId).add(data).await()
        return ref.id
    }

    // Actualizar película en la colección del usuario
    suspend fun updateFilm(userId: String, film: Film) {
        // Usamos el id del documento para localizarlo dentro de la colección del usuario
        getUserCollection(userId).document(film.id).set(filmToMap(film)).await()
    }

    // Borrar película de la colección del usuario
    suspend fun deleteFilm(userId: String, filmId: String) {
        getUserCollection(userId).document(filmId).delete().await()
    }

    // Mapeo manual para asegurar que los tipos de datos en Firestore sean consistentes
    private fun filmToMap(film: Film) = mapOf(
        "title" to film.title,
        "director" to film.director,
        "year" to film.year, // Aquí se guarda como Number automáticamente
        "genre" to film.genre,
        "format" to film.format,
        "imdbUrl" to film.imdbUrl,
        "comments" to film.comments
    )
}