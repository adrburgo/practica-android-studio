package com.campusdigitalfp.filmoteca

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FilmRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("films")

    // Escucha cambios en tiempo real
    fun getFilms(): Flow<List<Film>> = callbackFlow {
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
                    year = (doc.getLong("year") ?: 0).toInt(),
                    genre = (doc.getLong("genre") ?: 0).toInt(),
                    format = (doc.getLong("format") ?: 0).toInt(),
                    imdbUrl = doc.getString("imdbUrl") ?: "",
                    comments = doc.getString("comments") ?: ""
                )
            } ?: emptyList()
            trySend(films)
        }
        awaitClose { listener.remove() }
    }

    suspend fun addFilm(film: Film): String {
        val data = filmToMap(film)
        val ref = collection.add(data).await()
        return ref.id
    }

    suspend fun updateFilm(film: Film) {
        collection.document(film.id.toString()).set(filmToMap(film)).await()
    }

    suspend fun deleteFilm(filmId: String) {
        collection.document(filmId).delete().await()
    }

    private fun filmToMap(film: Film) = mapOf(
        "title" to film.title,
        "director" to film.director,
        "year" to film.year,
        "genre" to film.genre,
        "format" to film.format,
        "imdbUrl" to film.imdbUrl,
        "comments" to film.comments
    )
}