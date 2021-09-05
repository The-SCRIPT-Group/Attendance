buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://storage.googleapis.com/r8-releases/raw")
    }

    dependencies {
        // Refer https://issuetracker.google.com/issues/194915678#comment14
        classpath("com.android.tools:r8:${Dependencies.r8_version}")
        classpath("com.android.tools.build:gradle:${Dependencies.gradle_version}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Dependencies.kotlin_version}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${Dependencies.kotlin_version}")
        classpath("org.jetbrains.kotlin:kotlin-android-extensions:${Dependencies.kotlin_version}")
        classpath("com.google.gms:google-services:${Dependencies.gms_version}")
        classpath("com.google.firebase:firebase-crashlytics-gradle:${Dependencies.firebase_version}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Dependencies.hilt_version}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
