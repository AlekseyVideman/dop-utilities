plugins {
    idea
    groovy
    `java-library`
    `maven-publish`
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(26))
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "io.github.alekseyvideman"
            artifactId = "dop-utilities"
            version = "0.1.0-SNAPSHOT"
            from(components["java"])
            pom {
                name.set("dop-utilities")
            }
        }
    }
    repositories {
        mavenLocal()
    }
}

dependencies {
    implementation(libs.jackson)
    implementation(platform(libs.jackson.bom))
    compileOnly(libs.jspecify)

    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.groovy.test)
    testImplementation(libs.spock.core.test)
}

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
    }
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.isFork = true // compile in a separate and reusable process (fast)
    }
    idea {
        module.isDownloadJavadoc = true
    }
}
