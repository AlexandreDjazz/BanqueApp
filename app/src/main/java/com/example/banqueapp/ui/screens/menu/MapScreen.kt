package com.example.banqueapp.ui.screens.menu

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.banqueapp.data.model.ATM
import com.example.banqueapp.viewModels.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateBack: () -> Unit = {},
    showBackButton: Boolean = false,
    viewModel: MapViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        viewModel.initializeLocationClient(context)
    }

    LaunchedEffect(locationPermissionsState) {
        if (!locationPermissionsState.allPermissionsGranted) {
            locationPermissionsState.launchMultiplePermissionRequest()
        } else {
            viewModel.onPermissionResult(true)
            viewModel.getCurrentLocation()
        }
    }

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        viewModel.onPermissionResult(locationPermissionsState.allPermissionsGranted)
        if (locationPermissionsState.allPermissionsGranted) {
            viewModel.getCurrentLocation()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ATM à proximité") },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshATMs() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualiser")
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.hasLocationPermission) {
                FloatingActionButton(
                    onClick = { viewModel.getCurrentLocation() },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = "Ma position",
                        tint = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                !uiState.hasLocationPermission -> {
                    PermissionDeniedContent(
                        onRequestPermission = {
                            locationPermissionsState.launchMultiplePermissionRequest()
                        }
                    )
                }
                uiState.isLoading -> {
                    LoadingContent()
                }
                uiState.errorMessage != null -> {
                    ErrorContent(
                        message = uiState.errorMessage ?: "Erreur inconnue",
                        onRetry = { viewModel.getCurrentLocation() }
                    )
                }
                uiState.currentLocation != null -> {
                    MapContent(
                        currentLocation = uiState.currentLocation!!,
                        atmList = uiState.atmList
                    )
                }
            }
        }
    }
}

@Composable
private fun MapContent(
    currentLocation: LatLng,
    atmList: List<ATM>
) {
    var selectedAtm by remember { mutableStateOf<ATM?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 14f)
    }

    LaunchedEffect(currentLocation) {
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(currentLocation, 14f),
            durationMs = 1000
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = false
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = false
                )
            ) {

                Marker(
                    state = MarkerState(position = currentLocation),
                    title = "Vous êtes ici",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )

                atmList.forEach { atm ->
                    Marker(
                        state = MarkerState(position = atm.position),
                        title = atm.name,
                        snippet = "${String.format("%.2f", atm.distance)} km",
                        icon = BitmapDescriptorFactory.defaultMarker(
                            if (atm.isAvailable) BitmapDescriptorFactory.HUE_GREEN
                            else BitmapDescriptorFactory.HUE_RED
                        ),
                        onClick = {
                            selectedAtm = atm
                            true
                        }
                    )
                }
            }
        }

        ATMListSection(
            atmList = atmList,
            selectedAtm = selectedAtm,
            onAtmSelected = { atm ->
                selectedAtm = atm
                cameraPositionState.position = CameraPosition.fromLatLngZoom(atm.position, 16f)
            }
        )
    }
}

@Composable
private fun ATMListSection(
    atmList: List<ATM>,
    selectedAtm: ATM?,
    onAtmSelected: (ATM) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "ATM disponibles (${atmList.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            if (atmList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun ATM trouvé à proximité",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(atmList) { atm ->
                        ATMCard(
                            atm = atm,
                            isSelected = atm.id == selectedAtm?.id,
                            onClick = { onAtmSelected(atm) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ATMCard(
    atm: ATM,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (atm.isAvailable)
                            Color(0xFF4CAF50).copy(alpha = 0.2f)
                        else
                            Color(0xFFE53935).copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = if (atm.isAvailable) Color(0xFF4CAF50) else Color(0xFFE53935),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = atm.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = atm.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = "${String.format("%.2f", atm.distance)} km",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
            if (!atm.isAvailable) {
                Text(
                    text = "Indisponible",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFE53935),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun PermissionDeniedContent(onRequestPermission: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Permission de localisation requise",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Pour afficher les ATM à proximité, nous avons besoin d'accéder à votre position",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Button(onClick = onRequestPermission) {
                Text("Autoriser la localisation")
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Chargement de votre position...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = onRetry) {
                Text("Réessayer")
            }
        }
    }
}
