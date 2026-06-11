package com.tugas.herodex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tugas.herodex.data.model.Hero
import com.tugas.herodex.ui.components.HeroCard
import com.tugas.herodex.ui.viewmodel.HeroViewModel

@Composable
fun SquadScreen(
    viewModel: HeroViewModel,
    onHeroClick: (Hero) -> Unit
) {
    val squad by viewModel.squad.collectAsState()

    val squadAffiliationLabel = when {
        squad.isEmpty() -> "No Squad Yet"
        else -> {
            val counts = squad.groupingBy { it.afiliasi ?: "None" }.eachCount()
            val total = squad.size
            val maxEntry = counts.maxByOrNull { it.value }
            
            if (counts.size == 1) {
                "${maxEntry?.key} Squad"
            } else if (maxEntry != null && maxEntry.value > total / 2) {
                "Mostly ${maxEntry.key}"
            } else {
                "Mixed Squad"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "MY SQUAD",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Squad Affiliation Badge
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF8B0000))
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = squadAffiliationLabel,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (squad.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada hero yang direkrut ke Skuad!",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(squad, key = { it.nama }) { hero ->
                    HeroCard(
                        hero = hero,
                        viewModel = viewModel,
                        onHeroClick = { onHeroClick(hero) },
                        isSquadMode = true
                    )
                }
            }
        }
    }
}
