object Dependencies {
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"

    object Android {
        const val gradleTools = "com.android.tools.build:gradle:${Versions.Android.gradleTools}"
        const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"

        object AndroidX {
            const val core = "androidx.core:core-ktx:${Versions.Android.AndroidX.core}"
            const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.Android.AndroidX.constraintLayout}"
            const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.Android.AndroidX.lifecycle}"
            const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.Android.AndroidX.lifecycle}"
        }
    }
}