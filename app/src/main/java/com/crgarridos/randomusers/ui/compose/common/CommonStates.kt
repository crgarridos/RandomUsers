package com.crgarridos.randomusers.ui.compose.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun FullScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


@Composable
fun FullScreenStatusWithRetry(
    modifier: Modifier = Modifier,
    message: String,
    icon: ImageVector,
    color: Color,
    onRetry: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "",
            modifier = Modifier.size(64.dp),
            tint = color
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = color,
            textAlign = TextAlign.Center
        )
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}


@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    message: String,
    icon: ImageVector = Icons.Filled.Warning,
    onRetry: (() -> Unit)? = null,
) {
    FullScreenStatusWithRetry(
        modifier = modifier,
        message = message,
        icon = icon,
        color = MaterialTheme.colorScheme.error,
        onRetry = onRetry
    )
}

@Composable
fun ConnectivityIssueScreen(modifier: Modifier = Modifier, onRetry: () -> Unit) {
    ErrorScreen(
        modifier = modifier,
        message = "Oops! Looks like you\'re offline.\nPlease check your internet connection and try again.",
        icon = Icons.Filled.Warning,
        onRetry = onRetry
    )
}
