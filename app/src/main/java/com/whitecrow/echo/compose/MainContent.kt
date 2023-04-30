package com.whitecrow.echo.compose

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.whitecrow.echo.R
import com.whitecrow.echo.data.ChatMessage
import com.whitecrow.echo.model.ChatViewModel
import com.whitecrow.echo.util.themeColors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainContent() {
    val context = LocalContext.current
    val colors = context.themeColors

    val viewModel: ChatViewModel = hiltViewModel()
    viewModel.initialise(context)

    val isListening by viewModel.isListening.observeAsState(false)
    val chatMessages by viewModel.chatMessages.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    // To remember the LazyListState
    val listState = rememberLazyListState()

    // Request a specific permission, e.g. Manifest.permission.RECORD_AUDIO
    val permissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startListening()
        } else {
            // Permission not granted, show a message
            Toast.makeText(
                context,
                "Permission denied. Voice recognition can't be used.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    MaterialTheme(
        colors = colors
    ) {
        // Add TopAppBar to your UI hierarchy
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.app_name))
            }
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState
            ) {
                items(
                    count = chatMessages.size + if (isLoading) 1 else 0,
                    itemContent = { index ->
                        if (index == chatMessages.size && isLoading) {
                            LoadingDots()
                        } else {
                            val message = chatMessages[index]
                            Text(
                                text = message.content,
                                fontSize = 18.sp,
                                color = MaterialTheme.colors.onSurface,
                                fontWeight = if (message is ChatMessage.Input) FontWeight.Bold else FontWeight.Normal,
                                fontStyle = if (message is ChatMessage.Input) FontStyle.Italic else FontStyle.Normal
                            )
                        }
                    }
                )
            }

            // Add LaunchedEffect with chatMessages as the key for auto scrolling to the last
            LaunchedEffect(chatMessages) {
                if (chatMessages.isNotEmpty()) {
                    listState.animateScrollToItem(chatMessages.lastIndex)
                }
            }

            Button(
                onClick = {
                    requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            ) {
                Text(
                    text = context.getString(if (isListening) R.string.stop_listening else R.string.start_listening),
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    MaterialTheme {
        MainContent()
    }
}

@Preview
@Composable
fun LoadingDots() {
    val dotCount = 3
    val duration = 500

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        val infiniteTransition = rememberInfiniteTransition()

        for (i in 0 until dotCount) {
            val delay = i * duration / dotCount
            val scale by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(duration, easing = LinearEasing, delayMillis = delay),
                    repeatMode = RepeatMode.Reverse
                )
            )
            Text(
                text = ".",
                fontSize = 18.sp,
                modifier = Modifier.scale(scale)
            )
        }
    }
}
