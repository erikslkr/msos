plugins {
    kotlin("jvm") version "2.2.0"
    id("org.openjfx.javafxplugin") version "0.1.0"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation("org.openjfx:javafx-controls:21.0.2")
    implementation("org.openjfx:javafx-fxml:21.0.2")
    implementation("org.fxmisc.richtext:richtextfx:0.11.1")
    implementation("org.graphstream:gs-core:2.0")
    implementation("org.graphstream:gs-ui-javafx:2.0")
}

javafx {
    version = "24"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics")
}

application {
    mainClass.set("MainKt")
}
