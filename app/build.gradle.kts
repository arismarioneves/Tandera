import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

/**
 * Chave que identifica o app na API (cabeçalho X-Tandera-Key; APP_API_KEY no .env do TanderaWEB).
 * Fica fora do Git: local.properties (tandera.apiKey=...) ou -Ptandera.apiKey=... no CI.
 */
val apiKey: String = providers.gradleProperty("tandera.apiKey").orNull
    ?: rootProject.file("local.properties").takeIf { it.exists() }
        ?.let { file -> Properties().apply { file.inputStream().use { load(it) } }.getProperty("tandera.apiKey") }
    ?: ""

android {
    namespace = "com.mari05lim.tandera"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.mari05lim.tandera"
        minSdk = 26
        targetSdk = 37
        versionCode = 14
        versionName = "3.0"

        buildConfigField("String", "API_URL", "\"${providers.gradleProperty("tandera.apiUrl").get()}\"")
        buildConfigField("String", "SITE_URL", "\"${providers.gradleProperty("tandera.siteUrl").get()}\"")
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
    }

    buildTypes {
        // Instala ao lado da versão da Play Store (assinaturas diferentes não se sobrepõem).
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// Sem a chave o app de loja cairia nos limites do site: falha o build de release em vez de publicar assim.
tasks.matching { it.name == "preReleaseBuild" }.configureEach {
    val missingKey = apiKey.isBlank()
    doFirst {
        if (missingKey) throw GradleException("Defina tandera.apiKey no local.properties (mesmo valor de APP_API_KEY da API).")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    testImplementation(libs.junit)
}
