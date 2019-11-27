@file:Suppress("UnstableApiUsage")

import Publishing.addRepositories
import Publishing.mutatePublicationPom
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    id("com.android.library")
    id("maven-publish")
    signing
}

android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(18)
        targetSdkVersion(29)
        versionCode = 1
        versionName = Versions.rxcommon
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
        }
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

val dokka by tasks.getting(DokkaTask::class) {
    outputDirectory = "$buildDir/dokka"
    outputFormat = "html"

    multiplatform {}
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    archiveClassifier.set("javadoc")
    from(dokka)
}

kotlin {
    val androidTarget = android {
        publishLibraryVariants("release", "debug")
    }
    val jvmTarget = jvm {
        mavenPublication {
            artifact(dokkaJar)
        }
    }

    configure(
        listOf(
            metadata(),
            androidTarget,
            jvmTarget
        )
    ) {
        mavenPublication {
            artifactId = "rxcommon-binding-livedata${this.artifactId.substring(8)}"

            mutatePublicationPom(projectName = "RxCommon")
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":rxcommon-binding"))
                implementation(project(":rxcommon-core"))
                implementation(kotlin("stdlib-common"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        val androidMain by getting {
            dependsOn(jvmMain)
            dependencies {
                implementation(Dependencies.Android.liveData)
            }
        }

        val androidTest by getting {
            dependsOn(jvmTest)
        }
    }
}

publishing {
    addRepositories(project)
    publications.withType<MavenPublication>().apply {
        val kotlinMultiplatform by getting {
            artifactId = "rxcommon-binding-livedata"

            mutatePublicationPom(projectName = "RxCommon")
        }
    }
}

signing {
    if (Properties.isRelease) {
        sign(publishing.publications)
    }
}
