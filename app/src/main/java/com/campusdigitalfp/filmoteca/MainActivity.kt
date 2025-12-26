package com.campusdigitalfp.filmoteca

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.campusdigitalfp.filmoteca.ui.theme.FilmotecaTheme
import androidx.core.net.toUri
import androidx.navigation.NavType
import androidx.compose.ui.res.colorResource
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType


object Routes {
    const val FILM_LIST = "film_list"
    const val FILM_DATA = "film_data/{filmId}"
    const val FILM_EDIT = "film_edit"
    const val ABOUT = "about"

    fun filmData(filmId: Int) = "film_data/$filmId"
}


const val EDIT_RESULT = "edit_result"

data class Film(
    var id: Int = 0,
    var imageResId: Int = 0,
    var title: String? = null,
    var director: String? = null,
    var year: Int = 0,
    var genre: Int = 0,
    var format: Int = 0,
    var imdbUrl: String? = null,
    var comments: String? = null
) {
    override fun toString(): String = title ?: "<Sin título>"

    companion object {
        const val FORMAT_DVD = 0
        const val FORMAT_BLURAY = 1
        const val FORMAT_DIGITAL = 2

        const val GENRE_ACTION = 0
        const val GENRE_COMEDY = 1
        const val GENRE_DRAMA = 2
        const val GENRE_SCIFI = 3
        const val GENRE_HORROR = 4
    }
}

data class Movie(
    val name: String,
    val imageRes: Int,
    val director: String,
    val year: String,
    val genre: String,
    val format: String,
    val imdbUrl: String,
    val notes: String
)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FilmotecaTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Routes.FILM_LIST
                ) {
                    composable(Routes.FILM_LIST) {
                        FilmListScreen(navController)
                    }

                    composable(
                        route = Routes.FILM_DATA,
                        arguments = listOf(
                            navArgument("filmId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val filmId = backStackEntry.arguments?.getInt("filmId") ?: 0
                        FilmDataScreen(navController, filmId)
                    }


                    composable(Routes.FILM_EDIT) {
                        FilmEditScreen(navController)
                    }

                    composable(Routes.ABOUT) {
                        AboutScreen(navController)
                    }
                }
            }
        }
    }
}


@Composable
fun AboutScreen(navController: NavController) {
    val context = LocalContext.current
    val subject = stringResource(R.string.get_support)

    AppScaffold(showBackButton = true, navController = navController) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.created_by))
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.perfil),
                contentDescription = stringResource(R.string.created_by),
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { openWebSite(context,"https://www.google.es") }) {
                    Text(stringResource(R.string.go_to_website))
                }
                Button(onClick = { sendEmail(context, "eagullof@campusdigitalfp.es", subject) }) {
                    Text(stringResource(R.string.get_support))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.back))
            }
        }
    }
}

//Pantalla principal
@Composable
fun FilmListScreen(navController: NavController) {

    AppScaffold(showBackButton = false, navController = navController) { padding ->

        val films = FilmDataSource.films

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(films.size) { index ->
                val film = films[index]

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("film_data/${film.id}")
                        }
                ) {
                    Image(
                        painter = painterResource(film.imageResId),
                        contentDescription = film.title,
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(film.title ?: "")
                        Text("Director: ${film.director}")
                        Text("Año: ${film.year}")
                    }
                }
            }
        }
    }
}

