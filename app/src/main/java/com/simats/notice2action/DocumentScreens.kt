package com.simats.notice2action

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import java.io.InputStream
import android.content.Context
import android.provider.OpenableColumns
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun DocumentScannerCornerGuides(
    modifier: Modifier = Modifier,
    cornerColor: Color = Color(0xFF38BDF8),
    strokeWidth: Float = 7f,
    cornerLength: Float = 44f
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Top-Left corner
        drawLine(cornerColor, Offset(0f, 0f), Offset(cornerLength, 0f), strokeWidth)
        drawLine(cornerColor, Offset(0f, 0f), Offset(0f, cornerLength), strokeWidth)

        // Top-Right corner
        drawLine(cornerColor, Offset(w, 0f), Offset(w - cornerLength, 0f), strokeWidth)
        drawLine(cornerColor, Offset(w, 0f), Offset(w, cornerLength), strokeWidth)

        // Bottom-Left corner
        drawLine(cornerColor, Offset(0f, h), Offset(cornerLength, h), strokeWidth)
        drawLine(cornerColor, Offset(0f, h), Offset(0f, h - cornerLength), strokeWidth)

        // Bottom-Right corner
        drawLine(cornerColor, Offset(w, h), Offset(w - cornerLength, h), strokeWidth)
        drawLine(cornerColor, Offset(w, h), Offset(w, h - cornerLength), strokeWidth)
    }
}

