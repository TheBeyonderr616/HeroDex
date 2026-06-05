package com.tugas.herodex

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.tugas.herodex.data.model.Hero
import com.tugas.herodex.data.repository.HeroRepository
import com.tugas.herodex.ui.theme.HeroDexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HeroDexTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSplashScreen by remember { mutableStateOf(true) }

                    if (showSplashScreen) {
                        AnimatedSplashScreen(onSplashFinished = { showSplashScreen = false })
                    } else {
                        HeroDexApp()
                    }
                }
            }
        }
    }
}

@Composable
fun HeroDexApp() {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    var listHero by remember { mutableStateOf<List<Hero>>(emptyList()) }
    var isAppLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val repository = remember { HeroRepository() }
    var searchQuery by remember { mutableStateOf("") }
    var heroForDialog by remember { mutableStateOf<Hero?>(null) }
    var heroForScreen by remember { mutableStateOf<Hero?>(null) }
    var currentTab by remember { mutableStateOf("Home") }
    var recruitedHeroes by remember { mutableStateOf(emptyList<Hero>()) }

    LaunchedEffect(Unit) {
        try {
            val response = repository.getHeroes()
            listHero = response
            isAppLoading = false
            isError = false
        } catch (e: Exception) {
            isAppLoading = false
            isError = true
        }
    }

    val filteredHeroes = listHero.filter { hero ->
        hero.nama.contains(searchQuery, ignoreCase = true)
    }

    val onRecruitHero: (Hero) -> Unit = { hero ->
        if (!recruitedHeroes.contains(hero)) {
            recruitedHeroes = recruitedHeroes + hero
            Toast.makeText(context, "${hero.nama} direkrut ke Skuad!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "${hero.nama} sudah ada di Skuad!", Toast.LENGTH_SHORT).show()
        }
    }

    if (heroForScreen == null) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = currentTab == "Home",
                        onClick = { currentTab = "Home" }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Star, contentDescription = "My Squad") },
                        label = { Text("My Squad") },
                        selected = currentTab == "Squad",
                        onClick = { currentTab = "Squad" }
                    )
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                if (currentTab == "Home") {
                    if (isAppLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else if (isError || listHero.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Gagal Memuat Data",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Pastikan koneksi internet Anda menyala",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                label = { Text("Cari Hero Marvel...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(50),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Red
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() })
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            if (filteredHeroes.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Hero atau Villain tidak ditemukan di Universe ini!",
                                        textAlign = TextAlign.Center,
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    item {
                                        Text(
                                            text = "AVENGERS INITIATIVE",
                                            color = MaterialTheme.colorScheme.primary,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 4.sp
                                        )
                                        Text(
                                            "Featured Heroes",
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(vertical = 12.dp)
                                        )

                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                            items(filteredHeroes) { hero ->
                                                HeroRowItem(hero = hero, onClick = { heroForDialog = hero })
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(24.dp))
                                        Text(
                                            "Full Database",
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }

                                    items(filteredHeroes) { hero ->
                                        HeroCardDetail(
                                            hero = hero,
                                            snackbarHostState = snackbarHostState,
                                            onImageClick = { heroForScreen = hero },
                                            onRecruitClick = { onRecruitHero(hero) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (recruitedHeroes.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Belum ada hero yang direkrut ke Skuad!",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Text(
                                    text = "MY SQUAD",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }
                            items(recruitedHeroes) { hero ->
                                HeroCardDetail(
                                    hero = hero,
                                    snackbarHostState = snackbarHostState,
                                    onImageClick = { heroForScreen = hero },
                                    onRecruitClick = { onRecruitHero(hero) }
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        HeroDetailScreen(
            hero = heroForScreen!!,
            onBackClick = { heroForScreen = null },
            onRecruitClick = { onRecruitHero(heroForScreen!!) }
        )
    }
    if (heroForDialog != null) {
        AlertDialog(
            onDismissRequest = { heroForDialog = null },
            confirmButton = {
                TextButton(onClick = { heroForDialog = null }) {
                    Text("Tutup", color = Color.Red)
                }
            },
            title = { Text(text = heroForDialog!!.nama, fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = heroForDialog!!.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp).padding(bottom = 8.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(text = heroForDialog!!.deskripsi)
                }
            },
            containerColor = Color(0xFF1A1A1A),
            titleContentColor = Color.White,
            textContentColor = Color.LightGray
        )
    }
}

@Composable
fun HeroRowItem(hero: Hero, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFE23636))
    ) {
        Column {
            AsyncImage(
                model = hero.imageUrl,
                contentDescription = hero.nama,
                placeholder = painterResource(id = R.drawable.ironman),
                error = painterResource(id = R.drawable.ironman),
                modifier = Modifier.fillMaxWidth().height(100.dp),
                contentScale = ContentScale.Crop
            )
            Text(text = hero.nama, color = Color.White, modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun HeroCardDetail(
    hero: Hero, 
    snackbarHostState: SnackbarHostState, 
    onImageClick: () -> Unit,
    onRecruitClick: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }
    var isRecruiting by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFE23636))
    ) {
        Column {
            Box {
                AsyncImage(
                    model = hero.imageUrl,
                    contentDescription = hero.nama,
                    placeholder = painterResource(id = R.drawable.ironman),
                    error = painterResource(id = R.drawable.ironman),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable { onImageClick() },
                    contentScale = ContentScale.Crop
                )

                IconButton(onClick = { isFavorite = !isFavorite }, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(hero.nama, color = Color.White, style = MaterialTheme.typography.titleMedium)
                Text(hero.deskripsi, color = Color.LightGray, modifier = Modifier.padding(vertical = 4.dp))

                Button(
                    onClick = {
                        onRecruitClick()
                        coroutineScope.launch {
                            isRecruiting = true
                            delay(2000)
                            isRecruiting = false
                            snackbarHostState.showSnackbar("Hero ${hero.nama} berhasil masuk tim!")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isRecruiting
                ) {
                    if (isRecruiting) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Memproses...")
                    } else {
                        Text("Rekrut Hero Sekarang")
                    }
                }
            }
        }
    }
}

@Composable
fun HeroDetailScreen(
    hero: Hero, 
    onBackClick: () -> Unit,
    onRecruitClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {
            AsyncImage(
                model = hero.imageUrl,
                contentDescription = hero.nama,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            IconButton(
                onClick = onBackClick,
                modifier = Modifier.padding(16.dp).align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
                )
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = hero.nama.uppercase(),
                color = Color.Red,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 2.dp,
                color = Color.Red
            )

            Text(
                text = "BIOGRAPHY",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = hero.deskripsi + "\n\n" +
                       "Hero ini merupakan salah satu pejuang terhebat di jagat raya Marvel. " +
                       "Dikenal karena dedikasi dan kekuatannya yang luar biasa dalam menjaga keadilan. " +
                       "Setiap tindakan yang diambil selalu berfokus pada keselamatan warga sipil dan perdamaian dunia.",
                color = Color.LightGray,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onRecruitClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Rekrut Ke Skuad")
            }
        }
    }
}

@Composable
fun AnimatedSplashScreen(onSplashFinished: () -> Unit) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500)
        )
        delay(500)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.hdx),
            contentDescription = "Logo Aplikasi",
            modifier = Modifier
                .scale(scale.value)
                .alpha(scale.value)
        )
    }
}
