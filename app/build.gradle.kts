plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.interpretels"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.interpretels"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    aaptOptions {
        noCompress ("tflite", "task")
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Room Database
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // RecyclerView y CardView
    implementation(libs.recyclerview)
    implementation(libs.cardview)

    // CameraX (NUEVO)
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)

    // MediaPipe (NUEVO)
    implementation(libs.mediapipe.tasks.vision)

    // TensorFlow Lite (NUEVO)
    //implementation(libs.tensorflow.lite)
    //implementation(libs.tensorflow.lite.support)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}