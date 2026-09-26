package sfr.tests

import kotlin.test.Test

/** Tests reconnus par Gradle et par les boutons de test d'IntelliJ. */
class RegressionTests {
    @Test fun forcedAspectsKeepPolicyInsideMod() {
        val mod = sfr.FrenchSignalsMod()
        for (aspect in sfr.signalling.Aspect.entries) {
            val decision = kotlin.test.assertNotNull(mod.forcedDecision(aspect.ordinal))
            kotlin.test.assertEquals(aspect.ordinal, decision.aspect)
            mod.texture(decision, 0, 500)
            mod.drivingRule(decision)
        }
        kotlin.test.assertNull(mod.forcedDecision(-1))
        kotlin.test.assertNull(mod.forcedDecision(100))
        val stop = mod.drivingRule(mod.forcedDecision(sfr.signalling.Aspect.S.ordinal)!!)!!
        kotlin.test.assertEquals(setOf(nimby.DrivingFlag.Stop), stop.flags)
        val yellow = mod.drivingRule(mod.forcedDecision(sfr.signalling.Aspect.A.ordinal)!!)!!
        kotlin.test.assertTrue(nimby.DrivingFlag.ApproachPassable in yellow.flags)
    }
    @Test fun balRules() = testBalRules()
    @Test fun networkAndDrivingInstructions() = testNetworkAndInstructions()
    @Test fun brakingAndMemory() = testDrivingModelAndMemory()
    @Test fun originalImplementationEquivalence() = testCppEquivalence()
    @Test fun textureAssets() = testTextureAssets()
}
