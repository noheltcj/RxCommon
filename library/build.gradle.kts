plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
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

kotlin {
    val androidTarget = android {
        publishLibraryVariants("release", "debug")
    }
    val jvmTarget = jvm {
        mavenPublication {
            // TODO: Include sources
        }
    }
    val jsTarget = js {
        browser()
        nodejs()
    }
    val iosX64Target = iosX64()
    val iosArm64Target = iosArm64()
    val iosArm32Target = iosArm32()

    configure(
        listOf(
            metadata(),
            androidTarget,
            jvmTarget,
            jsTarget,
            iosX64Target,
            iosArm32Target,
            iosArm64Target
        )
    ) {
        mavenPublication {
            artifactId = "rxcommon${this.artifactId.substring(7)}"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val nativeMain by creating {
            dependsOn(commonMain)
        }

        val nativeTest by creating {
            dependsOn(commonTest)
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

        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

        val androidMain by getting {
            dependsOn(jvmMain)
        }

        val androidTest by getting {
            dependsOn(jvmTest)
        }

        val iosArm32Main by getting {
            dependsOn(nativeMain)
        }

        val iosArm32Test by getting {
            dependsOn(nativeTest)
        }

        val iosArm64Main by getting {
            dependsOn(nativeMain)
        }

        val iosArm64Test by getting {
            dependsOn(nativeTest)
        }

        val iosX64Main by getting {
            dependsOn(nativeMain)
        }

        val iosX64Test by getting {
            dependsOn(nativeTest)
        }
    }
}

publishing {
    publications.withType<MavenPublication>().apply {
        val kotlinMultiplatform by getting {
            artifactId = "rxcommon"
        }
    }
}
