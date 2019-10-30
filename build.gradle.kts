buildscript {
    repositories {
        jcenter()
        google()
    }

    dependencies {
        classpath(Dependencies.Classpath.kotlinGradle)
        classpath(Dependencies.Classpath.androidTools)
        classpath(Dependencies.Classpath.dokkaPlugin)
    }
}

subprojects {
    group = "com.noheltcj"
    version = Versions.rxcommon

    repositories {
        jcenter()
        google()
    }
}
