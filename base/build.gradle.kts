plugins {
    id("java")
}

group = "com.wq"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
tasks.withType<JavaCompile>{

    options.encoding="UTF-8"
}
