package sfr.tests

import kotlin.test.*
import nimby.*
import sfr.FrenchSignalsMod
import sfr.signals.t_a.s.c.v.`011100000`.Signal as T_A_011100000
import sfr.signals.t_a.s.c.v.`011100000`.Aspect as Aspect011100000
import sfr.signals.t_a.s.c.v.`011100000`.Rules as Rules011100000
import sfr.signals.t_a.s.c.v.`011100000`.Panel as Panel011100000
import sfr.signals.t_a.s.c.v.`011100000`.Settings as Settings011100000
import sfr.signals.t_a.s.c.v.`011100000`.Driving as Driving011100000
import sfr.signals.t_c.c.b.v.`111000000`.Signal as T_C_111000000
import sfr.signals.t_c.c.b.v.`111000000`.Aspect as Aspect111000000
import sfr.signals.t_c.c.b.v.`111000000`.Rules as Rules111000000
import sfr.signals.t_c.c.b.v.`111000000`.Panel as Panel111000000
import sfr.signals.t_c.c.b.v.`111000000`.Textures as Textures111000000
import sfr.signals.t_c.c.b.v.`111000000`.Driving as Driving111000000
import sfr.signals.t_c.c.b.v.`101000000`.Signal as T_C_101000000

class Signal111000000Tests {
    private val mod = FrenchSignalsMod()
    private val clear = Observation(Occupancy.Clear, true, true)
    private fun signal(id: Long = 1, next: Long = 0, settings: Map<String, Boolean> = emptyMap(),
                       observation: Observation = clear, type: String = T_C_111000000.TYPE) =
        Signal(id, next, settings, observation, type = type)

    @Test fun allBalSettingsAndAspectsRetainTheirRulesAndDrivingWhenNotForced() {
        for (mask in 0 until 16) for (next in Aspect011100000.entries) for (occupancy in Occupancy.entries) {
            val settings = Panel011100000.checkboxes.mapIndexed { i, box -> box.name to (mask and (1 shl i) != 0) }.toMap()
            val observation = clear.copy(block = occupancy)
            val expected = Rules011100000.evaluate(Settings011100000.from(settings), observation, next)
            val actual = Rules111000000.evaluate(settings, observation, Aspect111000000.entries.first { it.bal?.name == next.name })
            assertEquals(expected.aspect.name, actual.aspect.bal?.name)
            assertEquals(expected.reason.name, actual.reason.bal.name)
            assertEquals(Driving011100000.fromDecision(expected), Driving111000000.driving(actual))
        }
    }

    @Test fun forcedSquareIsAbsoluteAnnouncedUpstreamAndCanBeReleased() {
        val occupied = clear.copy(block = Occupancy.Occupied)
        val settings = mapOf("forceClosed" to true, "redFlashEnabled" to true, "greenFlashWork" to true)
        for (observation in listOf(clear, occupied, clear.copy(routeKnown = false), clear.copy(forcedStop = true))) {
            val result = assertNotNull(mod.decide(signal(settings = settings, observation = observation), null))
            assertEquals(Aspect111000000.Closed, mod.indication(result)!!.of(T_C_111000000.model)!!.aspect)
            assertEquals(AutomaticDriving.stop(), mod.drivingRule(result))
            assertEquals("imgs/t_c/c/b/v/111000000/tex01.svg", mod.texture(result, 0, 500))
        }
        val chain = listOf(signal(1, 2, type = T_A_011100000.TYPE), signal(2, settings = settings))
        assertEquals(Aspect011100000.A, mod.indication(mod.evaluateNetwork(chain)[0])!!.of(T_A_011100000.model)!!.aspect)
        val released = Rules111000000.evaluate(settings + ("forceClosed" to false), clear, Aspect111000000.VL)
        assertEquals(Aspect111000000.GreenFlash, released.aspect)
        val semaphore = Rules111000000.evaluate(emptyMap(), occupied, Aspect111000000.VL)
        assertEquals(Aspect111000000.S, semaphore.aspect)
        assertNotEquals(AutomaticDriving.stop(), Driving111000000.driving(semaphore))
    }

