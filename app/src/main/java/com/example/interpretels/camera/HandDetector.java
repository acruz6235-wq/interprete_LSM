package com.example.interpretels.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult;

public class HandDetector {

    private static final String TAG = "HandDetector";
    private static final String MODEL_NAME = "hand_landmarker.task";

    private HandLandmarker handLandmarker;
    private HandDetectionListener listener;

    public interface HandDetectionListener {
        void onHandsDetected(HandLandmarkerResult result);
        void onError(String error);
    }

    public HandDetector(Context context, HandDetectionListener listener) {
        this.listener = listener;
        initializeHandLandmarker(context);
    }

    private void initializeHandLandmarker(Context context) {
        try {
            BaseOptions baseOptions = BaseOptions.builder()
                    .setModelAssetPath(MODEL_NAME)
                    .build();

            HandLandmarker.HandLandmarkerOptions options =
                    HandLandmarker.HandLandmarkerOptions.builder()
                            .setBaseOptions(baseOptions)
                            .setRunningMode(RunningMode.IMAGE)
                            .setNumHands(2)  // Detectar hasta 2 manos
                            .setMinHandDetectionConfidence(0.5f)
                            .setMinHandPresenceConfidence(0.5f)
                            .setMinTrackingConfidence(0.5f)
                            .build();

            handLandmarker = HandLandmarker.createFromOptions(context, options);
            Log.d(TAG, "HandLandmarker inicializado correctamente");

        } catch (Exception e) {
            String error = "Error al inicializar HandLandmarker: " + e.getMessage();
            Log.e(TAG, error, e);
            if (listener != null) {
                listener.onError(error);
            }
        }
    }

    public void detectHands(Bitmap bitmap) {
        if (handLandmarker == null) {
            Log.e(TAG, "HandLandmarker no inicializado");
            return;
        }

        if (bitmap == null) {
            Log.e(TAG, "Bitmap es null");
            return;
        }

        Log.d(TAG, "Procesando imagen: " + bitmap.getWidth() + "x" + bitmap.getHeight());

        try {
            MPImage mpImage = new BitmapImageBuilder(bitmap).build();
            HandLandmarkerResult result = handLandmarker.detect(mpImage);

            Log.d(TAG, "Manos detectadas en resultado: " + result.landmarks().size());

            if (listener != null) {
                listener.onHandsDetected(result);
            }

        } catch (Exception e) {
            String error = "Error al detectar manos: " + e.getMessage();
            Log.e(TAG, error, e);
            if (listener != null) {
                listener.onError(error);
            }
        }
    }

    public void close() {
        if (handLandmarker != null) {
            handLandmarker.close();
            handLandmarker = null;
        }
    }
}