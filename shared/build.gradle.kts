plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.atomic.map.shared"
    compileSdk = 34
    buildFeatures { aidl = true }
}