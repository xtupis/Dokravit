plugins {
    id("java")
    application
}

group = "dev.xtupis"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation ("org.telegram:telegrambots:6.8.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.3")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.json:json:20231013")
    implementation ("org.seleniumhq.selenium:selenium-java:4.33.0")
    implementation ("org.junit.jupiter:junit-jupiter-engine:5.12.1")
    implementation ("com.microsoft.playwright:playwright:1.44.0")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("dev.xtupis.Dokravit.Main")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}