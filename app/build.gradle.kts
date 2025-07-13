plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
}

android {
    namespace = "com.ilya.rickandmorty"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ilya.rickandmorty"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    // Room
    implementation ("androidx.room:room-runtime:2.7.2")
    kapt ("androidx.room:room-compiler:2.7.2")

    // Опционально: поддержка Kotlin Coroutines
    implementation ("androidx.room:room-ktx:2.7.2")

    // Gson для сериализации (если используешь)
    implementation ("com.google.code.gson:gson:2.13.1")

    // Jetpack Compose
    implementation ("androidx.activity:activity-compose:1.8.2")
    implementation ("androidx.compose.ui:ui:1.6.1")
    implementation ("androidx.compose.material3:material3:1.3.2")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.6.1")
    debugImplementation ("androidx.compose.ui:ui-tooling:1.6.1")
    implementation ("androidx.compose.foundation:foundation:1.6.1")
    implementation ("androidx.compose.material:material-icons-extended:1.6.1")
    implementation ("androidx.compose.runtime:runtime-livedata:1.6.1")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.36.0")


    // ViewModel и LiveData
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Навигация
    implementation ("androidx.navigation:navigation-compose:2.7.7")

    // Сетевое взаимодействие
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation( "com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Загрузка изображений
    implementation( "io.coil-kt:coil-compose:2.5.0")



    // Пагинация (опционально, для улучшенной реализации)
    implementation ("androidx.paging:paging-compose:3.3.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(project(":compose-utils"))

}