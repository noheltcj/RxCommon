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

allprojects {
    group = "com.noheltcj"
    version = Versions.rxcommon

    repositories {
        jcenter()
        google()
        mavenLocal()
    }
}