@Composable
fun FilmDataScreen(navController: NavController, filmId: Int) {

    val context = LocalContext.current

    val film = FilmDataSource.films.getOrNull(filmId) ?: return

    AppScaffold(showBackButton = true, navController = navController) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Row {

                Image(
                    painter = painterResource(film.imageResId),
                    contentDescription = film.title,
                    modifier = Modifier
                        .height(200.dp)
                        .width(120.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {

                    Text(
                        text = film.title ?: "",
                        color = colorResource(R.color.teal_700),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Director:", fontWeight = FontWeight.Bold)
                    Text(film.director ?: "")

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Año:", fontWeight = FontWeight.Bold)
                    Text(film.year.toString())

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Género:", fontWeight = FontWeight.Bold)
                    Text(film.genre.toString())
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { openWebSite(context, film.imdbUrl ?: "") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver en IMDB")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Versión extendida")

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Button(onClick = {
                    navController.navigate(Routes.filmData(film.id))
                }) {
                    Text(stringResource(R.string.edit_movie))
                }

                Button(onClick = {
                    navController.popBackStack()
                }) {
                    Text(stringResource(R.string.back_to_main))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilmEditScreen(navController: NavController) {

    val context = LocalContext.current

    val generoList = listOf("Acción", "Drama", "Comedia", "Terror", "Sci-Fi")
    val formatoList = listOf("DVD", "Blu-ray", "Online")

    // Estados
    val tituloState = remember { mutableStateOf("") }
    val directorState = remember { mutableStateOf("") }
    val anyoState = remember { mutableStateOf("1997") }
    val urlState = remember { mutableStateOf("") }
    val comentariosState = remember { mutableStateOf("") }

    val expandedGenero = remember { mutableStateOf(false) }
    val expandedFormato = remember { mutableStateOf(false) }

    val generoState = remember { mutableStateOf(generoList.first()) }
    val formatoState = remember { mutableStateOf(formatoList.first()) }

    AppScaffold(showBackButton = true, navController = navController) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // FILA IMAGEN + BOTONES
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.dark_knight),
                    contentDescription = "Cartel",
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { /* TODO tomar foto */ }) {
                        Text("Tomar una fotografía")
                    }
                    Button(onClick = { /* TODO seleccionar imagen */ }) {
                        Text("Seleccionar una imagen")
                    }
                }
            }

            // CAMPOS DE TEXTO
            TextField(
                value = tituloState.value,
                onValueChange = { tituloState.value = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = directorState.value,
                onValueChange = { directorState.value = it },
                label = { Text("Director") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = anyoState.value,
                onValueChange = { anyoState.value = it },
                label = { Text("Año de estreno") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // DROPDOWN GÉNERO
            Column {
                Button(onClick = { expandedGenero.value = !expandedGenero.value }) {
                    Text("Género: ${generoState.value}")
                }
                DropdownMenu(
                    expanded = expandedGenero.value,
                    onDismissRequest = { expandedGenero.value = false }
                ) {
                    generoList.forEach { g ->
                        DropdownMenuItem(
                            text = { Text(g) },
                            onClick = {
                                generoState.value = g
                                expandedGenero.value = false
                            }
                        )
                    }
                }
            }

            // DROPDOWN FORMATO
            Column {
                Button(onClick = { expandedFormato.value = !expandedFormato.value }) {
                    Text("Formato: ${formatoState.value}")
                }
                DropdownMenu(
                    expanded = expandedFormato.value,
                    onDismissRequest = { expandedFormato.value = false }
                ) {
                    formatoList.forEach { f ->
                        DropdownMenuItem(
                            text = { Text(f) },
                            onClick = {
                                formatoState.value = f
                                expandedFormato.value = false
                            }
                        )
                    }
                }
            }

            TextField(
                value = urlState.value,
                onValueChange = { urlState.value = it },
                label = { Text("Enlace IMDB") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = comentariosState.value,
                onValueChange = { comentariosState.value = it },
                label = { Text("Comentarios") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
        }
    }
}




fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

//Función para abrir página web
fun openWebSite(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = url.toUri()
    }
    context.startActivity(intent)
}

//Función enviar correo electrónico
fun sendEmail(context: Context, email: String, asunto: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:$email".toUri()
        putExtra(Intent.EXTRA_SUBJECT, asunto)
    }
    context.startActivity(intent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    showBackButton: Boolean,
    navController: NavController,
    content: @Composable (paddingValues: PaddingValues) -> Unit
) {
    val navigationIconContent: (@Composable (() -> Unit))? = if (showBackButton) {
        {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.back)
                )
            }
        }
    } else null

    Scaffold(
        topBar = {
            if (navigationIconContent != null) {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    navigationIcon = navigationIconContent,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorResource(R.color.teal_700)
                    )
                )
            } else {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorResource(R.color.teal_700)
                    )
                )
            }
        }
    ) { padding ->
        content(padding)
    }
}
