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
import androidx.camera.core.Camera;
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
import com.example.interpretels.ml.SignClassifier;
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
    private FloatingActionButton fabFlashlight;

    private HandDetector handDetector;
    private SignClassifier signClassifier;
    private ExecutorService cameraExecutor;
    private Camera camera;
    private boolean isFlashlightOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView);
        tvTranslation = findViewById(R.id.tvTranslation);
        fabClose = findViewById(R.id.fabClose);
        fabFlashlight = findViewById(R.id.fabFlashlight);

        fabClose.setOnClickListener(v -> finish());
        fabFlashlight.setOnClickListener(v -> toggleFlashlight());

        // Inicializar HandDetector y SignClassifier
        handDetector = new HandDetector(this, this);
        signClassifier = new SignClassifier();
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
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build();

        final int[] frameCount = {0};
        imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
            frameCount[0]++;
            if (frameCount[0] % 10 == 0) {
                analyzeImage(imageProxy);
            } else {
                imageProxy.close();
            }
        });

        // Selector de cámara trasera
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // Bind y GUARDAR la referencia a la cámara
        cameraProvider.unbindAll();
        camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

        Log.d(TAG, "Cámara configurada correctamente");
    }

    private void analyzeImage(ImageProxy imageProxy) {
        try {
            Bitmap bitmap = imageProxyToBitmap(imageProxy);

            if (bitmap != null) {
                handDetector.detectHands(bitmap);
                bitmap.recycle();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error analizando imagen", e);
        } finally {
            imageProxy.close();
        }
    }

    // ✅ MÉTODO REFACTORIZADO - Dividido en 3 métodos más pequeños
    private Bitmap imageProxyToBitmap(ImageProxy image) {
        try {
            byte[] nv21 = convertImageProxyToNV21(image);
            return convertNV21ToBitmap(nv21, image.getWidth(), image.getHeight());
        } catch (Exception e) {
            Log.e(TAG, "Error convirtiendo ImageProxy a Bitmap", e);
            return null;
        }
    }

    private byte[] convertImageProxyToNV21(ImageProxy image) {
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

        return nv21;
    }

    private Bitmap convertNV21ToBitmap(byte[] nv21, int width, int height) {
        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, width, height, null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, out);

        byte[] imageBytes = out.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        Log.d(TAG, "Bitmap creado: " + (bitmap != null ? bitmap.getWidth() + "x" + bitmap.getHeight() : "null"));

        return bitmap;
    }

    @Override
    public void onHandsDetected(HandLandmarkerResult result) {
        runOnUiThread(() -> {
            if (result.landmarks().isEmpty()) {
                tvTranslation.setText("🖐️ No se detectaron manos");
            } else {
                String sign = signClassifier.classify(result.landmarks().get(0));
                tvTranslation.setText("✅ Seña detectada: " + sign);
                Log.d(TAG, "Seña clasificada: " + sign);
            }
        });
    }

    @Override
    public void onError(String error) {
        runOnUiThread(() -> {
            tvTranslation.setText("⚠️ Error: " + error);
            Log.e(TAG, error);
        });
    }

    // ✅ MÉTODO CORREGIDO - Usa ContextCompat.getColor() en lugar de getResources().getColor()
    private void toggleFlashlight() {
        if (camera == null) {
            Toast.makeText(this, "Cámara no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!camera.getCameraInfo().hasFlashUnit()) {
            Toast.makeText(this, "Este dispositivo no tiene flash", Toast.LENGTH_SHORT).show();
            return;
        }

        isFlashlightOn = !isFlashlightOn;
        camera.getCameraControl().enableTorch(isFlashlightOn);

        if (isFlashlightOn) {
            fabFlashlight.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            ContextCompat.getColor(this, android.R.color.holo_orange_light)
                    )
            );
        } else {
            fabFlashlight.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            ContextCompat.getColor(this, R.color.accent)
                    )
            );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (camera != null && isFlashlightOn) {
            camera.getCameraControl().enableTorch(false);
        }

        if (handDetector != null) {
            handDetector.close();
        }
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
}