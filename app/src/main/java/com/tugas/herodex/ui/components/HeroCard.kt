package com.tugas.herodex.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tugas.herodex.R
import com.tugas.herodex.data.model.Hero
import com.tugas.herodex.ui.viewmodel.HeroViewModel

@Composable
fun HeroCard(
    hero: Hero,
    viewModel: HeroViewModel,
    onHeroClick: () -> Unit,
    isSquadMode: Boolean = false
) {
    val favorites by viewModel.favorites.collectAsState()
    val isFavorite = favorites.contains(hero.nama)
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
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
                        .clickable { onHeroClick() },
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = { viewModel.toggleFavorite(hero) },
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.White
                    )
                }

                if (isSquadMode) {
                    IconButton(
                        onClick = { viewModel.removeFromSquad(hero) },
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove from Squad",
                            tint = Color.White
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = hero.nama,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    hero.kategori?.let {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (it == "Hero") Color(0xFF4CAF50) else Color(0xFFF44336)
                        ) {
                            Text(
                                text = it,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }
                
                Text(
                    text = "Power: ${hero.kekuatan}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                if (!isSquadMode) {
                    Button(
                        onClick = {
                            if (viewModel.isInSquad(hero)) {
                                Toast.makeText(context, "⚠️ ${hero.nama} sudah ada di Skuad!", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.addToSquad(hero)
                                Toast.makeText(context, "✅ ${hero.nama} berhasil direkrut ke Skuad!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Rekrut Ke Skuad")
                    }
                }
            }
        }
    }
}
