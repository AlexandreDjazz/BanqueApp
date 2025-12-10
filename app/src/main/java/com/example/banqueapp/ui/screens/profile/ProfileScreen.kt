package com.example.banqueapp.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.banqueapp.domain.models.User
import com.example.banqueapp.ui.screens.utils.ErrorScreen
import com.example.banqueapp.viewModels.UserUiState
import com.example.banqueapp.viewModels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    userViewModel: UserViewModel
) {
    val uiState by userViewModel.uiState.collectAsState()

    when (val currentState = uiState) {
        is UserUiState.Loading -> Box(Modifier.fillMaxSize()) { CircularProgressIndicator() }
        is UserUiState.LoggedOut -> Text("Non connecté")
        is UserUiState.Error -> ErrorScreen(currentState.message)
        is UserUiState.LoggedIn -> {
            ProfileContent(
                user = currentState.user,
                userViewModel = userViewModel,
                onNavigateToSettings = onNavigateToSettings
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    user: User,
    userViewModel: UserViewModel,
    onNavigateToSettings: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mon Profil") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            ProfileHeader(user = user)

            Spacer(modifier = Modifier.height(24.dp))

            ProfileSection(title = "Informations personnelles")

            ProfileInfoItem(
                icon = Icons.Default.Person,
                label = "Nom complet",
                value = user.name  // ← Directement depuis User
            )

            ProfileInfoItem(
                icon = Icons.Default.Email,
                label = "Email",
                value = user.email
            )

            // Phone manquant dans User → à ajouter en DB ou placeholder
            ProfileInfoItem(
                icon = Icons.Default.Phone,
                label = "Téléphone",
                value = "Non renseigné"  // ou ajoute phone à UserEntity
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProfileSection(title = "Actions")

            ProfileActionButton(
                icon = Icons.Default.Edit,
                text = "Modifier le profil",
                onClick = { /* Navigue vers EditProfile */ }
            )

            ProfileActionButton(
                icon = Icons.Default.Settings,
                text = "Paramètres",
                onClick = onNavigateToSettings  // ← Fonctionne déjà
            )

            ProfileActionButton(
                icon = Icons.Default.Info,
                text = "Aide et support",
                onClick = { /* Ouvre support */ }
            )

            ProfileActionButton(
                icon = Icons.Default.ExitToApp,
                text = "Se déconnecter",
                onClick = { userViewModel.onLogout() },  // ✅ Utilise UserViewModel
                isDestructive = true
            )
        }
    }
}

//pas fini
@Composable
fun ProfileHeader(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar",
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = user.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ProfileSection(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun ProfileInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ProfileActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (isDestructive) MaterialTheme.colorScheme.error
                          else MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}