import com.gta.buildsrc.Configuration

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.google.gms.google-services")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("de.mannodermaus.android-junit5")
    id("jacoco")
}

android {
    namespace = "com.gta.data"
    compileSdk = Configuration.compileSdk

    defaultConfig {
        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(project(":domain"))

    implementation(platform(Dependencies.Libraries.Firebase.PLATFORM))

    implementation(Dependencies.Libraries.dataLibraries)
    kapt(Dependencies.Libraries.dataKaptLibraries)

    testImplementation(Dependencies.Libraries.dataTestLibraries)
}

tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

tasks.register<JacocoReport>(name = "coverageReport") {
    dependsOn("testDebugUnitTest")
    group = "Reporting"
    description = "Generate Jacoco coverage reports"

    val excludes = listOf(
        "**/BuildConfig.*",
        "**/*Factory*",
        "**/*Module*",
        "**/*Component*"
    )

    val kotlinDirs = fileTree(
        "${project.buildDir}/intermediates/javac/debug/classes"
    ) { exclude(excludes) }

    classDirectories.setFrom(kotlinDirs)
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(files("${project.buildDir}/jacoco/testDebugUnitTest.exec"))

    reports {
        html.required.set(true)
        html.outputLocation.set(file("$buildDir/jacocoHtml"))
    }
}

