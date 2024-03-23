plugins {
    id("java")
    kotlin("jvm")
   // kotlin("kapt")
}

group = "org.example"

repositories {
    mavenCentral()
}


dependencies {
    implementation("com.google.guava:guava:31.1-jre")
    // you can use compileOnlyApi (or even compileApi) if you're only using isolating or aggregating processors (i.e. no dynamic processor)
    implementation("net.ltgt.gradle.incap:incap:1.0.0")
    implementation("com.google.auto.service:auto-service-annotations:1.1.1")
    implementation(project(":base"))


//    kapt("com.google.auto.service:auto-service:1.1.1")
//    kapt("net.ltgt.gradle.incap:incap-processor:1.0.0")
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")
    annotationProcessor("net.ltgt.gradle.incap:incap-processor:1.0.0")

}


tasks.withType<JavaCompile>{
    options.encoding="UTF-8"
}

//kapt {
//    useBuildCache = true
//    arguments{
//        arg("incremental","true")
//    }
//}