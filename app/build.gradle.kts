plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

android {
    namespace = "de.zimt.eimerliste"
    compileSdk = 37

    defaultConfig {
        applicationId = "de.zimt.eimerliste"
        minSdk = 26
        targetSdk = 36
        versionCode = 32
        versionName = "0.3.3-dev19"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            // Install alongside the original app without touching its database.
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-test"
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

ksp {
    arg("room.schemaLocation", file("schemas").absolutePath)
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2026.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.1")
    implementation("androidx.room:room-runtime:2.8.5")
    implementation("androidx.room:room-ktx:2.8.5")
    ksp("androidx.room:room-compiler:2.8.5")
    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
}

 
// Scope-freeze guard: fail builds if any of the six original user PNG layers go missing.
// This validates source assets without altering their bytes or introducing new designs.
val verifyOriginalBucketPngs = tasks.register("verifyOriginalBucketPngs") {
    val assets = listOf(
        "flechtkorb_hinten.png", "flechtkorb_vorne.png",
        "holzkuebel_hinten.png", "holzkuebel_vorne.png",
        "bucket_zinc_user_rear.png", "bucket_zinc_user_front.png"
    )
    val assetDir = layout.projectDirectory.dir("src/main/res/drawable-nodpi")
    inputs.files(assets.map { assetDir.file(it) })
    doLast {
        val pngSignature = byteArrayOf(137.toByte(), 80, 78, 71, 13, 10, 26, 10)
        assets.forEach { name ->
            val file = assetDir.file(name).asFile
            check(file.isFile) { "Required original bucket layer missing: $name" }
            check(file.length() >= 24) { "Invalid or truncated PNG: $name" }
            val header = file.inputStream().use { it.readNBytes(24) }
            check(header.copyOfRange(0, 8).contentEquals(pngSignature)) {
                "Not a PNG file: $name"
            }
            check(String(header, 12, 4, Charsets.US_ASCII) == "IHDR") {
                "Missing PNG IHDR: $name"
            }
            val width = java.nio.ByteBuffer.wrap(header, 16, 4).int
            val height = java.nio.ByteBuffer.wrap(header, 20, 4).int
            check(width > 0 && height > 0) { "Invalid PNG dimensions: $name" }
        }
    }
}
tasks.matching { it.name == "preBuild" }.configureEach {
    dependsOn(verifyOriginalBucketPngs)
}
