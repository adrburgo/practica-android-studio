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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import androidx.navigation.compose.rememberNavController
import com.campusdigitalfp.filmoteca.ui.theme.FilmotecaTheme
import androidx.core.net.toUri

object Routes {
    const val FILM_LIST = "film_list"
    const val FILM_DATA = "film_data"
    const val FILM_EDIT = "film_edit"
    const val ABOUT = "about"
}

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
                    composable(Routes.FILM_DATA) {
                        FilmDataScreen(navController)
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
    //val notImplementedText: String = stringResource(R.string.not_implemented)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = stringResource(R.string.created_by))

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.perfil),
            contentDescription = stringResource(R.string.created_by),
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(onClick = {
                openWebSite(context,"https://www.google.es")
            }) {
                Text(stringResource(R.string.go_to_website))
            }

            Button(onClick = {
                sendEmail(
                    context = context,
                    email = "eagullof@campusdigitalfp.es",
                    asunto = context.getString(R.string.incidence_subject)
                )
            }) {
                Text(stringResource(R.string.get_support))
            }


        }

        Button(onClick = {
            navController.popBackStack()
        }) {
            Text(stringResource(R.string.back))
        }
    }
}

//Pantalla principal
@Composable
fun FilmListScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(onClick = {
            navController.navigate(Routes.FILM_DATA)
        }) {
            Text(stringResource(R.string.view_movie_a))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            navController.navigate(Routes.FILM_DATA)
        }) {
            Text(stringResource(R.string.view_movie_b))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            navController.navigate(Routes.ABOUT)
        }) {
            Text(stringResource(R.string.about))
        }
    }
}

@Composable
fun FilmDataScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(stringResource(R.string.movie_data))

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate(Routes.FILM_DATA)
        }) {
            Text(stringResource(R.string.view_related_movie))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            navController.navigate(Routes.FILM_EDIT)
        }) {
            Text(stringResource(R.string.edit_movie))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            navController.navigate(Routes.FILM_LIST) {
                popUpTo(Routes.FILM_LIST) { inclusive = true }
            }
        }) {
            Text(stringResource(R.string.back_to_main))
        }
    }
}

@Composable
fun FilmEditScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(stringResource(R.string.editing_movie))

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.popBackStack()
        }) {
            Text(stringResource(R.string.save))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            navController.popBackStack()
        }) {
            Text(stringResource(R.string.cancel))
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