import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.ncorti.ktfmt.gradle") version "0.16.0"
  id("com.google.gms.google-services")
  id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
  id("jacoco")
  id("org.sonarqube") version "4.4.1.3373"
  id("kotlin-kapt")
  id("dagger.hilt.android.plugin")
}

android {
  namespace = "com.github.se.eventradar"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.github.se.eventradar"
    minSdk = 29
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables { useSupportLibrary = true }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
    debug {
      enableUnitTestCoverage = true
      enableAndroidTestCoverage = true
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions { jvmTarget = "1.8" }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  composeOptions { kotlinCompilerExtensionVersion = "1.5.1" }
  packaging {
    resources {
      merges += "META-INF/LICENSE.md"
      merges += "META-INF/LICENSE-notice.md"
    }
  }

  testOptions {
    unitTests.isReturnDefaultValues = true
    packagingOptions { jniLibs { useLegacyPackaging = true } }
  }
}
val activityComposeVersion = "1.9.0"
val androidXCameraVersion = "1.3.3"
val androidXEmulatorVersion = "2.3.0-alpha04"
dependencies {
  implementation("androidx.core:core-ktx:1.7.0")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
  implementation("androidx.activity:activity-compose:1.8.2")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.compose.material:material:1.1.1")
  implementation("androidx.compose.material3:material3:1.1.2")
  implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

  // Navigation
  implementation("androidx.navigation:navigation-compose:2.6.0-rc01")
  implementation("androidx.navigation:navigation-ui-ktx:2.7.5")

  // Google Maps
  implementation("com.google.maps.android:maps-compose:4.3.3")
  implementation("com.google.maps.android:maps-compose-utils:4.3.3")
  implementation("com.google.android.gms:play-services-maps:18.1.0")
  implementation("com.google.android.gms:play-services-auth:20.6.0")

  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")

  // Jetpack Compose
  implementation(platform("androidx.compose:compose-bom:2024.02.02"))
  implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
  implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
  androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.02"))
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-tooling-preview")
  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")

  // Firebase BoM
  implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
  implementation("com.google.firebase:firebase-auth-ktx")
  implementation("com.firebaseui:firebase-ui-auth:7.2.0")
  implementation("com.google.firebase:firebase-database-ktx")
  implementation("com.google.firebase:firebase-firestore")
  implementation("com.google.android.play:core-ktx:1.8.1")
  implementation("com.google.firebase:firebase-database-ktx")

  // Coil
  implementation("io.coil-kt:coil-compose:2.6.0")

  // Dagger Hilt
  implementation("com.google.dagger:hilt-android:${rootProject.extra.get("hiltVersion")}")
  kapt("com.google.dagger:hilt-android-compiler:${rootProject.extra.get("hiltVersion")}")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1-Beta")
  kaptTest("com.google.dagger:hilt-android-compiler:2.44")
  testImplementation("com.google.dagger:hilt-android-testing:2.44")
  kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.44")
  androidTestImplementation("com.google.dagger:hilt-android-testing:2.44")
  implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
  // JUnit
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")

  testImplementation("androidx.arch.core:core-testing:2.2.0")

  testImplementation("org.json:json:20220924")
    
    // Mockk & Espresso
    testImplementation("io.mockk:mockk:1.13.10")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("com.kaspersky.android-components:kaspresso:1.4.3")
    androidTestImplementation("com.kaspersky.android-components:kaspresso-compose-support:1.4.1")
    androidTestImplementation("io.mockk:mockk:1.13.10")
    androidTestImplementation("io.mockk:mockk-android:1.13.10")
    androidTestImplementation("io.mockk:mockk-agent:1.13.10")
    androidTestImplementation ("androidx.test:runner:1.5.2")
    androidTestImplementation ("androidx.test:rules:1.5.0")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation ("androidx.test.espresso:espresso-intents:3.5.1")

  // Robolectric
  testImplementation("org.robolectric:robolectric:4.11.1")

  // OKHttp3 BOM
  implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
  implementation("com.squareup.okhttp3:okhttp")
  implementation("com.squareup.okhttp3:logging-interceptor")

    //QR CODE (Zxing)
    implementation ("com.google.zxing:core:3.4.1")

    // CameraX
    implementation ("androidx.camera:camera-camera2:$androidXCameraVersion")
    implementation ("androidx.camera:camera-lifecycle:$androidXCameraVersion")
    implementation ("androidx.camera:camera-view:$androidXCameraVersion")

    // Android Test
    androidTestImplementation ("androidx.test.uiautomator:uiautomator:$androidXEmulatorVersion")

}

secrets {
  // Optionally specify a different file name containing your secrets.
  // The plugin defaults to "local.properties"
  propertiesFileName = "secrets.properties"

  // A properties file containing default secret values. This file can be
  // checked in version control.
  defaultPropertiesFileName = "local.defaults.properties"

  // Configure which keys should be ignored by the plugin by providing regular expressions.
  // "sdk.dir" is ignored by default.
  ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
  ignoreList.add("sdk.*") // Ignore all keys matching the regexp "sdk.*"
}

tasks.register("jacocoTestReport", JacocoReport::class) {
  mustRunAfter("testDebugUnitTest", "connectedDebugAndroidTest")

  reports {
    xml.required = true
    html.required = true
  }

  val fileFilter =
      listOf(
          "**/R.class",
          "**/R$*.class",
          "**/BuildConfig.*",
          "**/Manifest*.*",
          "**/*Test*.*",
          "android/**/*.*",
          "**/SignatureChecks.*",
      )
  val debugTree = fileTree("${project.buildDir}/tmp/kotlin-classes/debug") { exclude(fileFilter) }
  val mainSrc = "${project.projectDir}/src/main/java"

  sourceDirectories.setFrom(files(mainSrc))
  classDirectories.setFrom(files(debugTree))
  executionData.setFrom(
      fileTree(project.buildDir) {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        include("outputs/code_coverage/debugAndroidTest/connected/*/coverage.ec")
      })
}

// Avoid redundant tests, debug is sufficient
tasks.withType<Test> { onlyIf { !name.toLowerCaseAsciiOnly().contains("release") } }

sonar {
  properties {
    property("sonar.projectKey", "swent-sp-2024-event-radar_event-radar-app")
    property("sonar.organization", "swent-sp-2024-party-radar")
    property("sonar.host.url", "https://sonarcloud.io")
    property(
        "sonar.coverage.jacoco.xmlReportPaths",
        "build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
  }
}

kapt { correctErrorTypes = true }
