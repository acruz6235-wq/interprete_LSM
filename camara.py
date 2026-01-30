import cv2
import mediapipe as mp

# Inicializar MediaPipe
mp_hands = mp.solutions.hands
mp_draw = mp.solutions.drawing_utils

# Configurar detector de manos
hands = mp_hands.Hands(
    static_image_mode=False,
    max_num_hands=1,
    min_detection_confidence=0.7,
    min_tracking_confidence=0.7
)

# Inicializar cámara
cap = cv2.VideoCapture(0)

# Verificar que la cámara se abrió correctamente
if not cap.isOpened():
    print("Error: No se pudo acceder a la cámara")
    exit()

try:
    while True:
        ret, frame = cap.read()
        if not ret:
            print("Error al capturar frame")
            break

        # Convertir BGR a RGB
        frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        
        # Procesar detección
        results = hands.process(frame_rgb)

        # Dibujar landmarks si se detecta una mano
        if results.multi_hand_landmarks:
            for hand_landmarks in results.multi_hand_landmarks:
                mp_draw.draw_landmarks(
                    frame, 
                    hand_landmarks, 
                    mp_hands.HAND_CONNECTIONS,
                    mp_draw.DrawingSpec(color=(0, 255, 0), thickness=2),
                    mp_draw.DrawingSpec(color=(255, 0, 0), thickness=2)
                )

        # Mostrar frame
        cv2.imshow("Detección de Mano - ESC para salir", frame)

        # Salir con ESC
        if cv2.waitKey(1) & 0xFF == 27:
            break

except KeyboardInterrupt:
    print("\nInterrumpido por el usuario")

finally:
    # Liberar recursos
    cap.release()
    cv2.destroyAllWindows()
    hands.close()