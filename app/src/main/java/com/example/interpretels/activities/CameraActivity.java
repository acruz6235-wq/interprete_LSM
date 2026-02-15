package com.example.interpretels.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.interpretels.R;
import com.example.interpretels.camera.HandDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity implements HandDetector.HandDetectionListener {

    private static final String TAG = "CameraActivity";
    private static final int CAMERA_PERMISSION_CODE = 100;

    private PreviewView previewView;
    private TextView tvTranslation;
    private FloatingActionButton fabClose;

    private HandDetector handDetector;
    private ExecutorService cameraExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView);
        tvTranslation = findViewById(R.id.tvTranslation);
        fabClose = findViewById(R.id.fabClose);

        fabClose.setOnClickListener(v -> finish());

        // Inicializar HandDetector
        handDetector = new HandDetector(this, this);
        cameraExecutor = Executors.newSingleThreadExecutor();

        // Verificar permiso de cámara
        if (checkCameraPermission()) {
            startCamera();
        } else {
            requestCameraPermission();
        }
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error al iniciar cámara", e);
                Toast.makeText(this, "Error al iniciar cámara", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases(ProcessCameraProvider cameraProvider) {
        // Preview
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Image Analysis para MediaPipe
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)  // ← AGREGAR ESTO
                .build();

        // Procesar solo cada 10 frames para debugging
        final int[] frameCount = {0};
        imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
            frameCount[0]++;
            if (frameCount[0] % 10 == 0) {  // Solo cada 10 frames
                analyzeImage(imageProxy);
            } else {
                imageProxy.close();
            }
        });

        // Selector de cámara trasera
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // Bind
        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

        Log.d(TAG, "Cámara configurada correctamente");
    }

    private void analyzeImage(ImageProxy imageProxy) {
        try {
            // Convertir ImageProxy a Bitmap
            Bitmap bitmap = imageProxyToBitmap(imageProxy);

            if (bitmap != null) {
                // Detectar manos
                handDetector.detectHands(bitmap);
            }

        } finally {
            imageProxy.close();
        }
    }

    private Bitmap imageProxyToBitmap(ImageProxy image) {
        try {
            // Obtener el buffer YUV
            ImageProxy.PlaneProxy[] planes = image.getPlanes();
            ByteBuffer yBuffer = planes[0].getBuffer();
            ByteBuffer uBuffer = planes[1].getBuffer();
            ByteBuffer vBuffer = planes[2].getBuffer();

            int ySize = yBuffer.remaining();
            int uSize = uBuffer.remaining();
            int vSize = vBuffer.remaining();

            byte[] nv21 = new byte[ySize + uSize + vSize];

            yBuffer.get(nv21, 0, ySize);
            vBuffer.get(nv21, ySize, vSize);
            uBuffer.get(nv21, ySize + vSize, uSize);

            YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21,
                    image.getWidth(), image.getHeight(), null);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()),
                    100, out);

            byte[] imageBytes = out.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            Log.d(TAG, "Bitmap creado: " + (bitmap != null ? bitmap.getWidth() + "x" + bitmap.getHeight() : "null"));

            return bitmap;

        } catch (Exception e) {
            Log.e(TAG, "Error convirtiendo ImageProxy a Bitmap", e);
            return null;
        }
    }
    @Override
    public void onHandsDetected(HandLandmarkerResult result) {
        runOnUiThread(() -> {
            if (result.landmarks().isEmpty()) {
                tvTranslation.setText("No se detectaron manos");
            } else {
                int numHands = result.landmarks().size();
                tvTranslation.setText("Detectadas " + numHands + " mano(s)");

                // Aquí después agregaremos la clasificación de señas
                Log.d(TAG, "Manos detectadas: " + numHands);
            }
        });
    }

    @Override
    public void onError(String error) {
        runOnUiThread(() -> {
            tvTranslation.setText("Error: " + error);
            Log.e(TAG, error);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handDetector != null) {
            handDetector.close();
        }
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
}