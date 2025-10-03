import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

val keystoreProps = gradleLocalProperties(rootDir, providers)

plugins {
    alias(plugin.plugins.android.application)
    alias(plugin.plugins.kotlin.android)
    alias(plugin.plugins.kotlin.compose)
    alias(plugin.plugins.kotlin.serialization)
}

android {
    namespace = "io.github.beankitk.numberbricks.sample"
    compileSdk = build.versions.sdk.compile.get().toInt()
    
    defaultConfig {
        applicationId = "io.github.beankitk.numberbricks.sample"
        minSdk = build.versions.sdk.min.get().toInt()
        targetSdk = build.versions.sdk.target.get().toInt()
        versionCode = build.versions.version.code.get().toInt()
        versionName = build.versions.version.name.get()
    }
    
    signingConfigs {
        create("release") {
            storeFile = file(keystoreProps["storeFile"] as String)
            storePassword = keystoreProps["storePassword"] as String
            keyAlias = keystoreProps["keyAlias"] as String
            keyPassword = keystoreProps["keyPassword"] as String
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(build.versions.java.source.get())
        targetCompatibility = JavaVersion.toVersion(build.versions.java.target.get())
    }
    
    kotlinOptions {
       jvmTarget = build.versions.java.jvmTarget.get()
       freeCompilerArgs += listOf(
           "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
       )
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
        
        getByName("debug") {
            isDebuggable = true
        }
    }

    buildFeatures {
    	buildConfig = true
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


dependencies {  
    implementation(platform(libs.androidx.compose.bom))
    implementation(project(":numberbricks"))
    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.bundles.compose.core)
    
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation)
	implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.core)
}