pluginManagement {
    val sdk = providers.gradleProperty("nrfSdkDir")
        .orElse(providers.environmentVariable("NRF_KOTLIN_SDK"))
        .orNull ?: error("Configurer nrfSdkDir ou NRF_KOTLIN_SDK vers le kit SDK Kotlin.")
    repositories {
        maven { url = uri(file(sdk).resolve("gradle-repository")) }
        gradlePluginPortal()
        mavenCentral()
    }
    // Keep the build plugin aligned with the API and native bridge in this kit.
    val metadata = groovy.json.JsonSlurper().parseText(file(sdk).resolve("sdk.json").readText().removePrefix("\uFEFF")) as Map<*, *>
    plugins { id("fr.nimbyrails.mod") version (metadata["gradlePluginVersion"] as String) }
}

dependencyResolutionManagement { repositories { mavenCentral() } }
rootProject.name = "signalisation-francaise-realiste"
