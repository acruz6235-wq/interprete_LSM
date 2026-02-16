package com.example.interpretels.ml;

import android.util.Log;
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import java.util.List;

public class SignClassifier {

    private static final String TAG = "SignClassifier";

    // Índices de landmarks importantes
    private static final int WRIST = 0;
    private static final int THUMB_TIP = 4;
    private static final int THUMB_IP = 3;
    private static final int THUMB_MCP = 2;
    private static final int INDEX_TIP = 8;
    private static final int INDEX_PIP = 6;
    private static final int INDEX_MCP = 5;
    private static final int MIDDLE_TIP = 12;
    private static final int MIDDLE_PIP = 10;
    private static final int RING_TIP = 16;
    private static final int RING_PIP = 14;
    private static final int PINKY_TIP = 20;
    private static final int PINKY_PIP = 18;

    public String classify(List<NormalizedLandmark> landmarks) {
        if (landmarks == null || landmarks.size() != 21) {
            return "Mano no detectada";
        }

        // Intentar clasificar cada seña
        if (isLetterA(landmarks)) return "A";
        if (isLetterB(landmarks)) return "B";
        if (isLetterC(landmarks)) return "C";
        if (isLetterL(landmarks)) return "L";
        if (isLetterY(landmarks)) return "Y";
        if (isThumbsUp(landmarks)) return "👍 Bien";
        if (isOpenHand(landmarks)) return "✋ Mano abierta";

        return "Seña no reconocida";
    }

    // LETRA A: Puño cerrado con pulgar al lado
    private boolean isLetterA(List<NormalizedLandmark> landmarks) {
        // Pulgar extendido pero al lado
        boolean thumbSide = landmarks.get(THUMB_TIP).x() > landmarks.get(THUMB_MCP).x() - 0.05;

        // Todos los dedos doblados (puntas abajo de las articulaciones)
        boolean indexClosed = landmarks.get(INDEX_TIP).y() > landmarks.get(INDEX_PIP).y();
        boolean middleClosed = landmarks.get(MIDDLE_TIP).y() > landmarks.get(MIDDLE_PIP).y();
        boolean ringClosed = landmarks.get(RING_TIP).y() > landmarks.get(RING_PIP).y();
        boolean pinkyClosed = landmarks.get(PINKY_TIP).y() > landmarks.get(PINKY_PIP).y();

        return thumbSide && indexClosed && middleClosed && ringClosed && pinkyClosed;
    }

    // LETRA B: Mano abierta, dedos juntos, pulgar doblado
    private boolean isLetterB(List<NormalizedLandmark> landmarks) {
        // Dedos extendidos (puntas arriba de articulaciones)
        boolean indexExtended = landmarks.get(INDEX_TIP).y() < landmarks.get(INDEX_PIP).y();
        boolean middleExtended = landmarks.get(MIDDLE_TIP).y() < landmarks.get(MIDDLE_PIP).y();
        boolean ringExtended = landmarks.get(RING_TIP).y() < landmarks.get(RING_PIP).y();
        boolean pinkyExtended = landmarks.get(PINKY_TIP).y() < landmarks.get(PINKY_PIP).y();

        // Pulgar doblado hacia adentro
        boolean thumbBent = landmarks.get(THUMB_TIP).x() < landmarks.get(INDEX_MCP).x();

        // Dedos relativamente juntos
        float fingerSpread = Math.abs(landmarks.get(INDEX_TIP).x() - landmarks.get(PINKY_TIP).x());
        boolean fingersTogether = fingerSpread < 0.15;

        return indexExtended && middleExtended && ringExtended &&
                pinkyExtended && thumbBent && fingersTogether;
    }

    // LETRA C: Forma de C con la mano
    private boolean isLetterC(List<NormalizedLandmark> landmarks) {
        // Dedos curvados pero no completamente cerrados
        boolean indexCurved = landmarks.get(INDEX_TIP).y() < landmarks.get(WRIST).y() &&
                landmarks.get(INDEX_TIP).y() > landmarks.get(INDEX_MCP).y();

        boolean middleCurved = landmarks.get(MIDDLE_TIP).y() < landmarks.get(WRIST).y() &&
                landmarks.get(MIDDLE_TIP).y() > landmarks.get(INDEX_MCP).y();

        // Pulgar separado formando la C
        float thumbDistance = Math.abs(landmarks.get(THUMB_TIP).x() - landmarks.get(INDEX_TIP).x());
        boolean thumbSeparated = thumbDistance > 0.1;

        return indexCurved && middleCurved && thumbSeparated;
    }

