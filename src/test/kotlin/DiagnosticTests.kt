package sfr.tests

import kotlin.test.*
import nimby.*
import sfr.FrenchSignalsMod
import sfr.signals.t_a.s.c.v.`011100000`.Signal as T_A_011100000
import sfr.signals.t_c.c.b.v.`101000000`.Signal as T_C_101000000
import sfr.signals.t_c.c.b.v.`101000000`.Aspect as Aspect101000000
import sfr.signals.t_c.c.b.v.`101000000`.Reason as Reason101000000
import sfr.signals.t_c.c.b.v.`111000000`.Signal as T_C_111000000

/** Régressions du journal : les données manquantes restent diagnostiquées,
 * y compris lorsque le Carré affiche son arrêt absolu de repli. */
class DiagnosticTests {
    private val mod = FrenchSignalsMod()
    private val approaching = 0x5000000010001L

    @Test fun occupiedUnknownRouteIsLoggedWithEachModelsRestrictiveIndication() {
        val observation = Observation(Occupancy.Occupied, fresh = true, routeKnown = false)
        for (type in listOf(T_A_011100000.TYPE, T_C_111000000.TYPE)) {
            val value = assertNotNull(mod.decide(Signal(1, observation = observation, type = type), null))
            val indication = assertNotNull(mod.indication(value))
            assertEquals(if (type == T_C_111000000.TYPE) "Closed" else "S", indication.aspect.name)
            assertEquals("RouteUnknown", indication.reason.name)
            assertTrue(mod.isFault(value))
            assertEquals(3, nimby.internal.fault(value.aspect, value.reason)) // Fault + model still active.
            assertEquals(AutomaticDriving.stop(), mod.drivingRule(value))
        }
    }

    @Test fun knownOccupancyAndForcedStopsAreNotReportedAsMissingFacts() {
        for (type in listOf(T_A_011100000.TYPE, T_C_111000000.TYPE)) {
            for (observation in listOf(
                Observation(Occupancy.Occupied, fresh = true, routeKnown = true),
                Observation(Occupancy.Clear, fresh = true, routeKnown = true, forcedStop = true)
            )) {
                val value = assertNotNull(mod.decide(Signal(1, observation = observation, type = type), null))
                assertFalse(mod.isFault(value))
                assertEquals(2, nimby.internal.fault(value.aspect, value.reason))
            }
        }
        val resting = assertNotNull(mod.decide(Signal(1, type = T_C_111000000.TYPE,
            settings = mapOf("returnToSemaphore" to false),
            observation = Observation(Occupancy.Clear, fresh = true, routeKnown = true)), null))
        assertFalse(mod.isFault(resting))
    }

    @Test fun unknownDownstreamClosesCarreAndRecordsTheMissingFact() {
        val known = Observation(Occupancy.Clear, fresh = true, routeKnown = true, approachingTrain = approaching)
        fun decide(observation: Observation) = assertNotNull(mod.decide(Signal(1,
            settings = mapOf("active" to true), observation = observation, type = T_C_101000000.TYPE), null))
        val clear = decide(known)
        assertEquals(Reason101000000.ApproachConfirmed,
            mod.indication(clear)!!.of(T_C_101000000.model)!!.reason)
        assertFalse(mod.isFault(clear))
        for (observation in listOf(known.copy(block = Occupancy.Unknown),
            known.copy(routeKnown = false), known.copy(block = Occupancy.Unknown, routeKnown = false))) {
            val value = decide(observation)
            val indication = mod.indication(value)!!.of(T_C_101000000.model)!!
            assertEquals(Aspect101000000.Closed, indication.aspect)
            assertEquals(if (observation.routeKnown) Reason101000000.BlockUnknown else Reason101000000.RouteUnknown,
                indication.reason)
            assertTrue(mod.isFault(value))
            assertEquals(3, nimby.internal.fault(value.aspect, value.reason))
            assertEquals(AutomaticDriving.stop(), mod.drivingRule(value))
            assertEquals("imgs/t_c/c/b/v/101000000/tex01.svg", mod.texture(value, 0, 500))
        }
    }

    @Test fun unknownDownstreamNeverHidesAnOccupiedOrUnavailableBlock() {
        val observation = Observation(Occupancy.Unknown, fresh = true, routeKnown = false,
            approachingTrain = approaching)
        for (current in listOf(observation.copy(block = Occupancy.Occupied),
            observation.copy(fresh = false), observation.copy(lampFailed = true),
            observation.copy(forcedStop = true))) {
            val value = assertNotNull(mod.decide(Signal(1, type = T_C_101000000.TYPE,
                settings = mapOf("active" to true), observation = current), null))
            assertEquals(Aspect101000000.Closed, mod.indication(value)!!.of(T_C_101000000.model)!!.aspect)
            assertEquals(AutomaticDriving.stop(), mod.drivingRule(value))
        }
    }
}
