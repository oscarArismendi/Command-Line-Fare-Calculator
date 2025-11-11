plugins {
    kotlin("jvm") version "2.1.21"
    id ("com.diffplug.spotless") version "6.22.0" apply false
    application
}

group = "org.fare.calculator"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-assertions-core-jvm:6.0.1")
    implementation("org.apache.poi:poi:5.4.1")
    implementation("org.apache.poi:poi-ooxml:5.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.3.6")
    implementation("com.michael-bull.kotlin-result:kotlin-result:2.1.0")

    // logger
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
    implementation("ch.qos.logback:logback-classic:1.4.11")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("org.fare.calculator.FareCalculatorAppKt")
}

allprojects {
    apply(plugin = "com.diffplug.spotless")
    configure<com.diffplug.gradle.spotless.SpotlessExtension>{
        kotlin {
            target("**/*.kt")
            ktlint("1.0.0")
        }
    }
}
