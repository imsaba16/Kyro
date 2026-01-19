plugins {
    kotlin("jvm") version "2.2.20"
    application
}

group = "com.kyro.core"
version = "0.0.1-alpha"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.kyro.cli.MainKt")
}
tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}