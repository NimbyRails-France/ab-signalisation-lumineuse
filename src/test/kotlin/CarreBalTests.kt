package sfr.tests

import kotlin.test.*
import nimby.*
import sfr.FrenchSignalsMod
import sfr.signals.bal.*
import sfr.signals.carrebal.*
import sfr.signals.carreavertissement.CarreAvertissement

class CarreBalTests {
    private val mod = FrenchSignalsMod()
    private val clear = Observation(Occupancy.Clear, true, true)
    private fun signal(id: Long = 1, next: Long = 0, settings: Map<String, Boolean> = emptyMap(),
                       observation: Observation = clear, type: String = CarreBal.TYPE) =
        Signal(id, next, settings, observation, type = type)

    @Test fun allBalSettingsAndAspectsRetainTheirRulesAndDrivingWhenNotForced() {
        for (mask in 0 until 16) for (next in BalAspect.entries) for (occupancy in Occupancy.entries) {
            val settings = BalPanel.checkboxes.mapIndexed { i, box -> box.name to (mask and (1 shl i) != 0) }.toMap()
            val observation = clear.copy(block = occupancy)
            val expected = BalRules.evaluate(BalSettings.from(settings), observation, next)
            val actual = CarreBalRules.evaluate(settings, observation, CarreBalAspect.entries.first { it.bal?.name == next.name })
            assertEquals(expected.aspect.name, actual.aspect.bal?.name)
            assertEquals(expected.reason.name, actual.reason.bal.name)
            assertEquals(BalDriving.fromDecision(expected), CarreBalDriving.driving(actual))
        }
    }

    @Test fun forcedSquareIsAbsoluteAnnouncedUpstreamAndCanBeReleased() {
        val occupied = clear.copy(block = Occupancy.Occupied)
        val settings = mapOf("forceClosed" to true, "redFlashEnabled" to true, "greenFlashWork" to true)
        for (observation in listOf(clear, occupied, clear.copy(routeKnown = false), clear.copy(forcedStop = true))) {
            val result = assertNotNull(mod.decide(signal(settings = settings, observation = observation), null))
            assertEquals(CarreBalAspect.Closed, mod.indication(result)!!.of(CarreBal.model)!!.aspect)
            assertEquals(AutomaticDriving.stop(), mod.drivingRule(result))
            assertEquals("imgs/cc/cc_sma/tex01.svg", mod.texture(result, 0, 500))
        }
        val chain = listOf(signal(1, 2, type = BalSignals.TYPE), signal(2, settings = settings))
        assertEquals(BalAspect.A, mod.indication(mod.evaluateNetwork(chain)[0])!!.of(BalSignals.model)!!.aspect)
        val released = CarreBalRules.evaluate(settings + ("forceClosed" to false), clear, CarreBalAspect.VL)
        assertEquals(CarreBalAspect.GreenFlash, released.aspect)
        val semaphore = CarreBalRules.evaluate(emptyMap(), occupied, CarreBalAspect.VL)
        assertEquals(CarreBalAspect.S, semaphore.aspect)
        assertNotEquals(AutomaticDriving.stop(), CarreBalDriving.driving(semaphore))
    }

    @Test fun faultsUnavailableProfilesAndMixedUnknownsNeverBecomeClear() {
        for (observation in listOf(clear.copy(fresh = false), clear.copy(lampFailed = true))) {
            val decision = mod.decide(signal(settings = mapOf("forceClosed" to true), observation = observation), null)!!
            assertEquals(CarreBalAspect.Unknown, mod.indication(decision)!!.of(CarreBal.model)!!.aspect)
            assertEquals(AutomaticDriving.stop(), mod.drivingRule(decision))
        }
        val unavailable = signal(settings = mapOf("forceClosed" to true)).copy(settingsStatus = SettingsStatus.Unavailable)
        assertEquals(CarreBalAspect.Unknown, mod.indication(mod.decide(unavailable, null)!!)!!.of(CarreBal.model)!!.aspect)
        for (types in listOf(BalSignals.TYPE to CarreBal.TYPE, CarreBal.TYPE to BalSignals.TYPE)) {
            val results = mod.evaluateNetwork(listOf(signal(1, 2, type = types.first),
                signal(2, observation = clear.copy(fresh = false), type = types.second)))
            assertEquals("indéterminé", mod.indication(results[0])!!.let {
                it.of(BalSignals.model)?.aspect?.label ?: it.of(CarreBal.model)!!.aspect.label })
        }
    }

    @Test fun worksCrossBothModelsWithoutOverwritingForceOrSavedSettings() {
        val source = BalPanel.workBlocks.withValue(mapOf("greenFlashWork" to true), 2)
        for (sourceType in listOf(BalSignals.TYPE, CarreBal.TYPE)) {
            val input = listOf(signal(1, 2, source, type = sourceType),
                signal(2, 3, mapOf("forceClosed" to true)), signal(3, 4, type = BalSignals.TYPE),
                signal(4, 5), signal(5, observation = clear.copy(block = Occupancy.Occupied)))
            val prepared = mod.prepareObservedNetwork(input)
            assertEquals(listOf(1L, 2L, 3L), prepared.filter { it.settings["greenFlashWork"] == true }.map { it.id })
            assertEquals(mapOf("forceClosed" to true), input[1].settings)
            val decisions = mod.evaluateNetwork(input)
            assertEquals(CarreBalAspect.Closed, mod.indication(decisions[1])!!.of(CarreBal.model)!!.aspect)
            val absent = input.map { if (it.id == 2L) it.copy(settingsStatus = SettingsStatus.Absent) else it }
            assertEquals(false, mod.prepareObservedNetwork(absent)[1].settings["forceClosed"])
            val boundary = input.map { if (it.id == 2L) it.copy(type = CarreAvertissement.TYPE) else it }
            assertEquals(listOf(1L), mod.prepareObservedNetwork(boundary).filter { it.settings["greenFlashWork"] == true }.map { it.id })
        }
        assertEquals(listOf(BalPanel.workBlocks), CarreBal.model.type.numbers)
        assertEquals(BalPanel.checkboxes + CarreBalPanel.forceClosed, CarreBal.model.type.checkboxes.filterNot { it.name.startsWith("nrf.number.") })
        assertEquals(4, CarreBal.model.type.construction!!.size)
        assertTrue(CarreBal.model.type.construction!!.left)
    }

    @Test fun ownTexturesIncludeBothBlinkPhasesAndCatalogueEntries() {
        val frames = listOf(CarreBalAspect.GreenFlash to ("05" to "06"),
            CarreBalAspect.YellowFlash to ("07" to "08"), CarreBalAspect.RedFlash to ("09" to "10"))
        for ((aspect, pair) in frames) {
            val animation = CarreBalTextures.forAspect(aspect)
            assertEquals("imgs/cc/cc_sma/tex${pair.first}.svg", animation.first)
            assertEquals("imgs/cc/cc_sma/tex${pair.second}.svg", animation.alternate)
            assertEquals(500L, animation.everyMs)
            assertTrue(animation.first in CarreBalTextures.catalogue)
            assertTrue(animation.alternate in CarreBalTextures.catalogue)
        }
    }
}
