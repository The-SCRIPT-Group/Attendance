import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlinx-serialization")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = 31

    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val appName = parent?.name?.replace(' ', '-')
                val outputFileName = "${appName}-${output.baseName}-${variant.versionName}.apk"
                output.outputFileName = outputFileName
            }
    }

    defaultConfig {
        applicationId = "in.thescriptgroup.attendance"
        minSdk = 22
        targetSdk = 31
        versionCode = 6
        versionName = "v1.3"
        multiDexEnabled = true
    }

    val keystoreConfigFile = rootProject.layout.projectDirectory.file("key.properties")
    if (keystoreConfigFile.asFile.exists()) {
        val contents = providers.fileContents(keystoreConfigFile).asText.forUseAtConfigurationTime()
        val keystoreProperties = Properties()
        keystoreProperties.load(contents.get().byteInputStream())
        signingConfigs {
            register("release") {
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = rootProject.file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
            }
        }
        buildTypes.all { signingConfig = signingConfigs.getByName("release") }
    }

    buildTypes {
        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Dependencies.kotlin_version}")
    implementation("androidx.core:core-ktx:${Dependencies.core_ktx_version}")
    implementation("androidx.appcompat:appcompat:${Dependencies.appcompat_version}")
    implementation("androidx.legacy:legacy-support-v4:${Dependencies.legacy_version}")
    implementation("androidx.preference:preference-ktx:${Dependencies.preference_version}")

    implementation("com.google.android.material:material:${Dependencies.material_version}")
    implementation("androidx.constraintlayout:constraintlayout:${Dependencies.constraint_layout_version}")

    implementation("com.squareup.retrofit2:retrofit:${Dependencies.retrofit_version}")
    implementation("com.squareup.retrofit2:converter-gson:${Dependencies.retrofit_version}")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${Dependencies.kotlinx_version}")
    implementation("com.google.code.gson:gson:${Dependencies.gson_version}")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Dependencies.lifecycle_version}")
    implementation("androidx.lifecycle:lifecycle-common-java8:${Dependencies.lifecycle_version}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Dependencies.lifecycle_version}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Dependencies.lifecycle_version}")

    implementation(platform("com.google.firebase:firebase-bom:${Dependencies.firebase_bom_version}"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
}
