plugins {
    java
    kotlin("jvm") version "1.3.72"
}

subprojects {

    apply(plugin = "java")
    apply(plugin = "kotlin")

    group = "rug.stan"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://dl.bintray.com/kotlin/kotlin-datascience")
    }

    dependencies {
        implementation("org.jetbrains:kotlin-numpy:0.1.5")
        implementation("com.aparapi:aparapi:2.0.0")
        implementation("org.jblas:jblas:1.2.4")
        implementation("org.jocl:jocl:2.0.2")
        implementation(kotlin("stdlib-jdk8"))
        "testImplementation"("junit:junit:4.12")
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    tasks {
        compileKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
}