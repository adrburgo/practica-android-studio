package com.campusdigitalfp.filmoteca


object FilmDataSource {

    val films: MutableList<Film> = mutableListOf()

    init {
        val f1 = Film()
        f1.id = films.size
        f1.title = "The Dark Knight"
        f1.director = "Christopher Nolan"
        f1.imageResId = R.drawable.dark_knight
        f1.comments = "Considerada una de las mejores películas de superhéroes de la historia."
        f1.format = Film.FORMAT_BLURAY
        f1.genre = Film.GENRE_ACTION
        f1.imdbUrl = "https://www.imdb.com/title/tt0468569/"
        f1.year = 2008
        films.add(f1)

        val f2 = Film()
        f2.id = films.size
        f2.title = "Inception"
        f2.director = "Christopher Nolan"
        f2.imageResId = R.drawable.inception
        f2.comments = "Película compleja sobre sueños dentro de sueños."
        f2.format = Film.FORMAT_DIGITAL
        f2.genre = Film.GENRE_SCIFI
        f2.imdbUrl = "https://www.imdb.com/title/tt1375666/"
        f2.year = 2010
        films.add(f2)
    }
}