    @Test fun faultsUnavailableProfilesAndMixedUnknownsNeverBecomeClear() {
        for (observation in listOf(clear.copy(fresh = false), clear.copy(lampFailed = true))) {
            val decision = mod.decide(signal(settings = mapOf("forceClosed" to true), observation = observation), null)!!
            assertEquals(Aspect111000000.Unknown, mod.indication(decision)!!.of(T_C_111000000.model)!!.aspect)
            assertEquals(AutomaticDriving.stop(), mod.drivingRule(decision))
        }
        val unavailable = signal(settings = mapOf("forceClosed" to true)).copy(settingsStatus = SettingsStatus.Unavailable)
        assertEquals(Aspect111000000.Unknown, mod.indication(mod.decide(unavailable, null)!!)!!.of(T_C_111000000.model)!!.aspect)
        for (types in listOf(T_A_011100000.TYPE to T_C_111000000.TYPE, T_C_111000000.TYPE to T_A_011100000.TYPE)) {
            val results = mod.evaluateNetwork(listOf(signal(1, 2, type = types.first),
                signal(2, observation = clear.copy(fresh = false), type = types.second)))
            assertEquals("indéterminé", mod.indication(results[0])!!.let {
                it.of(T_A_011100000.model)?.aspect?.label ?: it.of(T_C_111000000.model)!!.aspect.label })
        }
    }

    @Test fun worksCrossBothModelsWithoutOverwritingForceOrSavedSettings() {
        val source = Panel011100000.workBlocks.withValue(mapOf("greenFlashWork" to true), 2)
        for (sourceType in listOf(T_A_011100000.TYPE, T_C_111000000.TYPE)) {
            val input = listOf(signal(1, 2, source, type = sourceType),
                signal(2, 3, mapOf("forceClosed" to true)), signal(3, 4, type = T_A_011100000.TYPE),
                signal(4, 5), signal(5, observation = clear.copy(block = Occupancy.Occupied)))
            val prepared = mod.prepareObservedNetwork(input)
            assertEquals(listOf(1L, 2L, 3L), prepared.filter { it.settings["greenFlashWork"] == true }.map { it.id })
            assertEquals(mapOf("forceClosed" to true), input[1].settings)
            val decisions = mod.evaluateNetwork(input)
            assertEquals(Aspect111000000.Closed, mod.indication(decisions[1])!!.of(T_C_111000000.model)!!.aspect)
            val absent = input.map { if (it.id == 2L) it.copy(settingsStatus = SettingsStatus.Absent) else it }
            assertEquals(false, mod.prepareObservedNetwork(absent)[1].settings["forceClosed"])
            val boundary = input.map { if (it.id == 2L) it.copy(type = T_C_101000000.TYPE) else it }
            assertEquals(listOf(1L), mod.prepareObservedNetwork(boundary).filter { it.settings["greenFlashWork"] == true }.map { it.id })
        }
        assertEquals(listOf(Panel011100000.workBlocks), T_C_111000000.model.type.numbers)
        assertEquals(Panel011100000.checkboxes + Panel111000000.forceClosed, T_C_111000000.model.type.checkboxes.filterNot { it.name.startsWith("nrf.number.") })
        assertEquals(4, T_C_111000000.model.type.construction!!.size)
        assertTrue(T_C_111000000.model.type.construction!!.left)
    }

    @Test fun ownTexturesIncludeBothBlinkPhasesAndCatalogueEntries() {
        val frames = listOf(Aspect111000000.GreenFlash to ("05" to "06"),
            Aspect111000000.YellowFlash to ("07" to "08"), Aspect111000000.RedFlash to ("09" to "10"))
        for ((aspect, pair) in frames) {
            val animation = Textures111000000.forAspect(aspect)
            assertEquals("imgs/t_c/c/b/v/111000000/tex${pair.first}.svg", animation.first)
            assertEquals("imgs/t_c/c/b/v/111000000/tex${pair.second}.svg", animation.alternate)
            assertEquals(500L, animation.everyMs)
            assertTrue(animation.first in Textures111000000.catalogue)
            assertTrue(animation.alternate in Textures111000000.catalogue)
        }
    }
}
