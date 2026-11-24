import java.io.BufferedInputStream
import java.io.DataInputStream
import java.nio.ByteBuffer
import java.util.zip.CRC32

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
        versionCode = 53
        versionName = "0.3.3-dev40"
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
    testImplementation("org.json:json:20240303")
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")
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
        val dimensions = mutableMapOf<String, Pair<Int, Int>>()
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
            val width = ByteBuffer.wrap(header, 16, 4).int
            val height = ByteBuffer.wrap(header, 20, 4).int
            check(width > 0 && height > 0) { "Invalid PNG dimensions: $name" }
            // Decode each user PNG to detect damaged image data, without rewriting it.
            val decoded = javax.imageio.ImageIO.read(file)
            check(decoded != null && decoded.width == width && decoded.height == height) {
                "PNG pixel data cannot be decoded consistently: $name"
            }
            // Validate every PNG chunk CRC, including IDAT and IEND. ImageIO
            // may decode images even when a chunk checksum is corrupted.
            DataInputStream(BufferedInputStream(file.inputStream())).use { input ->
                val signature = ByteArray(8)
                input.readFully(signature)
                check(signature.contentEquals(pngSignature)) { "Invalid PNG signature: $name" }
                var sawIend = false
                var chunkIndex = 0
                var sawIdat = false
                while (!sawIend) {
                    val chunkLength = input.readInt().toLong() and 0xffffffffL
                    check(chunkLength <= file.length() && chunkLength <= 256L * 1024 * 1024) {
                        "Invalid PNG chunk length: $name"
                    }
                    val chunkType = ByteArray(4)
                    input.readFully(chunkType)
                    val type = String(chunkType, Charsets.US_ASCII)
                    if (chunkIndex == 0) {
                        check(type == "IHDR" && chunkLength == 13L) { "Invalid first PNG IHDR chunk: $name" }
                    } else {
                        check(type != "IHDR") { "Duplicate PNG IHDR chunk: $name" }
                    }
                    if (type == "IDAT") sawIdat = true
                    if (type == "IEND") check(sawIdat) { "PNG has no image data: $name" }
                    chunkIndex++
                    val crc = CRC32()
                    crc.update(chunkType)
                    val buffer = ByteArray(8192)
                    var remaining = chunkLength
                    while (remaining > 0) {
                        val n = minOf(remaining, buffer.size.toLong()).toInt()
                        input.readFully(buffer, 0, n)
                        crc.update(buffer, 0, n)
                        remaining -= n
                    }
                    val expected = input.readInt().toLong() and 0xffffffffL
                    check(crc.value == expected) {
                        "PNG chunk checksum mismatch in $name: ${String(chunkType, Charsets.US_ASCII)}"
                    }
                    if (type == "IEND") {
                        check(chunkLength == 0L) { "Invalid PNG IEND length: $name" }
                        sawIend = true
                    }
                }
                check(input.read() == -1) { "Trailing bytes after PNG IEND: $name" }
            }
            dimensions[name] = width to height
        }
        // Both layers must use the same source pixel coordinate system. Reject
        // mismatched canvases rather than silently stretching one layer.
        listOf(
            "flechtkorb_hinten.png" to "flechtkorb_vorne.png",
            "holzkuebel_hinten.png" to "holzkuebel_vorne.png",
            "bucket_zinc_user_rear.png" to "bucket_zinc_user_front.png"
        ).forEach { (rear, front) ->
            check(dimensions[rear] == dimensions[front]) {
                "Original bucket layers have different canvas dimensions: $rear and $front"
            }
        }
    }
}
tasks.matching { it.name == "preBuild" }.configureEach {
    dependsOn(verifyOriginalBucketPngs)
}