@Composable
fun ScanDocumentScreen(
    onBackClicked: () -> Unit,
    onUseDocument: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var camera by remember { mutableStateOf<Camera?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isTorchOn by remember { mutableStateOf(false) }
    var isFrontCamera by remember { mutableStateOf(false) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var flashBlink by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                capturedBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
            } catch (e: Exception) {
                Toast.makeText(context, "Could not open selected image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun takePhoto() {
        val capture = imageCapture
        if (capture == null) {
            Toast.makeText(context, "Camera is initializing, please wait...", Toast.LENGTH_SHORT).show()
            return
        }
        if (isCapturing) return

        isCapturing = true
        flashBlink = true

        val photoFile = File(context.cacheDir, "scan_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        val executor = ContextCompat.getMainExecutor(context)

        capture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    flashBlink = false
                    try {
                        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                        capturedBitmap = bitmap
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to load captured photo", Toast.LENGTH_SHORT).show()
                    } finally {
                        isCapturing = false
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    flashBlink = false
                    isCapturing = false
                    Toast.makeText(context, "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    fun toggleTorch() {
        if (isFrontCamera) {
            Toast.makeText(context, "Flash is not available on front camera", Toast.LENGTH_SHORT).show()
            return
        }
        val cam = camera
        if (cam != null && cam.cameraInfo.hasFlashUnit()) {
            val newState = !isTorchOn
            cam.cameraControl.enableTorch(newState)
            isTorchOn = newState
        } else {
            Toast.makeText(context, "Flash not supported or camera not ready", Toast.LENGTH_SHORT).show()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0F172A) // Sleek dark camera background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Top Camera App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClicked,
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "DOCUMENT SCANNER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = if (capturedBitmap != null) "Preview Capture" else "Original Camera",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Flash / Torch Toggle
                    IconButton(
                        onClick = { toggleTorch() },
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                if (isTorchOn) Color(0xFFFBBF24).copy(alpha = 0.25f)
                                else Color.White.copy(alpha = 0.15f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Toggle Flash",
                            tint = if (isTorchOn) Color(0xFFFBBF24) else Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Camera Viewfinder / Captured Photo Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    if (capturedBitmap != null) {
                        // Display captured photo
                        Image(
                            bitmap = capturedBitmap!!.asImageBitmap(),
                            contentDescription = "Captured Document",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )

                        // Top badge showing capture success
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 16.dp)
                                .background(Color(0xFF16A34A).copy(alpha = 0.9f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "✓ Photo Captured Successfully",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else if (!hasCameraPermission) {
                        // Permission request state
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera Permission",
                                tint = Color(0xFF60A5FA),
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Camera Permission Required",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Grant camera permission to capture real document photos directly from your device.",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8),
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(0.85f).height(46.dp)
                            ) {
                                Text(
                                    text = "Allow Camera Access",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            TextButton(
                                onClick = { galleryLauncher.launch("image/*") }
                            ) {
                                Text(
                                    text = "Or Choose from Photo Gallery",
                                    fontSize = 13.sp,
                                    color = Color(0xFF60A5FA)
                                )
                            }
                        }
                    } else {
                        // Real Camera Preview View via CameraX
                        AndroidView(
                            factory = { ctx ->
                                val previewView = PreviewView(ctx).apply {
                                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                                    scaleType = PreviewView.ScaleType.FILL_CENTER
                                }
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.setSurfaceProvider(previewView.surfaceProvider)
                                    }
                                    val capture = ImageCapture.Builder()
                                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                        .build()
                                    imageCapture = capture

                                    val cameraSelector = if (isFrontCamera) {
                                        CameraSelector.DEFAULT_FRONT_CAMERA
                                    } else {
                                        CameraSelector.DEFAULT_BACK_CAMERA
                                    }

                                    try {
                                        cameraProvider.unbindAll()
                                        camera = cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview,
                                            capture
                                        )
                                    } catch (exc: Exception) {
                                        exc.printStackTrace()
                                    }
                                }, ContextCompat.getMainExecutor(ctx))
                                previewView
                            },
                            update = { previewView ->
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.setSurfaceProvider(previewView.surfaceProvider)
                                    }
                                    val capture = ImageCapture.Builder()
                                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                        .build()
                                    imageCapture = capture

                                    val cameraSelector = if (isFrontCamera) {
                                        CameraSelector.DEFAULT_FRONT_CAMERA
                                    } else {
                                        CameraSelector.DEFAULT_BACK_CAMERA
                                    }

                                    try {
                                        cameraProvider.unbindAll()
                                        camera = cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview,
                                            capture
                                        )
                                        if (!isFrontCamera && camera?.cameraInfo?.hasFlashUnit() == true) {
                                            camera?.cameraControl?.enableTorch(isTorchOn)
                                        }
                                    } catch (exc: Exception) {
                                        exc.printStackTrace()
                                    }
                                }, ContextCompat.getMainExecutor(context))
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Document Boundary Frame & Corner Guides
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .fillMaxHeight(0.88f)
                        ) {
                            DocumentScannerCornerGuides(
                                modifier = Modifier.fillMaxSize(),
                                cornerColor = Color(0xFF38BDF8),
                                strokeWidth = 8f,
                                cornerLength = 48f
                            )

                            // Alignment instruction badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 14.dp)
                                    .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Align document within corners • Keep steady",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Shutter Blink Feedback
                    if (flashBlink) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White.copy(alpha = 0.75f))
                        )
                    }
                }

                // Action Controls Section
                if (capturedBitmap != null) {
                    // Actions after photo has been taken
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    capturedBitmap = null
                                    flashBlink = false
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.5.dp, Color(0xFF64748B)),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Retake",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Retake Photo",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    ActiveDocumentSession.setDocument(
                                        uri = null,
                                        bitmap = capturedBitmap,
                                        name = "scanned_notice_" + System.currentTimeMillis() + ".png",
                                        sizeText = "Live Camera Scan",
                                        image = true
                                    )
                                    onUseDocument()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Use",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Use Document",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Camera Shutter & Hardware Controls
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Gallery photo picker
                        IconButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color.White.copy(alpha = 0.12f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Pick from Gallery",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Shutter Button
                        Box(
                            modifier = Modifier
                                .size(78.dp)
                                .border(3.5.dp, Color.White, CircleShape)
                                .padding(5.dp)
                                .clip(CircleShape)
                                .background(if (isCapturing) Color(0xFF38BDF8) else Color.White)
                                .clickable(enabled = hasCameraPermission && !isCapturing) {
                                    takePhoto()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCapturing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = Color.White,
                                    strokeWidth = 3.dp
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .border(2.dp, Color(0xFF0F172A), CircleShape)
                                        .background(Color.White, CircleShape)
                                )
                            }
                        }

                        // Switch Camera (Flip Front/Back)
                        IconButton(
                            onClick = {
                                isFrontCamera = !isFrontCamera
                                isTorchOn = false
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color.White.copy(alpha = 0.12f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cameraswitch,
                                contentDescription = "Flip Camera",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

object ActiveDocumentSession {
    var documentUri by mutableStateOf<Uri?>(null)
    var documentBitmap by mutableStateOf<Bitmap?>(null)
    var documentName by mutableStateOf<String>("innovation-week-2026.pdf")
    var documentSizeText by mutableStateOf<String>("2.4 MB")
    var isImage by mutableStateOf<Boolean>(false)
    var isReady by mutableStateOf<Boolean>(true)

    fun setDocument(
        uri: Uri?,
        bitmap: Bitmap?,
        name: String,
        sizeText: String,
        image: Boolean
    ) {
        documentUri = uri
        documentBitmap = bitmap
        documentName = name
        documentSizeText = sizeText
        isImage = image
        isReady = true
    }

    fun clear() {
        documentUri = null
        documentBitmap = null
        documentName = "innovation-week-2026.pdf"
        documentSizeText = "2.4 MB"
        isImage = false
        isReady = false
    }
}

fun getFileNameAndSize(context: Context, uri: Uri): Pair<String, Long> {
    var name = "notice_${System.currentTimeMillis()}"
    var size = 0L
    try {
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) {
                        name = cursor.getString(nameIndex) ?: name
                    }
                    if (sizeIndex != -1) {
                        size = cursor.getLong(sizeIndex)
                    }
                }
            }
        } else if (uri.scheme == "file") {
            uri.path?.let { path ->
                val file = File(path)
                name = file.name
                size = file.length()
            }
        }
    } catch (_: Exception) {}
    return Pair(name, size)
}

