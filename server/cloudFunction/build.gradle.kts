plugins {
    id("java")
}

group = "epicurius"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.cloud.functions:functions-framework-api:1.1.0")
    implementation("com.google.cloud:google-cloud-vision:3.17.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}