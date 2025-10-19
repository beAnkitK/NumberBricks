plugins {
    alias(plugin.plugins.android.library)
    alias(plugin.plugins.kotlin.android)
    alias(plugin.plugins.kotlin.compose)
}

android {
    namespace = "io.github.beankitk.numberbricks"
    compileSdk = build.versions.sdk.compile.get().toInt()

    defaultConfig {
        minSdk = build.versions.sdk.min.get().toInt()
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(build.versions.java.source.get())
        targetCompatibility = JavaVersion.toVersion(build.versions.java.target.get())
    }
    
    buildFeatures {
        compose = true
    }
    
    kotlinOptions {
        jvmTarget = build.versions.java.jvmTarget.get()
        freeCompilerArgs += listOf(
            "-P", "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.buildDir.absolutePath}/compose_metrics",
            "-P", "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.buildDir.absolutePath}/compose_metrics"
        )
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.animation.core)
    implementation(libs.compose.foundation)
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.kotlinx.coroutines.android)
}
