import org.gradle.caching.http.HttpBuildCache
import org.gradle.kotlin.dsl.KotlinSettingsScript
import java.net.URI

fun KotlinSettingsScript.setupBuildCache() {
    buildCache {
        val localBuildCacheUrl = Properties.gradleLocalBuildCacheUrl
        if (!localBuildCacheUrl.isNullOrEmpty()) {
            println("Using local build cache located at: $localBuildCacheUrl")
            local(HttpBuildCache::class.java) {
                url = URI(localBuildCacheUrl)
            }
        }
    }
}