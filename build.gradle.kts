plugins {
	kotlin("jvm") version "2.3.0"
}

group = "org.bread_experts_group"
version = "1.0"

repositories {
	mavenCentral()
	mavenLocal()
}

dependencies {
	testImplementation(kotlin("test"))
	implementation("org.bread_experts_group:experimental_application_modifier:D0F1P0")
}

kotlin {
	jvmToolchain(25)
}

tasks.test {
	useJUnitPlatform()
}