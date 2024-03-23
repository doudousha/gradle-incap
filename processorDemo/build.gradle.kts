import org.jetbrains.kotlin.gradle.internal.Kapt3GradleSubplugin.Companion.isIncrementalKapt

plugins {
    id("java")
    kotlin("jvm")
   // kotlin("kapt")
}

group = "com.wq"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":base"))
   // kapt(project(":processor"))
  annotationProcessor(project(":processor"))
}

tasks.test {
    useJUnitPlatform()
}
tasks.withType<JavaCompile> {

}
//
//kapt {
//    useBuildCache = true
//    arguments{
//        arg("incremental","true")
//    }
//}