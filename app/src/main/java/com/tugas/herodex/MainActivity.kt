package com.tugas.herodex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tugas.herodex.model.HeroSource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HeroScreen()
        }
    }
}

@Composable
fun HeroScreen() {

    val listHero = HeroSource.dummyHero

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        items(listHero) { hero ->
            Column(modifier = Modifier.padding(bottom = 24.dp)) {

                Image(
                    painter = painterResource(id = hero.imageRes),
                    contentDescription = hero.nama,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Nama: ${hero.nama}",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(text = "Deskripsi: ${hero.deskripsi}")

                Text(text = "Kekuatan: ${hero.kekuatan}")

                Text(text = "--------------------------------")
            }
        }
    }
}