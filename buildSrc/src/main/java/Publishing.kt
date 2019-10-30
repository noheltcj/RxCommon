@file:Suppress("UnstableApiUsage")

import Properties.requiredForReleaseProperty
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import java.net.URI

object Publishing {
    fun PublishingExtension.addRepositories(project: Project) {
        repositories {
            maven {
                val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
                val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"

                url = URI(
                    if (Properties.isRelease) {
                        releasesRepoUrl
                    } else {
                        snapshotsRepoUrl
                    }
                )

                authentication {
                    credentials {
                        username = project.requiredForReleaseProperty("ossrhUsername")
                        password = project.requiredForReleaseProperty("ossrhPassword")
                    }
                }
            }
        }
    }

    fun MavenPublication.mutatePublicationPom(projectName: String) {
        pom {
            name.set(projectName)
            inceptionYear.set("2018")

            description.set("A multiplatform ReactiveX implementation.")
            url.set("https://github.com/noheltcj/RxCommon")

            scm {
                connection.set("scm:git:https://github.com/noheltcj/RxCommon")
                developerConnection.set("scm:git:https://github.com/noheltcj/RxCommon")
                url.set("https://github.com/noheltcj/RxCommon")
            }

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://github.com/noheltcj/RxCommon/blob/master/LICENSE")
                }
            }

            developers {
                developer {
                    id.set("noheltcj")
                    name.set("Colton Nohelty")
                    email.set("noheltycolton@gmail.com")
                }
            }
        }
    }
}