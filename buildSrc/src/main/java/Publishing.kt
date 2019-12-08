@file:Suppress("UnstableApiUsage")

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import java.net.URI

object Publishing {
    val shouldSign get() =
        Properties.ossrhPassword != null && Properties.ossrhUsername != null

    fun PublishingExtension.addRepositories(project: Project) {
        repositories {
            val ossrhUsername = Properties.ossrhUsername ?: return@repositories
            val ossrhPassword = Properties.ossrhPassword ?: return@repositories

            maven {
                val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"

                url = URI(releasesRepoUrl)

                authentication {
                    credentials {
                        username = ossrhUsername
                        password = ossrhPassword
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