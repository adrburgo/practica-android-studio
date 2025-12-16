package com.campusdigitalfp.filmoteca

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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


object Routes {
    const val FILM_LIST = "film_list"
    const val FILM_DATA = "film_data/{movieName}"
    const val FILM_EDIT = "film_edit"
    const val ABOUT = "about"

    fun filmData(movieName: String) = "film_data/$movieName"
}

const val EDIT_RESULT = "edit_result"

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

val movies = listOf(

    Movie(
        name = "The Dark Knight",
        imageRes = R.drawable.dark_knight, // cartel en drawable
        director = "Christopher Nolan",
        year = "2008",
        genre = "Acción / Crimen",
        format = "Blu-ray",
        imdbUrl = "https://www.imdb.com/title/tt0468569/",
        notes = "Considerada una de las mejores películas de superhéroes de la historia."
    ),

    Movie(
        name = "Inception",
        imageRes = R.drawable.inception, // cartel en drawable
        director = "Christopher Nolan",
        year = "2010",
        genre = "Ciencia ficción",
        format = "Digital",
        imdbUrl = "https://www.imdb.com/title/tt1375666/",
        notes = "Película compleja sobre sueños dentro de sueños."
    )
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
                            navArgument("movieName") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val movieName = backStackEntry.arguments?.getString("movieName") ?: ""
                        FilmDataScreen(navController, movieName)
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(onClick = {
                navController.navigate(Routes.filmData(movies[0].name))
            }) {
                Text("Ver ${movies[0].name}")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = {
                navController.navigate(Routes.filmData(movies[1].name))
            }) {
                Text("Ver ${movies[1].name}")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = {
                navController.navigate(Routes.ABOUT)
            }) {
                Text(stringResource(R.string.about))
            }
        }
    }
}

@Composable
fun FilmDataScreen(navController: NavController, movieName: String) {

    val context = LocalContext.current

    // Película estática según el nombre recibido
    val movie = movies.find { it.name == movieName } ?: movies.first()

    AppScaffold(showBackButton = true, navController = navController) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Row {

                Image(
                    painter = painterResource(id = movie.imageRes),
                    contentDescription = movie.name,
                    modifier = Modifier.size(140.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {

                    Text(
                        text = movie.name,
                        color = colorResource(R.color.teal_700),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Director:", fontWeight = FontWeight.Bold)
                    Text(movie.director)

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Año:", fontWeight = FontWeight.Bold)
                    Text(movie.year)

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Género:", fontWeight = FontWeight.Bold)
                    Text(movie.genre)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { openWebSite(context, movie.imdbUrl) },
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
                    navController.navigate(Routes.FILM_EDIT)
                }) {
                    Text(stringResource(R.string.edit_movie))
                }

                Button(onClick = {
                    navController.navigate(Routes.FILM_LIST) {
                        popUpTo(Routes.FILM_LIST) { inclusive = true }
                    }
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(EDIT_RESULT, false)
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.teal_200)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                // Guardar cambios = RESULT_OK
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(EDIT_RESULT, true)
                navController.popBackStack()
            }) {
                Text(stringResource(R.string.save))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(EDIT_RESULT, false)
                navController.popBackStack()
            }) {
                Text(stringResource(R.string.cancel))
            }
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
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
