package com.campusdigitalfp.filmoteca

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.ui.PlayerView
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.viewinterop.AndroidView

object Routes {

    const val LOGIN = "login"
    const val FILM_LIST = "film_list"
    const val FILM_DATA = "film_data/{filmId}"
    const val FILM_EDIT = "film_edit/{filmId}"
    const val ABOUT = "about"

    //fun filmData(filmId: Int) = "film_data/$filmId"
    fun filmEdit(filmId: String) = "film_edit/$filmId"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            //val startRoute = if (auth.currentUser != null) Routes.FILM_LIST else Routes.LOGIN
            val startRoute = Routes.LOGIN

            FilmotecaTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = startRoute
                ) {
                    composable(Routes.LOGIN) {
                        LoginScreen(navController)
                    }

                    composable(Routes.FILM_LIST) {
                        FilmListScreen(navController)
                    }

                    composable(
                        route = Routes.FILM_DATA,
                        arguments = listOf(navArgument("filmId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val filmId = backStackEntry.arguments?.getString("filmId") ?: return@composable
                        FilmDataScreen(navController, filmId)
                    }

                    composable(
                        route = Routes.FILM_EDIT,
                        arguments = listOf(navArgument("filmId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val filmId = backStackEntry.arguments?.getString("filmId") ?: return@composable
                        FilmEditScreen(navController, filmId)
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
fun LoginScreen(navController: NavController) {
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) } // Para alternar entre login y registro
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = if (isRegistering) "Registro de Usuario" else "Iniciar Sesión", fontSize = 24.sp)

        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (isRegistering) {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navController.navigate(Routes.FILM_LIST) { popUpTo(Routes.LOGIN) { inclusive = true } }
                        } else {
                            Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navController.navigate(Routes.FILM_LIST) { popUpTo(Routes.LOGIN) { inclusive = true } }
                        } else {
                            Toast.makeText(context, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }) {
            Text(if (isRegistering) "Registrarse" else "Entrar")
        }

        TextButton(onClick = { isRegistering = !isRegistering }) {
            Text(if (isRegistering) "¿Ya tienes cuenta? Inicia sesión" else "¿No tienes cuenta? Regístrate")
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
            val exoPlayer = remember {
                ExoPlayer.Builder(context).build().apply {
                    setMediaItem(MediaItem.fromUri("android.resource://${context.packageName}/${R.raw.howto}"))
                    prepare()
                }
            }
            DisposableEffect(Unit) { onDispose { exoPlayer.release() } }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Cómo usar la app", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            AndroidView(
                factory = { PlayerView(it).apply { player = exoPlayer } },
                modifier = Modifier.fillMaxWidth().height(220.dp)
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FilmListScreen(navController: NavController, viewModel: FilmViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    val context = LocalContext.current
    val films by viewModel.films.collectAsState()

    val multiSelectMode = remember { mutableStateOf(false) }
    val selectedFilmIds = remember { mutableStateListOf<String>() }
    val menuExpanded = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.teal_700)
                ),
                actions = {
                    if (multiSelectMode.value) {
                        IconButton(onClick = {
                            selectedFilmIds.forEach { id -> viewModel.deleteFilm(id) }
                            selectedFilmIds.clear()
                            multiSelectMode.value = false
                            Toast.makeText(context, "Películas borradas", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(painter = painterResource(R.drawable.ic_delete), contentDescription = "Borrar")
                        }
                    }
                    Box {
                        IconButton(onClick = { menuExpanded.value = true }) {
                            Icon(painter = painterResource(R.drawable.ic_more_vert), contentDescription = "Opciones")
                        }
                        DropdownMenu(
                            expanded = menuExpanded.value,
                            onDismissRequest = { menuExpanded.value = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Añadir película") },
                                onClick = {
                                    viewModel.addFilm(Film(
                                        title = "Nueva Película",
                                        director = "Desconocido",
                                        year = 2025,
                                        genre = Film.GENRE_ACTION,
                                        format = Film.FORMAT_DVD,
                                        imagen = "dark_knight"
                                    ))
                                    menuExpanded.value = false
                                    Toast.makeText(context, "Película añadida", Toast.LENGTH_SHORT).show()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Acerca de") },
                                onClick = {
                                    navController.navigate(Routes.ABOUT)
                                    menuExpanded.value = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(films.size) { index ->
                val film = films[index]

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (selectedFilmIds.contains(film.id)) colorResource(R.color.teal_200)
                            else Color.Transparent
                        )
                        .combinedClickable(
                            onClick = {
                                if (multiSelectMode.value) {
                                    if (selectedFilmIds.contains(film.id)) selectedFilmIds.remove(film.id)
                                    else selectedFilmIds.add(film.id)
                                } else {
                                    navController.navigate("film_data/${film.id}")
                                }
                            },
                            onLongClick = {
                                multiSelectMode.value = true
                                if (!selectedFilmIds.contains(film.id)) selectedFilmIds.add(film.id)
                            }
                        )
                        .padding(8.dp)
                ) {
                    if (film.imagen.startsWith("/")) {
                        AsyncImage(
                            model = File(film.imagen),
                            contentDescription = film.title,
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Crop,
                            error = painterResource(R.drawable.dark_knight)
                        )
                    } else {
                        val imageRes = context.resources.getIdentifier(film.imagen, "drawable", context.packageName)
                            .takeIf { it != 0 } ?: R.drawable.dark_knight
                        Image(
                            painter = painterResource(imageRes),
                            contentDescription = film.title,
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(film.title, fontWeight = FontWeight.Bold)
                        Text("Director: ${film.director}")
                        Text("Año: ${film.year}")
                    }
                }
            }
        }
    }
}
@Composable
fun FilmDataScreen(
    navController: NavController,
    filmId: String,
    viewModel: FilmViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val films by viewModel.films.collectAsState()
    val film = films.find { it.id == filmId } ?: return

    AppScaffold(showBackButton = true, navController = navController) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            Row {
                if (film.imagen.startsWith("/")) {
                    AsyncImage(
                        model = File(film.imagen),
                        contentDescription = film.title,
                        modifier = Modifier.height(200.dp).width(120.dp),
                        contentScale = ContentScale.Crop,
                        error = painterResource(R.drawable.dark_knight)
                    )
                } else {
                    val imageRes = context.resources.getIdentifier(film.imagen, "drawable", context.packageName)
                        .takeIf { it != 0 } ?: R.drawable.dark_knight
                    Image(
                        painter = painterResource(imageRes),
                        contentDescription = film.title,
                        modifier = Modifier.height(200.dp).width(120.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(film.title, color = colorResource(R.color.teal_700), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Director:", fontWeight = FontWeight.Bold)
                    Text(film.director)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Año:", fontWeight = FontWeight.Bold)
                    Text(film.year.toString())
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Género:", fontWeight = FontWeight.Bold)
                    Text(film.genre.toString())
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { openWebSite(context, film.imdbUrl) }, modifier = Modifier.fillMaxWidth()) {
                Text("Ver en IMDB")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = { navController.navigate(Routes.filmEdit(film.id)) }) {
                    Text(stringResource(R.string.edit_movie))
                }
                Button(onClick = { navController.popBackStack() }) {
                    Text(stringResource(R.string.back_to_main))
                }
            }
        }
    }
}

@SuppressLint("LocalContextResourcesRead")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilmEditScreen(
    navController: NavController,
    filmId: String,
    viewModel: FilmViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val films by viewModel.films.collectAsState()
    val film = films.find { it.id == filmId } ?: return

    val generoList = context.resources.getStringArray(R.array.genero_list)
    val formatoList = listOf("DVD", "Blu-ray", "Online")

    val tituloState = remember { mutableStateOf(film.title) }
    val directorState = remember { mutableStateOf(film.director) }
    val anyoState = remember { mutableStateOf(film.year.toString()) }
    val urlState = remember { mutableStateOf(film.imdbUrl) }
    val comentariosState = remember { mutableStateOf(film.comments) }
    val imagenState = remember { mutableStateOf(film.imagen) }

    val expandedGenero = remember { mutableStateOf(false) }
    val expandedFormato = remember { mutableStateOf(false) }
    val generoState = remember { mutableStateOf(generoList.getOrElse(film.genre) { generoList.first() }) }
    val formatoState = remember { mutableStateOf(formatoList.getOrElse(film.format) { formatoList.first() }) }

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasCameraPermission = granted
    }

    var realImagePath by remember { mutableStateOf<String?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && realImagePath != null) {
            imagenState.value = realImagePath!!
            android.util.Log.d("CAMARA", "Foto guardada en: ${realImagePath}")
        }
    }

    AppScaffold(showBackButton = true, navController = navController) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

                    // Muestra la foto nueva (ruta absoluta) o el drawable por defecto
                    if (imagenState.value.startsWith("/")) {
                        AsyncImage(
                            model = File(imagenState.value),
                            contentDescription = "Cartel",
                            modifier = Modifier.size(100.dp),
                            contentScale = ContentScale.Crop,
                            error = painterResource(R.drawable.dark_knight)
                        )
                    } else {
                        val imageRes = context.resources.getIdentifier(
                            imagenState.value, "drawable", context.packageName
                        ).takeIf { it != 0 } ?: R.drawable.dark_knight
                        Image(
                            painter = painterResource(imageRes),
                            contentDescription = "Cartel",
                            modifier = Modifier.size(100.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(onClick = {
                        if (!hasCameraPermission) {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        } else {
                            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                            val file = File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir)
                            // ← guardamos la ruta REAL del archivo antes de lanzar la cámara
                            realImagePath = file.absolutePath
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                            tempImageUri = uri
                            cameraLauncher.launch(uri)
                        }
                    }) {
                        Text("Tomar fotografía")
                    }
                }
            }

            item { TextField(value = tituloState.value, onValueChange = { tituloState.value = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth()) }
            item { TextField(value = directorState.value, onValueChange = { directorState.value = it }, label = { Text("Director") }, modifier = Modifier.fillMaxWidth()) }
            item {
                TextField(
                    value = anyoState.value, onValueChange = { anyoState.value = it },
                    label = { Text("Año de estreno") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Column {
                    Button(onClick = { expandedGenero.value = !expandedGenero.value }) { Text("Género: ${generoState.value}") }
                    DropdownMenu(expanded = expandedGenero.value, onDismissRequest = { expandedGenero.value = false }) {
                        generoList.forEach { g -> DropdownMenuItem(text = { Text(g) }, onClick = { generoState.value = g; expandedGenero.value = false }) }
                    }
                }
            }
            item {
                Column {
                    Button(onClick = { expandedFormato.value = !expandedFormato.value }) { Text("Formato: ${formatoState.value}") }
                    DropdownMenu(expanded = expandedFormato.value, onDismissRequest = { expandedFormato.value = false }) {
                        formatoList.forEach { f -> DropdownMenuItem(text = { Text(f) }, onClick = { formatoState.value = f; expandedFormato.value = false }) }
                    }
                }
            }
            item { TextField(value = urlState.value, onValueChange = { urlState.value = it }, label = { Text("Enlace IMDB") }, modifier = Modifier.fillMaxWidth()) }
            item {
                TextField(
                    value = comentariosState.value, onValueChange = { comentariosState.value = it },
                    label = { Text("Comentarios") },
                    modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 5
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(
                        onClick = {
                            // Guarda la película con la ruta de la imagen (absoluta o nombre drawable)
                            viewModel.updateFilm(film.copy(
                                title = tituloState.value,
                                director = directorState.value,
                                year = anyoState.value.toIntOrNull() ?: film.year,
                                genre = generoList.indexOf(generoState.value).coerceAtLeast(0),
                                format = formatoList.indexOf(formatoState.value).coerceAtLeast(0),
                                imdbUrl = urlState.value,
                                comments = comentariosState.value,
                                imagen = imagenState.value  // ruta absoluta o nombre drawable
                            ))
                            Toast.makeText(context, "Película actualizada", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Guardar") }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                }
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

            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable {
                        // Ir al listado principal
                        navController.navigate(Routes.FILM_LIST) {
                            popUpTo(Routes.FILM_LIST) { inclusive = true }
                        }
                    }
            ) {
                Image(
                    painter = painterResource(R.drawable.home_icon), // icono de la app
                    contentDescription = "Home",
                    modifier = Modifier.size(36.dp)
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
                    ),

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