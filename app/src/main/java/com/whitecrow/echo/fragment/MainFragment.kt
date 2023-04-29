package com.whitecrow.echo.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.whitecrow.echo.R
import com.whitecrow.echo.data.ChatMessage
import com.whitecrow.echo.model.ChatViewModel
import com.whitecrow.echo.util.themeColors
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Fragment
 */
@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: ChatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            ChatScreen(viewModel = viewModel)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initialise(requireContext())
    }

    @Composable
    fun ChatScreen(viewModel: ChatViewModel) {
        val context = LocalContext.current
        val colors = context.themeColors

        val isListening by viewModel.isListening.observeAsState(false)
        val chatMessages by viewModel.chatMessages.observeAsState(emptyList())
        val isLoading by viewModel.isLoading.observeAsState(false)

        // To remember the LazyListState
        val listState = rememberLazyListState()

        MaterialTheme(
            colors = colors
        ) {
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
                        if (ContextCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.RECORD_AUDIO
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            requestRecordAudioPermission.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            viewModel.startListening()
                        }
                    }
                ) {
                    Text(
                        text = getString(if (isListening) R.string.stop_listening else R.string.start_listening),
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    }

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

    private val requestRecordAudioPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            viewModel.startListening()
        } else {
            // Permission not granted, show a message
            Toast.makeText(
                requireContext(),
                "Permission denied. Voice recognition can't be used.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}