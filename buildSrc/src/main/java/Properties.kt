import java.io.File

object Properties {
    private val properties = java.util.Properties()

    init {
        val home = System.getenv("HOME")
        properties.apply {
            loadPropertiesIfExists("local.properties")
            home?.also {
                loadPropertiesIfExists("$it/.gradle/gradle.properties")
            }
        }
    }

    val ossrhUsername by lazy {
        optionalProperty("ossrhUsername")
    }

    val ossrhPassword by lazy {
        optionalProperty("ossrhPassword")
    }

    val gradleLocalBuildCacheUrl by lazy {
        optionalProperty("GRADLE_LOCAL_BUILD_CACHE_URL")
    }

    private fun optionalProperty(name: String): String? =
        properties[name] as String?
            ?: System.getenv(name)

    private fun java.util.Properties.loadPropertiesIfExists(propertiesFile: String) {
        val localPropertiesFile = File(propertiesFile)
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use {
                load(it)
            }
        }
    }
}
