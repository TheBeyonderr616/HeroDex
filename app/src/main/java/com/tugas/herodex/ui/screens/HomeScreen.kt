package com.tugas.herodex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tugas.herodex.data.model.Hero
import com.tugas.herodex.ui.components.HeroCard
import com.tugas.herodex.ui.viewmodel.HeroViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HeroViewModel,
    onHeroClick: (Hero) -> Unit
) {
    val heroes by viewModel.heroes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isError by viewModel.isError.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var selectedKategori by remember { mutableStateOf("All") }
    var selectedAfiliasi by remember { mutableStateOf("All") }
    var selectedSort by remember { mutableStateOf("Default") }

    val keyboardController = LocalSoftwareKeyboardController.current

    val filteredHeroes = heroes
        .filter {
            val matchSearch = it.nama.contains(searchQuery, ignoreCase = true)
            val matchKategori = selectedKategori == "All" || it.kategori == selectedKategori
            val matchAfiliasi = selectedAfiliasi == "All" || it.afiliasi == selectedAfiliasi
            matchSearch && matchKategori && matchAfiliasi
        }
        .let { list ->
            when (selectedSort) {
                "A-Z" -> list.sortedBy { it.nama }
                "Z-A" -> list.sortedByDescending { it.nama }
                else -> list
            }
        }

    val isFilterActive = selectedKategori != "All" || selectedAfiliasi != "All" || selectedSort != "Default"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Filter & Urutkan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    HorizontalDivider()
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Kategori", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 8.dp))
                    listOf("All", "Hero", "Villain").forEach { kategori ->
                        NavigationDrawerItem(
                            label = { Text(kategori) },
                            selected = selectedKategori == kategori,
                            onClick = {
                                selectedKategori = kategori
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Afiliasi", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 8.dp))
                    listOf("All", "Avengers", "X-Men", "Defenders", "Asgard", "None").forEach { afiliasi ->
                        NavigationDrawerItem(
                            label = { Text(afiliasi) },
                            selected = selectedAfiliasi == afiliasi,
                            onClick = {
                                selectedAfiliasi = afiliasi
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Urutkan", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 8.dp))
                    listOf("Default", "A-Z", "Z-A").forEach { sort ->
                        NavigationDrawerItem(
                            label = { Text(sort) },
                            selected = selectedSort == sort,
                            onClick = {
                                selectedSort = sort
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BadgedBox(
                    badge = {
                        if (isFilterActive) {
                            Badge()
                        }
                    }
                ) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Buka Filter")
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Cari Hero Marvel...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Red
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() })
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Red)
                }
            } else if (isError) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Gagal Memuat Data", color = Color.Red, fontWeight = FontWeight.Bold)
                        Button(onClick = { viewModel.fetchHeroes() }, modifier = Modifier.padding(top = 8.dp)) {
                            Text("Coba Lagi")
                        }
                    }
                }
            } else {
                Text(
                    text = "AVENGERS INITIATIVE",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                if (filteredHeroes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Tidak ada hero yang cocok dengan filter", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredHeroes, key = { it.nama }) { hero ->
                            HeroCard(
                                hero = hero,
                                viewModel = viewModel,
                                onHeroClick = { onHeroClick(hero) }
                            )
                        }
                    }
                }
            }
        }
    }
}
