/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import java.util.Base64

plugins {
    id("javadoc-stub-convention")
    id("org.gradle.maven-publish")
    id("signing")
}

publishing {
    repositories.maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
        name = "mavencentral"

        credentials {
            username = System.getenv("OSSRH_USER") ?: properties["mavenCentralUsername"].toString()
            password = System.getenv("OSSRH_KEY") ?: properties["mavenCentralPassword"].toString()
        }
    }

    publications.withType<MavenPublication> {
        // Provide artifacts information requited by Maven Central
        pom {
            name.set("MOKO {{name}}")
            description.set("Add description")
            url.set("https://github.com/icerockdev/moko-{{name}}")
            licenses {
                license {
                    name.set("Apache-2.0")
                    distribution.set("repo")
                    url.set("https://github.com/icerockdev/moko-{{name}}/blob/master/LICENSE.md")
                }
            }

            developers {
                developer {
                    id.set("Alex009")
                    name.set("Aleksey Mikhailov")
                    email.set("aleksey.mikhailov@icerockdev.com")
                }
            }

            scm {
                connection.set("scm:git:ssh://github.com/icerockdev/moko-{{name}}.git")
                developerConnection.set("scm:git:ssh://github.com/icerockdev/moko-{{name}}.git")
                url.set("https://github.com/icerockdev/moko-{{name}}")
            }
        }
    }
}


signing {
    sign(publishing.publications)
}
//signing {
//    val signingKeyId: String? = System.getenv("SIGNING_KEY_ID")
//    val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
//    val signingKey: String? = System.getenv("SIGNING_KEY")?.let { base64Key ->
//        String(Base64.getDecoder().decode(base64Key))
//    }
//    if (signingKeyId != null) {
//        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
//        sign(publishing.publications)
//    }
//}
