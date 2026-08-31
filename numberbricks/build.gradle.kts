import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(plugin.plugins.android.library)
    alias(plugin.plugins.kotlin.android)
    alias(plugin.plugins.kotlin.compose)
    alias(plugin.plugins.ktfmt)
}

android {
    namespace = "io.github.beankitk.numberbricks"
    compileSdk = build.versions.sdk.compile.get().toInt()

    defaultConfig { minSdk = build.versions.sdk.min.get().toInt() }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(build.versions.java.source.get())
        targetCompatibility = JavaVersion.toVersion(build.versions.java.target.get())
    }

    buildFeatures { compose = true }

    kotlin { compilerOptions { jvmTarget.set(JvmTarget.JVM_21) } }

    composeCompiler {
        reportsDestination.set(layout.buildDirectory.dir("compose_metrics"))
        metricsDestination.set(layout.buildDirectory.dir("compose_metrics"))
    }

    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.collection)
    implementation(libs.compose.animation.core)
    implementation(libs.compose.foundation)
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.kotlinx.coroutines.android)
}

ktfmt { kotlinLangStyle() }
