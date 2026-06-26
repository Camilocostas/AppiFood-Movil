    import org.gradle.kotlin.dsl.implementation

    plugins {
        alias(libs.plugins.android.application)
        alias(libs.plugins.kotlin.android)
        alias(libs.plugins.kotlin.compose)
        alias(libs.plugins.hilt)
        alias(libs.plugins.ksp)
        id("com.google.gms.google-services")
    }

    android {
        namespace = "com.example.appifood_movil"
        compileSdk = 35

        defaultConfig {
            applicationId = "com.example.appifood_movil"
            minSdk = 24
            targetSdk = 35
            versionCode = 1
            versionName = "1.0"

            // Manifest placeholder for Maps API key. Provide MAPS_API_KEY in local.properties or as a CI secret.
            manifestPlaceholders["com.google.android.geo.API_KEY"] = project.findProperty("MAPS_API_KEY") ?: ""

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
        // Google Maps & Location
        implementation(platform("com.google.firebase:firebase-bom:33.5.0"))
        implementation("com.google.firebase:firebase-auth")
        implementation("com.google.android.gms:play-services-auth:21.2.0")
        implementation("com.google.android.gms:play-services-location:21.3.0")
        implementation("com.google.maps.android:maps-compose:4.3.0")
        implementation("com.google.android.gms:play-services-maps:19.0.0")
        // Generación de códigos QR
        implementation("com.google.zxing:core:3.5.3")

        // ⚠️ SOLO UN BOM - Este es el principal
        implementation(platform(libs.androidx.compose.bom))

        // Compose & UI (usando libs del TOML)
        // Logging interceptor para OkHttp — necesario para HttpLoggingInterceptor
        implementation("com.google.firebase:firebase-messaging-ktx")

        // ✅ Para notificaciones locales
        implementation("androidx.core:core-ktx:1.12.0")
        implementation("com.google.firebase:firebase-storage-ktx")
        implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
        implementation("androidx.security:security-crypto:1.1.0-alpha06")
        implementation("com.airbnb.android:lottie-compose:6.4.0")
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(libs.androidx.compose.ui)
        implementation(libs.androidx.compose.ui.graphics)
        implementation(libs.androidx.compose.ui.tooling.preview)
        implementation(libs.androidx.compose.material3)
        implementation(libs.androidx.compose.foundation)
        implementation(libs.androidx.compose.animation.core)

        // Google Play Services Auth
        implementation("com.google.android.gms:play-services-auth:21.2.0")

        // Iconos extendidos (no está en el TOML, lo pongo manual)
        implementation("androidx.compose.material:material-icons-extended")

        // Splash Screen
        implementation("androidx.core:core-splashscreen:1.0.1")

        // Navigation
        implementation("androidx.navigation:navigation-compose:2.8.0")
        implementation(libs.androidx.hilt.navigation.compose)

        // Hilt
        implementation(libs.hilt.android)
        ksp(libs.hilt.compiler)

        // Retrofit
        implementation(libs.retrofit)
        implementation(libs.retrofit.gson)

        // Coil (Image Loading)
        implementation(libs.coil.compose)

        implementation("com.google.firebase:firebase-firestore-ktx")

        // Testing
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(platform(libs.androidx.compose.bom))
        androidTestImplementation(libs.androidx.compose.ui.test.junit4)
        debugImplementation(libs.androidx.compose.ui.tooling)
        debugImplementation(libs.androidx.compose.ui.test.manifest)
    }
