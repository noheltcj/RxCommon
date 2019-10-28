buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath(Dependencies.kotlinGradlePlugin)
        classpath(Dependencies.Android.gradleTools)
    }
}

subprojects {
    group = "com.noheltcj"
    version = Versions.rxcommon

    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}
