import org.gradle.api.Project

object Properties {
    val isRelease by lazy {
        System.getenv("release") == "true"
    }

    fun Project.requiredForReleaseProperty(name: String) =
        properties[name] as String?
            ?: "".takeIf { !isRelease }
            ?: throw RuntimeException("Missing property $name")
}