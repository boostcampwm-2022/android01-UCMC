// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(Dependencies.Classpaths.GOOGLE_SERVICES)
        classpath(Dependencies.Classpaths.NAVIGATION)
        classpath(Dependencies.Classpaths.HILT)
        classpath(Dependencies.Classpaths.CRASHLYTICS)
        classpath(Dependencies.Classpaths.JUNIT5)
    }
}

plugins {
    id("com.android.application") version Dependencies.Versions.ANDROID apply false
    id("com.android.library") version Dependencies.Versions.ANDROID apply false
    id("org.jetbrains.kotlin.android") version Dependencies.Versions.KOTLIN apply false
    id("org.jetbrains.kotlin.jvm") version Dependencies.Versions.KOTLIN apply false
}

subprojects {
    val ktlint by configurations.creating

    dependencies {
        ktlint("com.pinterest:ktlint:0.47.1")
    }

    tasks.register<JavaExec>("ktlint") {
        group = "verification"
        description = "Check Kotlin code style."
        mainClass.set("com.pinterest.ktlint.Main")
        classpath = ktlint
        args("--android", "src/**/*.kt")
    }

    tasks.register<JavaExec>("ktlintFormat") {
        group = "formatting"
        description = "Fix Kotlin code style deviations."
        mainClass.set("com.pinterest.ktlint.Main")
        classpath = ktlint
        args("--android", "-F", "src/**/*.kt")
    }
}
