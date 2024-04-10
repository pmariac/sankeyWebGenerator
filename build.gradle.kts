plugins {
	kotlin("multiplatform") version "1.9.23"
}

group = "io.data2viz"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
	commonMainImplementation("io.data2viz.d2v:d2v-viz:0.10.7")
	commonMainImplementation("io.data2viz.d2v:d2v-sankey:0.10.7")
	commonMainImplementation("io.data2viz.d2v:d2v-format:0.10.7")
}

kotlin {
	js {
		browser {
		}
		binaries.executable()
	}
}
