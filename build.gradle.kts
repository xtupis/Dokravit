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