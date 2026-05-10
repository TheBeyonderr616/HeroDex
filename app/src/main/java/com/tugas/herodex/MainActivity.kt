package com.tugas.herodex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.tugas.herodex.model.Hero
import com.tugas.herodex.network.RetrofitClient
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
                    HeroDexApp()
                }
            }
        }
    }
}

@Composable
fun HeroDexApp() {
    var listHero by remember { mutableStateOf<List<Hero>>(emptyList()) }
    var isAppLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.instance.getHeroes()
            listHero = response
            isAppLoading = false
            isError = false
        } catch (e: Exception) {
            isAppLoading = false
            isError = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("AVENGERS INITIATIVE", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge)
                    Text("Featured Heroes", color = Color.White, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 12.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(listHero) { hero ->
                            HeroRowItem(hero = hero)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Full Database", color = Color.White, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
                }

                items(listHero) { hero ->
                    HeroCardDetail(hero = hero, snackbarHostState = snackbarHostState)
                }
            }
        }

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun HeroRowItem(hero: Hero) {
    Card(
        modifier = Modifier.width(150.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
fun HeroCardDetail(hero: Hero, snackbarHostState: SnackbarHostState) {
    var isFavorite by remember { mutableStateOf(false) }
    var isRecruiting by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = hero.imageUrl,
                    contentDescription = hero.nama,
                    placeholder = painterResource(id = R.drawable.ironman),
                    error = painterResource(id = R.drawable.ironman),
                    modifier = Modifier.fillMaxWidth().height(200.dp),
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