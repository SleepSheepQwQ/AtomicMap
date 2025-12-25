buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // 插件版本与你 libs.versions.toml 或当前环境对齐
        classpath("com.android.tools.build:gradle:8.2.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
