plugins {
    kotlin("multiplatform")
    id("maven-publish")
}

kotlin {
    jvm()
    js {
        browser()
        nodejs()
    }
    iosX64()
    iosArm64()
    iosArm32()

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

        jvm {
            compilations["main"].defaultSourceSet {
                dependencies {
                    implementation(kotlin("stdlib-jdk8"))
                }
            }
        }

        jvm {
            compilations["test"].defaultSourceSet {
                dependencies {
                    implementation(kotlin("test-junit"))
                }
            }
        }

        js().compilations["main"].defaultSourceSet  {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }

        js().compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

        iosArm32().compilations["main"].defaultSourceSet {
            dependsOn(nativeMain)
        }

        iosArm32().compilations["test"].defaultSourceSet {
            dependsOn(nativeTest)
        }

        iosArm64().compilations["main"].defaultSourceSet {
            dependsOn(nativeMain)
        }

        iosArm64().compilations["test"].defaultSourceSet {
            dependsOn(nativeTest)
        }

        iosX64().compilations["main"].defaultSourceSet {
            dependsOn(nativeMain)
        }

        iosX64().compilations["test"].defaultSourceSet {
            dependsOn(nativeTest)
        }
    }
}
