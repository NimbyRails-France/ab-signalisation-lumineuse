package sfr.tests

import kotlin.test.Test

/** Tests reconnus par Gradle et par les boutons de test d'IntelliJ. */
class RegressionTests {
    @Test fun balRules() = testBalRules()
    @Test fun networkAndDrivingInstructions() = testNetworkAndInstructions()
    @Test fun brakingAndMemory() = testDrivingModelAndMemory()
    @Test fun originalImplementationEquivalence() = testCppEquivalence()
    @Test fun textureAssets() = testTextureAssets()
}
