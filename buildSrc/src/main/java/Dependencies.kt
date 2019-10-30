object Dependencies {
    object Classpath {
        const val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val androidTools = "com.android.tools.build:gradle:${Versions.Android.gradleTools}"
        const val dokkaPlugin = "org.jetbrains.dokka:dokka-gradle-plugin:${Versions.dokka}"
    }

    object Android {
        object AndroidX {
            const val core = "androidx.core:core-ktx:${Versions.Android.AndroidX.core}"
            const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.Android.AndroidX.constraintLayout}"
            const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.Android.AndroidX.lifecycle}"
            const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.Android.AndroidX.lifecycle}"
        }
    }
}