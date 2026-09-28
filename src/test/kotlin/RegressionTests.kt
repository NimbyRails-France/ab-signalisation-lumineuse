package sfr.tests

import sfr.signals.bal.*
import sfr.signals.carreavertissement.CarreAvertissement

import kotlin.test.Test

/** Tests reconnus par Gradle et par les boutons de test d'IntelliJ. */
class RegressionTests {
    @Test fun forcedStatesAreNotEnabledInSfr() {
        val mod = sfr.FrenchSignalsMod()
        for (type in mod.signalTypes) for (code in -1..100)
            kotlin.test.assertNull(mod.forcedDecision(type.id, code))
    }
    @Test fun balRules() = testBalRules()
    @Test fun networkAndDrivingInstructions() = testNetworkAndInstructions()
    @Test fun removedDiagnosticCalculatorReportsUnavailable() {
        kotlin.test.assertFalse(sfr.FrenchSignalsMod().plan(
            nimby.Vehicle(), nimby.DrivingSettings(), nimby.DrivingInput(), emptyList()).available)
    }
    @Test fun textureAssets() = testTextureAssets()
}
