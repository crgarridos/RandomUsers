package com.crgarridos.randomusers.ui.compose.userdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.crgarridos.randomusers.R
import com.crgarridos.randomusers.ui.compose.common.PreviewLightAndDarkTheme
import com.crgarridos.randomusers.ui.compose.common.ErrorScreen
import com.crgarridos.randomusers.ui.compose.common.FullScreenLoading
import com.crgarridos.randomusers.ui.compose.model.UiUser
import com.crgarridos.randomusers.ui.compose.theme.RandomUsersTheme
import com.crgarridos.randomusers.ui.compose.userlist.previewUserList

sealed class UserDetailUiState {
    object Loading : UserDetailUiState()
    data class Success(val user: UiUser) : UserDetailUiState()
    data class Error(val message: String) : UserDetailUiState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    uiState: UserDetailUiState,
    onNavigateBack: () -> Unit = {},
    onRetry: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contact Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is UserDetailUiState.Loading -> FullScreenLoading()
            is UserDetailUiState.Error -> ErrorScreen(
                message = uiState.message,
                onRetry = onRetry,
                modifier = Modifier.padding(paddingValues)
            )

            is UserDetailUiState.Success -> {
                UserDetailContent(user = uiState.user, modifier = Modifier.padding(paddingValues))
            }
        }
    }
}

@Composable
fun UserDetailContent(user: UiUser, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(user.largePictureUrl)
                .crossfade(true)
                .placeholder(R.drawable.profile_placeholder)
                .error(R.drawable.profile_placeholder)
                .build(),
            contentDescription = "${user.fullName} large picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
        )

        Text(
            text = user.fullName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                DetailInfoRow(icon = Icons.Filled.Email, label = "Email", value = user.email)
                HorizontalDivider()
                DetailInfoRow(icon = Icons.Filled.Phone, label = "Phone", value = user.phone)
                HorizontalDivider()
                DetailInfoRow(icon = Icons.Filled.Place, label = "Location", value = user.location)
            }
        }
    }
}

@Composable
private fun DetailInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(all = 24.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// Previews
@Composable
@PreviewLightAndDarkTheme
private fun DetailInfoRowPreview() {
    DetailInfoRow(icon = Icons.Filled.Phone, label = "Phone", value = "06 64 64 64 64")
}

@Composable
@PreviewLightAndDarkTheme
private fun UserDetailScreenPreview_Successd() {
    RandomUsersTheme {
        UserDetailScreen(
            uiState = UserDetailUiState.Success(previewUserList.first()),
        )
    }
}

@Composable
@PreviewLightAndDarkTheme
private fun UserDetailScreenPreview_Loading() {
    RandomUsersTheme {
        UserDetailScreen(
            uiState = UserDetailUiState.Loading,
        )
    }
}

@Composable
@PreviewLightAndDarkTheme
private fun UserDetailScreenPreview_Error() {
    RandomUsersTheme {
        UserDetailScreen(
            uiState = UserDetailUiState.Error("Failed to load user details."),
        )

    }
}