    // LETRA L: Índice y pulgar extendidos formando L
    private boolean isLetterL(List<NormalizedLandmark> landmarks) {
        // Índice extendido verticalmente
        boolean indexExtended = landmarks.get(INDEX_TIP).y() < landmarks.get(INDEX_PIP).y() - 0.05;

        // Pulgar extendido horizontalmente
        boolean thumbExtended = Math.abs(landmarks.get(THUMB_TIP).x() - landmarks.get(THUMB_MCP).x()) > 0.08;

        // Otros dedos doblados
        boolean middleClosed = landmarks.get(MIDDLE_TIP).y() > landmarks.get(MIDDLE_PIP).y();
        boolean ringClosed = landmarks.get(RING_TIP).y() > landmarks.get(RING_PIP).y();
        boolean pinkyClosed = landmarks.get(PINKY_TIP).y() > landmarks.get(PINKY_PIP).y();

        return indexExtended && thumbExtended && middleClosed && ringClosed && pinkyClosed;
    }

    // LETRA Y: Pulgar y meñique extendidos
    private boolean isLetterY(List<NormalizedLandmark> landmarks) {
        // Pulgar extendido
        boolean thumbExtended = Math.abs(landmarks.get(THUMB_TIP).x() - landmarks.get(WRIST).x()) > 0.15;

        // Meñique extendido
        boolean pinkyExtended = landmarks.get(PINKY_TIP).y() < landmarks.get(PINKY_PIP).y();

        // Dedos del medio doblados
        boolean indexClosed = landmarks.get(INDEX_TIP).y() > landmarks.get(INDEX_PIP).y();
        boolean middleClosed = landmarks.get(MIDDLE_TIP).y() > landmarks.get(MIDDLE_PIP).y();
        boolean ringClosed = landmarks.get(RING_TIP).y() > landmarks.get(RING_PIP).y();

        return thumbExtended && pinkyExtended && indexClosed && middleClosed && ringClosed;
    }

    // PULGAR ARRIBA (Bien / OK)
    private boolean isThumbsUp(List<NormalizedLandmark> landmarks) {
        // Pulgar apuntando hacia arriba
        boolean thumbUp = landmarks.get(THUMB_TIP).y() < landmarks.get(THUMB_IP).y() &&
                landmarks.get(THUMB_IP).y() < landmarks.get(THUMB_MCP).y();

        // Todos los demás dedos cerrados
        boolean fingersClosed = landmarks.get(INDEX_TIP).y() > landmarks.get(INDEX_PIP).y() &&
                landmarks.get(MIDDLE_TIP).y() > landmarks.get(MIDDLE_PIP).y() &&
                landmarks.get(RING_TIP).y() > landmarks.get(RING_PIP).y() &&
                landmarks.get(PINKY_TIP).y() > landmarks.get(PINKY_PIP).y();

        return thumbUp && fingersClosed;
    }

    // MANO ABIERTA (Hola / Alto)
    private boolean isOpenHand(List<NormalizedLandmark> landmarks) {
        // Todos los dedos extendidos
        boolean allExtended = landmarks.get(THUMB_TIP).y() < landmarks.get(THUMB_MCP).y() &&
                landmarks.get(INDEX_TIP).y() < landmarks.get(INDEX_PIP).y() &&
                landmarks.get(MIDDLE_TIP).y() < landmarks.get(MIDDLE_PIP).y() &&
                landmarks.get(RING_TIP).y() < landmarks.get(RING_PIP).y() &&
                landmarks.get(PINKY_TIP).y() < landmarks.get(PINKY_PIP).y();

        // Dedos separados
        float spread = Math.abs(landmarks.get(THUMB_TIP).x() - landmarks.get(PINKY_TIP).x());
        boolean fingersSpread = spread > 0.2;

        return allExtended && fingersSpread;
    }

    // Método auxiliar: Calcular distancia entre dos puntos
    private float distance(NormalizedLandmark p1, NormalizedLandmark p2) {
        float dx = p1.x() - p2.x();
        float dy = p1.y() - p2.y();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}