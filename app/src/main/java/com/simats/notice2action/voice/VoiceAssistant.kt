package com.simats.notice2action.voice

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.simats.notice2action.AppScreen
import java.util.Locale

/**
 * State and Controller for Voice Assistant Speech Recognition & Text-To-Speech.
 */
class VoiceAssistantManager(private val context: Context) : TextToSpeech.OnInitListener {

    var isListening by mutableStateOf(false)
    var isSpeaking by mutableStateOf(false)
    var ttsEnabled by mutableStateOf(true)
    var recognizedText by mutableStateOf("")
    var statusMessage by mutableStateOf("Tap the microphone or say a command...")
    var lastResponseText by mutableStateOf("")

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    init {
        initSpeech()
    }

    private fun initSpeech() {
        try {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {
                            isListening = true
                            statusMessage = "Listening... Speak now."
                        }

                        override fun onBeginningOfSpeech() {
                            statusMessage = "Processing your voice..."
                        }

                        override fun onRmsChanged(rmsdB: Float) {}
                        override fun onBufferReceived(buffer: ByteArray?) {}
                        override fun onEndOfSpeech() {
                            isListening = false
                            statusMessage = "Processing command..."
                        }

                        override fun onError(error: Int) {
                            isListening = false
                            statusMessage = "Could not hear audio clearly. Try typing below!"
                        }

                        override fun onResults(results: Bundle?) {
                            isListening = false
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            if (!matches.isNullOrEmpty()) {
                                recognizedText = matches[0]
                            }
                        }

                        override fun onPartialResults(partialResults: Bundle?) {
                            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            if (!matches.isNullOrEmpty()) {
                                recognizedText = matches[0]
                            }
                        }

                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }
            }
        } catch (_: Exception) {}

        try {
            textToSpeech = TextToSpeech(context, this)
        } catch (_: Exception) {}
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale.US
            isTtsReady = true
        }
    }

    fun startListening(launcher: ActivityResultLauncher<Intent>? = null) {
        recognizedText = ""
        lastResponseText = ""
        stopSpeaking()

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a Notice2Action command (e.g. Scan document, Show my tasks)...")
        }

        var started = false
        if (speechRecognizer != null && SpeechRecognizer.isRecognitionAvailable(context)) {
            try {
                speechRecognizer?.startListening(intent)
                started = true
            } catch (_: Exception) {}
        }

        if (!started && launcher != null) {
            try {
                launcher.launch(intent)
                started = true
            } catch (_: Exception) {
                isListening = false
                statusMessage = "Listening... Speak or type command below."
            }
        } else if (!started) {
            isListening = true
            statusMessage = "Listening... Speak or type command below."
        }
    }

    fun stopListening() {
        isListening = false
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
    }

    fun speak(text: String) {
        if (!ttsEnabled || !isTtsReady || text.isBlank()) return
        stopSpeaking()
        isSpeaking = true
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "NoticeVoiceAssistant")
    }

    fun stopSpeaking() {
        isSpeaking = false
        try {
            textToSpeech?.stop()
        } catch (_: Exception) {}
    }

    fun destroy() {
        try {
            speechRecognizer?.destroy()
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        } catch (_: Exception) {}
    }
}

/**
 * Floating Voice Assistant Trigger Button.
 */
@Composable
fun VoiceAssistantFloatingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .shadow(12.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF2563EB), Color(0xFF4F46E5))
                )
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Mic,
            contentDescription = "Voice Assistant",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}

/**
 * Voice Assistant Overlay Dialog.
 */
@Composable
fun VoiceAssistantDialog(
    onDismiss: () -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    val context = LocalContext.current
    val voiceManager = remember { VoiceAssistantManager(context) }
    var inputText by remember { mutableStateOf("") }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                voiceManager.recognizedText = matches[0]
                voiceManager.statusMessage = "Voice Recognized"
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a Notice2Action command (e.g. Scan document, Show my tasks)...")
            }
            try {
                speechLauncher.launch(intent)
            } catch (_: Exception) {}
        } else {
            Toast.makeText(context, "Microphone permission required for voice recognition", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchVoiceInput() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a Notice2Action command (e.g. Scan document, Show my tasks)...")
            }
            try {
                speechLauncher.launch(intent)
                voiceManager.isListening = true
                voiceManager.statusMessage = "Listening... Speak now."
            } catch (e: Exception) {
                voiceManager.statusMessage = "Speech input unavailable on device."
            }
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            voiceManager.destroy()
        }
    }

    LaunchedEffect(voiceManager.recognizedText) {
        if (voiceManager.recognizedText.isNotBlank()) {
            val result = VoiceCommandHandler.processCommand(voiceManager.recognizedText, onNavigate)
            val responseText = when (result) {
                is VoiceCommandResult.Navigate -> result.responseText
                is VoiceCommandResult.ActionExecuted -> result.responseText
                is VoiceCommandResult.Information -> result.responseText
                is VoiceCommandResult.Unrecognized -> result.responseText
            }
            voiceManager.lastResponseText = responseText
            voiceManager.statusMessage = "Done"
            voiceManager.speak(responseText)
        }
    }

    fun handleUserSubmit(query: String) {
        if (query.isBlank()) return
        voiceManager.recognizedText = query
        inputText = ""
    }

    Dialog(
        onDismissRequest = {
            voiceManager.stopSpeaking()
            voiceManager.stopListening()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFDBEAFE), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎙️", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Notice2Action Voice Assistant",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Speak or type your command",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = { voiceManager.ttsEnabled = !voiceManager.ttsEnabled }) {
                            Icon(
                                imageVector = if (voiceManager.ttsEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                                contentDescription = "Toggle TTS",
                                tint = if (voiceManager.ttsEnabled) Color(0xFF2563EB) else Color(0xFF94A3B8)
                            )
                        }
                        IconButton(onClick = {
                            voiceManager.stopSpeaking()
                            voiceManager.stopListening()
                            onDismiss()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF64748B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mic Pulse Box
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            if (voiceManager.isListening) Color(0xFF2563EB)
                            else if (voiceManager.isSpeaking) Color(0xFF10B981)
                            else Color(0xFFEFF6FF)
                        )
                        .clickable {
                            if (voiceManager.isListening) {
                                voiceManager.stopListening()
                            } else {
                                launchVoiceInput()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "Mic",
                            tint = if (voiceManager.isListening || voiceManager.isSpeaking) Color.White else Color(0xFF2563EB),
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = if (voiceManager.isListening) "Tap to Stop" else "Tap to Speak",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (voiceManager.isListening || voiceManager.isSpeaking) Color.White.copy(alpha = 0.9f) else Color(0xFF2563EB)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = voiceManager.statusMessage,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )

                if (voiceManager.recognizedText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "“${voiceManager.recognizedText}”",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )
                    }
                }

                if (voiceManager.lastResponseText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEEF2FF), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Text(text = "🤖", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = voiceManager.lastResponseText,
                                fontSize = 13.sp,
                                color = Color(0xFF1E1B4B),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Suggested Voice Commands",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                val suggestions = listOf(
                    "Scan Document",
                    "Show My Tasks",
                    "Reminders",
                    "Remind me to submit report",
                    "How many tasks do I have?",
                    "Open Calendar",
                    "How to scan a notice"
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(suggestions) { prompt ->
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(16.dp))
                                .clickable { handleUserSubmit(prompt) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = prompt,
                                fontSize = 11.sp,
                                color = Color(0xFF2563EB),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Or type command...", fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { handleUserSubmit(inputText) },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFF2563EB), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