fun formatDocumentSize(bytes: Long): String {
    if (bytes <= 0L) return "Document ready"
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    return when {
        mb >= 1.0 -> String.format(Locale.US, "%.1f MB", mb)
        kb >= 1.0 -> String.format(Locale.US, "%.0f KB", kb)
        else -> "$bytes B"
    }
}

@Composable
fun UploadDocumentScreen(
    onBackClicked: () -> Unit,
    onContinue: () -> Unit,
    onScanClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedUri by remember { mutableStateOf<Uri?>(ActiveDocumentSession.documentUri) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(ActiveDocumentSession.documentBitmap) }
    var fileName by remember { mutableStateOf(ActiveDocumentSession.documentName) }
    var fileSizeText by remember { mutableStateOf(ActiveDocumentSession.documentSizeText) }
    var isImageFile by remember { mutableStateOf(ActiveDocumentSession.isImage) }
    var hasFile by remember { mutableStateOf(ActiveDocumentSession.documentUri != null || ActiveDocumentSession.documentBitmap != null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf(if (hasFile) 1f else 0f) }

    fun processUri(uri: Uri, isFromGallery: Boolean) {
        try {
            val (name, size) = getFileNameAndSize(context, uri)
            val sizeFormatted = formatDocumentSize(size)
            val mimeType = context.contentResolver.getType(uri) ?: ""
            val isImg = isFromGallery || mimeType.startsWith("image/") ||
                    name.endsWith(".jpg", true) || name.endsWith(".png", true) || name.endsWith(".jpeg", true)

            var bmp: Bitmap? = null
            if (isImg) {
                try {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        val opts = BitmapFactory.Options().apply { inSampleSize = 2 }
                        bmp = BitmapFactory.decodeStream(stream, null, opts)
                    }
                } catch (_: Exception) {}
            }

            selectedUri = uri
            selectedBitmap = bmp
            fileName = name
            fileSizeText = sizeFormatted
            isImageFile = isImg
            hasFile = true
            isUploading = true
            uploadProgress = 0.25f

            ActiveDocumentSession.setDocument(
                uri = uri,
                bitmap = bmp,
                name = name,
                sizeText = sizeFormatted,
                image = isImg
            )

            coroutineScope.launch {
                delay(120)
                uploadProgress = 0.70f
                delay(150)
                uploadProgress = 1.0f
                isUploading = false
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open selected file: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    // Opens phone Gallery to pick photos/scans
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            processUri(uri, isFromGallery = true)
        }
    }

    // Opens phone File manager / document picker for PDFs and files
    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            processUri(uri, isFromGallery = false)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Header Row with Material Icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color(0xFFEEF2FF), CircleShape)
                                .clickable { onBackClicked() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "DOCUMENT CAPTURE & PROCESSING",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Upload Document",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }

                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Select a notice, timetable, circular or document from your phone to automatically extract deadlines and actionable tasks.",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Realistic Main Upload Dropzone Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(20.dp))
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(20.dp))
                        .border(BorderStroke(1.5.dp, Color(0xFFC7D2FE)), RoundedCornerShape(20.dp))
                        .clickable {
                            fileLauncher.launch("*/*")
                        }
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White, CircleShape)
                                .shadow(2.dp, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = "Upload",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (hasFile) "Tap to change document" else "Tap to choose notice or file",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Supports PDF, JPG, PNG or DOCX • Up to 25 MB",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("PDF", "JPG", "PNG", "DOCX").forEach { format ->
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                                ) {
                                    Text(
                                        text = format,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF475569),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons: Real Phone Gallery & Real Phone Browse Files
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Choose from Gallery Button
                    OutlinedButton(
                        onClick = {
                            galleryLauncher.launch("image/*")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, Color(0xFF93C5FD)),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFEFF6FF))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Gallery",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Gallery",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "Photos & Scans",
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    // Browse Files Button
                    OutlinedButton(
                        onClick = {
                            fileLauncher.launch("*/*")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = "Browse Files",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Browse Files",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "PDF & Docs",
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scan Physical Notice Option
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onScanClicked() },
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFEEF2FF), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Camera",
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Scan Physical Document",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "Use original camera with autofocus",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open camera",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Selected File Status & Preview Card
                if (hasFile) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(16.dp))
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .border(BorderStroke(1.dp, Color(0xFFE2E8F0)), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    if (isImageFile && selectedBitmap != null) {
                                        Image(
                                            bitmap = selectedBitmap!!.asImageBitmap(),
                                            contentDescription = "Thumbnail",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .background(
                                                    if (fileName.endsWith(".pdf", true)) Color(0xFFFEE2E2) else Color(0xFFEEF2FF),
                                                    RoundedCornerShape(10.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (fileName.endsWith(".pdf", true)) Icons.Default.PictureAsPdf else Icons.Default.Description,
                                                contentDescription = "Document",
                                                tint = if (fileName.endsWith(".pdf", true)) Color(0xFFDC2626) else Color(0xFF2563EB),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = fileName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A),
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (isUploading) "Uploading • $fileSizeText" else "$fileSizeText • Ready",
                                                fontSize = 11.sp,
                                                color = Color(0xFF64748B)
                                            )
                                            if (!isUploading) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Ready",
                                                    tint = Color(0xFF16A34A),
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        selectedUri = null
                                        selectedBitmap = null
                                        hasFile = false
                                        ActiveDocumentSession.clear()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove document",
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Linear progress indicator
                            LinearProgressIndicator(
                                progress = { uploadProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = Color(0xFF2563EB),
                                trackColor = Color(0xFFE2E8F0)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Verified for OCR & Action Extraction",
                                        fontSize = 10.sp,
                                        color = Color(0xFF16A34A),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Text(
                                    text = if (isUploading) "${(uploadProgress * 100).toInt()}%" else "100%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uploadProgress >= 1f) Color(0xFF16A34A) else Color(0xFF2563EB)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Continue Action Button
            Button(
                onClick = {
                    if (!hasFile) {
                        Toast.makeText(context, "Please choose a photo or document first", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    onContinue()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB)
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Continue to Preview",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Continue",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DocumentPreviewScreen(
    onBackClicked: () -> Unit,
    onContinue: () -> Unit
) {
    val documentTitle = ActiveDocumentSession.documentName.ifBlank { "INNOVATION WEEK 2026" }
    val documentSize = ActiveDocumentSession.documentSizeText
    val documentBitmap = ActiveDocumentSession.documentBitmap

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color(0xFFEEF2FF), CircleShape)
                                .clickable { onBackClicked() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "DOCUMENT CAPTURE & PROCESSING",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Document Preview",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }

                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Realistic Document Visual Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(16.dp))
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(BorderStroke(1.dp, Color(0xFFE2E8F0)), RoundedCornerShape(16.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        if (documentBitmap != null) {
                            Image(
                                bitmap = documentBitmap.asImageBitmap(),
                                contentDescription = "Document Preview",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            if (documentTitle.endsWith(".pdf", true)) Color(0xFFFEE2E2) else Color(0xFFEEF2FF),
                                            RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (documentTitle.endsWith(".pdf", true)) Icons.Default.PictureAsPdf else Icons.Default.Description,
                                        contentDescription = null,
                                        tint = if (documentTitle.endsWith(".pdf", true)) Color(0xFFDC2626) else Color(0xFF2563EB),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = documentTitle,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A),
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "$documentSize • Ready to extract",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Submit proposal by 20 October 2026, 5:00 PM.",
                            fontSize = 13.sp,
                            color = Color(0xFF334155),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onContinue() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Continue to Notice Details", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Continue",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun NoticeDetailsScreen(
    onBackClicked: () -> Unit,
    onGenerateActions: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFEEF2FF), CircleShape)
                                .clickable { onBackClicked() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "←", fontSize = 18.sp, color = Color(0xFF4F46E5), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "C • DOCUMENT CAPTURE & PROCESSING",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Notice details",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Innovation Week 2026",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(text = "Summary", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Submit a proposal, attend a team briefing and prepare for final presentations.",
                            fontSize = 13.sp,
                            color = Color(0xFF475569)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onGenerateActions() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text(text = "Generate Actions", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun MyDocumentsScreen(
    onBackClicked: () -> Unit,
    onUploadDocumentClicked: () -> Unit,
    onDocumentClicked: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "←",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155),
                        modifier = Modifier
                            .clickable { onBackClicked() }
                            .padding(end = 12.dp)
                    )
                    Text(
                        text = "My Documents",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                DocumentCard(
                    icon = "📄",
                    title = "Innovation Week 2026",
                    details = "PDF • 15 Oct 2026 • 2 pages",
                    tagText = "Verify dates",
                    tagBgColor = Color(0xFFFEF3C7),
                    tagTextColor = Color(0xFFD97706),
                    subtitle = "3 actions identified",
                    onClick = { onDocumentClicked() }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onUploadDocumentClicked() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEF2FF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Upload Document", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6))
                }
            }
        }
    }
}

@Composable
fun DocumentCard(
    icon: String,
    title: String,
    details: String,
    tagText: String,
    tagBgColor: Color,
    tagTextColor: Color,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFEFF6FF), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = icon, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = details,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(tagBgColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = tagText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = tagTextColor
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun DocumentDetailsScreen(
    onBackClicked: () -> Unit,
    onShareClicked: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "←",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155),
                        modifier = Modifier
                            .clickable { onBackClicked() }
                            .padding(end = 12.dp)
                    )
                    Text(
                        text = "Document details",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Innovation Week 2026",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Button(
                onClick = { onShareClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Share Notice", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun ShareNoticeScreen(
    onBackClicked: () -> Unit
) {
    var shareFeedback by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "←",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155),
                    modifier = Modifier
                        .clickable { onBackClicked() }
                        .padding(end = 12.dp)
                )
                Text(
                    text = "Share notice",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Innovation Week 2026",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Select a group or share option below:",
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { shareFeedback = "Notice shared with College Students!" },
                color = Color.White,
                shape = RoundedCornerShape(14.dp),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🎓", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "College Students", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text(text = "24 members • Innovation Week hub", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                    Text(text = "Share →", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { shareFeedback = "Notice link copied to clipboard!" },
                color = Color.White,
                shape = RoundedCornerShape(14.dp),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🔗", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Copy Notice Link", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text(text = "Share with anyone via direct link", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                    Text(text = "Copy", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                }
            }

            if (shareFeedback != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFDCFCE7), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✓ $shareFeedback", color = Color(0xFF16A34A), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
