package sfr.tests

import sfr.signals.t_c.c.b.v.`101000000`.Signal as T_C_101000000

import kotlin.test.Test

/** Tests reconnus par Gradle et par les boutons de test d'IntelliJ. */
class RegressionTests {
    @Test fun forcedStatesAreNotEnabledInSfr() {
        val mod = sfr.FrenchSignalsMod()
        for (type in mod.signalTypes) for (code in -1..100)
            kotlin.test.assertNull(mod.forcedDecision(type.id, code))
    }
    @Test fun balRules() = test011100000Rules()
    @Test fun networkAndDrivingInstructions() = testNetworkAndInstructions()
    @Test fun removedDiagnosticCalculatorReportsUnavailable() {
        kotlin.test.assertFalse(sfr.FrenchSignalsMod().plan(
            nimby.Vehicle(), nimby.DrivingSettings(), nimby.DrivingInput(), emptyList()).available)
    }
    @Test fun textureAssets() = testTextureAssets()
}
