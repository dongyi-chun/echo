package com.whitecrow.echo.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.whitecrow.echo.R
import com.whitecrow.echo.model.VoiceRecognitionViewModel
import com.whitecrow.echo.util.themeColors

/**
 * Main Fragment
 */
class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: VoiceRecognitionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            VoiceRecognitionScreen(viewModel = viewModel)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initialise(requireContext())
    }

    @Composable
    fun VoiceRecognitionScreen(viewModel: VoiceRecognitionViewModel) {
        val context = LocalContext.current
        val colors = context.themeColors

        val recognisedText by viewModel.recognisedText.observeAsState("")
        val isListening by viewModel.isListening.observeAsState(false)

        MaterialTheme(
            colors = colors
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                Text(text = recognisedText, fontSize = 30.sp, color = MaterialTheme.colors.onSurface)

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
}