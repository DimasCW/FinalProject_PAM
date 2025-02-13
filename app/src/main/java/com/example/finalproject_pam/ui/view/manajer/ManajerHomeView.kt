package com.example.finalproject_pam.ui.view.manajer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalproject_pam.R
import com.example.finalproject_pam.model.Manajer
import com.example.finalproject_pam.model.Pemilik
import com.example.finalproject_pam.ui.PenyediaViewModel
import com.example.finalproject_pam.ui.costumwidget.CostumeTopAppBar
import com.example.finalproject_pam.ui.navigation.DestinasiNavigasi
import com.example.finalproject_pam.ui.viewmodel.manajer.ManajerHomeUiState
import com.example.finalproject_pam.ui.viewmodel.manajer.ManajerHomeVM
import com.example.finalproject_pam.ui.viewmodel.pemilik.PemilikHomeUiState
import com.example.finalproject_pam.ui.viewmodel.pemilik.PemilikHomeVM

object DestinasiHomeManajer : DestinasiNavigasi {
    override val route = "home_manajer"
    override val titleRes = "Manajer"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManajerHomeView(
    navigateBack: () -> Unit,
    navigateToItemEntry: () -> Unit,
    modifier: Modifier = Modifier,
    onDetailClick: (String) -> Unit = {},
    viewModel: ManajerHomeVM = viewModel(factory = PenyediaViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CostumeTopAppBar(
                title = DestinasiHomeManajer.titleRes,
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                onRefresh = {
                    viewModel.getMnjr()
                },
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToItemEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(16.dp),
                containerColor = Color(0xFF6200EE) // Warna aksen
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Manajer",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { innerPadding ->
        ManajerHomeStatus(
            manajerhomeUiState = viewModel.mnjrUIState,
            retryAction = { viewModel.getMnjr() },
            modifier = Modifier.padding(innerPadding),
            onDetailClick = onDetailClick,
            onDeleteClick = { manajer ->
                viewModel.deleteManajer(manajer.id_manajer)
                viewModel.getMnjr()
            }
        )
    }
}

@Composable
fun ManajerHomeStatus(
    manajerhomeUiState: ManajerHomeUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    onDeleteClick: (Manajer) -> Unit = {},
    onDetailClick: (String) -> Unit
) {
    when (manajerhomeUiState) {
        is ManajerHomeUiState.Loading -> OnLoading(modifier = modifier.fillMaxSize())
        is ManajerHomeUiState.Success -> {
            if (manajerhomeUiState.manajer.isEmpty()) {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Tidak ada data manajer",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                MnjrLayout(
                    manajer = manajerhomeUiState.manajer,
                    modifier = modifier.fillMaxWidth(),
                    onDetailClick = { onDetailClick(it.id_manajer) },
                    onDeleteClick = { onDeleteClick(it) }
                )
            }
        }
        is ManajerHomeUiState.Error -> OnError(retryAction, modifier = modifier.fillMaxSize())
    }
}

@Composable
fun OnLoading(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun OnError(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.loading_failed),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = retryAction, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
            Text(text = stringResource(R.string.retry), color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
fun MnjrLayout(
    manajer: List<Manajer>,
    modifier: Modifier = Modifier,
    onDetailClick: (Manajer) -> Unit,
    onDeleteClick: (Manajer) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(manajer) { manajer ->
            MnjrCard(
                manajer = manajer,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDetailClick(manajer) },
                onDeleteClick = { onDeleteClick(manajer) }
            )
        }
    }
}

@Composable
fun MnjrCard(
    manajer: Manajer,
    modifier: Modifier = Modifier,
    onDeleteClick: (Manajer) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(4.dp, shape = RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bagian kiri: Logo atau gambar
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFF6200EE), shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Logo",
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Bagian kanan: Informasi manajer
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = manajer.nama_manajer,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Kontak: ${manajer.kontak_manajer}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Tombol delete
            IconButton(onClick = { onDeleteClick(manajer) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}