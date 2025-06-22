//plugins {
//    alias(libs.plugins.android.application)
//}
//
//android {
//    namespace = "com.example.habittrackerapp"
//    compileSdk = 35
//
//    defaultConfig {
//        applicationId = "com.example.habittrackerapp"
//        minSdk = 24
//        targetSdk = 35
//        versionCode = 1
//        versionName = "1.0"
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//    }
//
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }
//}
//
//dependencies {
//
//    implementation(libs.appcompat)
//    implementation(libs.material)
//    implementation(libs.activity)
//    implementation(libs.constraintlayout)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.ext.junit)
//    androidTestImplementation(libs.espresso.core)
//
//    // Room Database
//
//
//
//}


plugins {
    alias(libs.plugins.android.application)



    id("kotlin-android")           // Ensure this is also included
    id("kotlin-kapt")

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.habittrackerapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.habittrackerapp"
        minSdk = 24
        targetSdk = 35
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

    /*defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = mutableMapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }
    }*/
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation ("com.google.firebase:firebase-storage:20.0.0")
    implementation ("com.google.firebase:firebase-auth:21.0.1")

    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-analytics")


    implementation("com.github.bumptech.glide:glide:4.13.2")  // Add Glide dependency
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1") // Use kapt for Room annotation processor

    // For Kotlin, make sure you use KAPT for annotation processing
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
kapt {
    correctErrorTypes = true  // Ensure Glide's annotation processing works correctly
}