// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    ext {
        androidCoreVersion = "1.9.0"
        kotlinVersion = "1.4.30"
        lifecycleVersion = "2.5.1"
        navigationVersion = "2.5.3"
        hiltVersion = "2.44"
        coroutineVersion = "1.6.4"
        roomVersion = "2.4.3"
        timberVersion = "5.0.1"
        glideVersion = "4.14.2"
        viewPagerVersion = "1.0.0"
        naverMapsVersion = "3.16.0"
        streamChatVersion = "5.11.0"
        locationPermissionVersion = "20.0.0"
    }

    dependencies {
        classpath 'com.google.gms:google-services:4.3.14'
        classpath "androidx.navigation:navigation-safe-args-gradle-plugin:$navigationVersion"
        classpath "com.google.dagger:hilt-android-gradle-plugin:$hiltVersion"
        classpath 'com.google.firebase:firebase-crashlytics-gradle:2.9.2'
    }
}

plugins {
    id 'com.android.application' version '7.3.0' apply false
    id 'com.android.library' version '7.3.0' apply false
    id 'org.jetbrains.kotlin.android' version '1.7.10' apply false
    id 'org.jetbrains.kotlin.jvm' version '1.7.10' apply false
}

subprojects {
    configurations {
        ktlint
    }

    dependencies {
        ktlint 'com.pinterest:ktlint:0.47.1'
    }

    task ktlint(type: JavaExec, group: "verification") {
        description = "Check Kotlin code style."
        mainClass.set("com.pinterest.ktlint.Main")
        classpath = configurations.ktlint
        args "src/**/*.kt"
    }

    task ktlintFormat(type: JavaExec, group: "formatting") {
        description = "Fix Kotlin code style deviations."
        mainClass.set("com.pinterest.ktlint.Main")
        classpath = configurations.ktlint
        args "-F", "src/**/*.kt"
    }
}
