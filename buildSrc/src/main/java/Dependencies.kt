object Dependencies {
    object Project {
        const val rxcommonBinding = "com.noheltcj:rxcommon-binding:${Versions.rxcommon}"
        const val rxcommonCore = "com.noheltcj:rxcommon-core:${Versions.rxcommon}"
    }

    object Classpath {
        const val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val androidTools = "com.android.tools.build:gradle:${Versions.Android.gradleTools}"
        const val dokkaPlugin = "org.jetbrains.dokka:dokka-gradle-plugin:${Versions.dokka}"
    }

    object Android {
        const val liveData = "androidx.lifecycle:lifecycle-livedata:${Versions.Android.architectureComponents}"
    }
}