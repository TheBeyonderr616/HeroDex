package com.tugas.herodex.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tugas.herodex.R
import com.tugas.herodex.data.model.Hero
import com.tugas.herodex.ui.viewmodel.HeroViewModel

@Composable
fun HeroDetailScreen(
    hero: Hero,
    viewModel: HeroViewModel,
    onBackClick: () -> Unit,
    onRecruitClick: () -> Unit
) {
    val context = LocalContext.current
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
                placeholder = painterResource(id = R.drawable.ironman),
                error = painterResource(id = R.drawable.ironman),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                hero.kategori?.let {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (it == "Hero") Color(0xFF4CAF50) else Color(0xFFF44336)
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = hero.afiliasi ?: "Unknown Affiliation",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 2.dp,
                color = Color.Red
            )

            DetailSection(title = "POWERS", content = hero.kekuatan)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            DetailSection(title = "BIOGRAPHY", content = hero.deskripsi)

            Spacer(modifier = Modifier.height(16.dp))

            DetailSection(title = "FIRST APPEARANCE", content = hero.kemunculanPertama ?: "Unknown")

            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    if (viewModel.isInSquad(hero)) {
                        Toast.makeText(context, "⚠️ ${hero.nama} sudah ada di Skuad!", Toast.LENGTH_SHORT).show()
                    } else {
                        onRecruitClick()
                        Toast.makeText(context, "✅ ${hero.nama} berhasil direkrut ke Skuad!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("REKRUT KE SKUAD", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun DetailSection(title: String, content: String) {
    Column {
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 24.sp
        )
    }
}
