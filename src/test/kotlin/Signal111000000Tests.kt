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
import sfr.signals.t_c.c.b.v.`111000000`.Reason as Reason111000000
import sfr.signals.t_c.c.b.v.`111000000`.Settings as Settings111000000
import sfr.signals.t_c.c.b.v.`111000000`.ReturnMode
import sfr.signals.t_c.c.b.v.`101000000`.Signal as T_C_101000000

class Signal111000000Tests {
    private val mod = FrenchSignalsMod()
    private val clear = Observation(Occupancy.Clear, true, true)
    private val approaching = 0x5000000010001L
    // Generic BAL fixtures explicitly select Semaphore; actual defaults are tested below.
    private fun signal(id: Long = 1, next: Long = 0, settings: Map<String, Boolean> = mapOf("returnToSemaphore" to true),
                       observation: Observation = clear, type: String = T_C_111000000.TYPE) =
        Signal(id, next, settings, observation, type = type)

    @Test fun knownBalSettingsAndAspectsRetainTheirRulesAndUnknownsCloseTheCarre() {
        for (mask in 0 until 16) for (next in Aspect011100000.entries) for (occupancy in Occupancy.entries) {
            val settings = Panel011100000.checkboxes.mapIndexed { i, box -> box.name to (mask and (1 shl i) != 0) }.toMap()
            val observation = clear.copy(block = occupancy)
            val expected = Rules011100000.evaluate(Settings011100000.from(settings), observation, next)
            // Both modes retain every open BAL aspect. Square replaces the
            // occupied block's S/S cli, and missing data, with an absolute stop.
            for (square in listOf(false, true)) {
                val observed = if (square) observation.copy(approachingTrain = approaching) else observation
                val actual = Rules111000000.evaluate(settings + ("returnToSemaphore" to !square), observed,
                    Aspect111000000.entries.first { it.bal?.name == next.name })
                if (expected.aspect == Aspect011100000.Unknown || square && occupancy == Occupancy.Occupied)
                    assertEquals(Aspect111000000.Closed, actual.aspect)
                else assertEquals(expected.aspect.name, actual.aspect.bal?.name)
                assertEquals(expected.reason.name, requireNotNull(actual.reason.bal).name)
                assertEquals(if (actual.aspect == Aspect111000000.Closed) AutomaticDriving.stop()
                    else Driving011100000.fromDecision(expected), Driving111000000.driving(actual))
            }
        }
    }

    @Test fun returnToSemaphoreIsUncheckedByDefaultAndSquareOpensWithAConfirmedHead() {
        val type = T_C_111000000.model.type
        assertTrue(type.observeApproach)
        assertEquals(2, type.approachBlocks)
        assertFalse(Panel111000000.returnToSemaphore.defaultValue)
        assertEquals("returnToSemaphore", Panel111000000.returnToSemaphore.name)
        assertEquals("returnToSemaphore", Panel111000000.forceClosed.visibleWhen)
        assertEquals("returnToSemaphore", type.checkboxes[4].name)
        assertEquals("forceClosed", type.checkboxes[5].name)
        for (old in listOf(false, true))
            assertEquals(ReturnMode.Square, Settings111000000.from(mapOf("automaticOpening" to old)).mode)
        assertEquals(ReturnMode.Square, Settings111000000.from(emptyMap()).mode)
        assertEquals(ReturnMode.Semaphore, Settings111000000.from(mapOf("returnToSemaphore" to true)).mode)
        assertEquals(ReturnMode.Square, Settings111000000.from(mapOf("returnToSemaphore" to false)).mode)
        val next = mod.evaluate(T_A_011100000.TYPE, emptyMap(), clear.copy(next = Aspect011100000.VL.ordinal))
        fun decision(settings: Map<String, Boolean>, observation: Observation = clear) =
            assertNotNull(mod.decide(signal(next = 2, settings = settings, observation = observation), next))
        fun indication(value: Decision) = assertNotNull(mod.indication(value)?.of(T_C_111000000.model))

        assertEquals(Aspect111000000.Closed, indication(decision(emptyMap())).aspect)
        assertEquals(Aspect111000000.VL, indication(decision(mapOf("returnToSemaphore" to true))).aspect)
        val absentProfile = signal(next = 2, settings = mapOf("returnToSemaphore" to true))
            .copy(settingsStatus = SettingsStatus.Absent)
        assertEquals(Aspect111000000.Closed, indication(assertNotNull(mod.decide(absentProfile, next))).aspect)
        val settings = mapOf("returnToSemaphore" to false)
        val resting = decision(settings)
        assertEquals(Aspect111000000.Closed, indication(resting).aspect)
        assertEquals(Reason111000000.AwaitingApproach, indication(resting).reason)
        assertFalse(mod.isFault(resting))
        assertEquals(AutomaticDriving.stop(), mod.drivingRule(resting))
        assertEquals("imgs/t_c/c/b/v/111000000/tex01.svg", mod.texture(resting, 0, 500))
        val announced = clear.copy(approachingTrain = approaching)
        assertEquals(Aspect111000000.VL, indication(decision(settings, announced)).aspect)
        // Tail presence or a remembered previous opening cannot replace the head proof.
        assertEquals(Aspect111000000.Closed, indication(decision(settings)).aspect)
        assertEquals(Aspect111000000.VL,
            indication(decision(settings, announced.copy(approachingTrain = approaching + 1))).aspect)
        assertEquals(Aspect111000000.Closed, Rules111000000.evaluate(settings, clear, Aspect111000000.VL).aspect)
        assertEquals(Aspect111000000.VL, Rules111000000.evaluate(settings, announced, Aspect111000000.VL).aspect)
    }

    @Test fun approachNeverReleasesForcedClosureOrMissingFacts() {
        val settings = mapOf("returnToSemaphore" to false, "redFlashEnabled" to true, "greenFlashWork" to true)
        val announced = clear.copy(approachingTrain = approaching)
        val next = mod.evaluate(T_A_011100000.TYPE, emptyMap(), clear.copy(next = Aspect011100000.VL.ordinal))
        fun decision(observation: Observation, values: Map<String, Boolean> = settings,
                     status: SettingsStatus = SettingsStatus.Present) = assertNotNull(mod.decide(
            signal(next = 2, settings = values, observation = observation).copy(settingsStatus = status), next))
        for (observation in listOf(announced.copy(fresh = false), announced.copy(lampFailed = true),
            announced.copy(approachingTrain = 1), announced.copy(routeKnown = false),
            announced.copy(block = Occupancy.Unknown))) {
            val result = decision(observation)
            assertEquals(if (observation.lampFailed) Aspect111000000.Unknown else Aspect111000000.Closed,
                mod.indication(result)!!.of(T_C_111000000.model)!!.aspect)
            assertEquals(AutomaticDriving.stop(), mod.drivingRule(result))
        }
        val unavailable = decision(announced, status = SettingsStatus.Unavailable)
        assertEquals(Aspect111000000.Closed, mod.indication(unavailable)!!.of(T_C_111000000.model)!!.aspect)
        assertEquals(AutomaticDriving.stop(), mod.drivingRule(unavailable))
        for (observation in listOf(announced, announced.copy(block = Occupancy.Occupied),
            announced.copy(routeKnown = false))) {
            for (result in listOf(decision(observation, settings + mapOf("forceClosed" to true, "returnToSemaphore" to true)),
                decision(observation.copy(forcedStop = true)))) {
                val indication = mod.indication(result)!!.of(T_C_111000000.model)!!
                assertEquals(Aspect111000000.Closed, indication.aspect)
                assertEquals(Reason111000000.ForcedStop, indication.reason)
                assertEquals(AutomaticDriving.stop(), mod.drivingRule(result))
            }
        }
    }

    @Test fun upstreamBalAnnouncesTheAbsoluteStopWhileOptionalApproachIsWaiting() {
        val resting = assertNotNull(mod.decide(signal(settings = mapOf("returnToSemaphore" to false)), null))
        val upstream = signal(next = 2, type = T_A_011100000.TYPE)
        val warning = assertNotNull(mod.decide(upstream, resting))
        assertEquals(Aspect011100000.A, mod.indication(warning)!!.of(T_A_011100000.model)!!.aspect)
    }
    @Test fun crossedRefusalIsAbsoluteAndRecoveryRespectsTheSelectedReturnMode() {
        for (conflict in RouteConflict.entries) for (block in Occupancy.entries) for (square in listOf(false, true)) {
            val settings = mapOf("returnToSemaphore" to !square, "redFlashEnabled" to true, "greenFlashWork" to true)
            val input = signal(settings = settings, observation = clear.copy(block = block,
                approachingTrain = approaching, routeConflict = conflict))
            val decision = assertNotNull(mod.decide(input, null))
            val indication = assertNotNull(mod.indication(decision)?.of(T_C_111000000.model))
            assertEquals(Aspect111000000.Closed, indication.aspect)
            assertEquals(Reason111000000.RouteConflict, indication.reason)
            assertEquals(AutomaticDriving.stop(), mod.drivingRule(decision))
            assertFalse(mod.isFault(decision))
            val upstream = signal(next = input.id, type = T_A_011100000.TYPE)
            assertEquals(Aspect011100000.A, assertNotNull(mod.indication(assertNotNull(mod.decide(upstream, decision)))?.of(T_A_011100000.model)).aspect)
            for (redFlash in listOf(false, true)) {
                val released = input.copy(settings = mapOf("returnToSemaphore" to !square, "redFlashEnabled" to redFlash),
                    observation = clear.copy(block = Occupancy.Occupied, approachingTrain = approaching))
                val semaphore = assertNotNull(mod.decide(released, null))
                assertEquals(if (square) Aspect111000000.Closed else if (redFlash) Aspect111000000.RedFlash else Aspect111000000.S,
                    assertNotNull(mod.indication(semaphore)?.of(T_C_111000000.model)).aspect)
                val driving = assertNotNull(mod.drivingRule(semaphore))
                if (square) assertEquals(AutomaticDriving.stop(), driving)
                else {
                    assertNotEquals(AutomaticDriving.stop(), driving)
                    assertTrue(DrivingFlag.OnSight in driving.flags)
                    assertEquals(!redFlash, DrivingFlag.StopThenProceed in driving.flags)
                    if (redFlash) assertEquals(15.0 / 3.6, driving.speedMps)
                }
                assertFalse(mod.isFault(semaphore)) // Known occupancy is normal in both modes.
                val unknownRoute = assertNotNull(mod.decide(released.copy(observation =
                    released.observation.copy(routeKnown = false)), null))
                assertEquals(Aspect111000000.Closed,
                    mod.indication(unknownRoute)!!.of(T_C_111000000.model)!!.aspect)
                assertEquals(AutomaticDriving.stop(), mod.drivingRule(unknownRoute))
            }
        }
    }

    @Test fun forcedSquareIsAbsoluteAnnouncedUpstreamAndCanBeReleased() {
        val occupied = clear.copy(block = Occupancy.Occupied)
        val settings = mapOf("returnToSemaphore" to true, "forceClosed" to true, "redFlashEnabled" to true, "greenFlashWork" to true)
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
        val semaphore = Rules111000000.evaluate(mapOf("returnToSemaphore" to true), occupied, Aspect111000000.VL)
        assertEquals(Aspect111000000.S, semaphore.aspect)
        assertNotEquals(AutomaticDriving.stop(), Driving111000000.driving(semaphore))
    }

    @Test fun cachedForceOnlyAppliesInSemaphoreAndLiveModeSwitchesRestoreItsPreference() {
        val next = mod.evaluate(T_A_011100000.TYPE, emptyMap(), clear.copy(next = Aspect011100000.VL.ordinal))
        val announced = clear.copy(approachingTrain = approaching)
        for (redFlash in listOf(false, true)) {
            val saved = mapOf("returnToSemaphore" to false, "forceClosed" to true, "redFlashEnabled" to redFlash)
            fun decide(values: Map<String, Boolean>, observation: Observation = announced) = assertNotNull(mod.decide(
                signal(next = 2, settings = values, observation = observation), next))
            fun indication(value: Decision) = mod.indication(value)!!.of(T_C_111000000.model)!!
            val square = decide(saved)
            assertEquals(Aspect111000000.VL, indication(square).aspect) // Hidden force never holds the default mode closed.
            assertEquals(Reason111000000.AwaitingApproach, indication(decide(saved, clear)).reason)
            val squareOccupied = decide(saved, announced.copy(block = Occupancy.Occupied))
            assertEquals(Aspect111000000.Closed, indication(squareOccupied).aspect)
            assertEquals(Reason111000000.BlockOccupied, indication(squareOccupied).reason)
            assertEquals(AutomaticDriving.stop(), mod.drivingRule(squareOccupied))
            val semaphoreSettings = saved + ("returnToSemaphore" to true)
            val forced = decide(semaphoreSettings)
            assertEquals(Reason111000000.ForcedStop, indication(forced).reason)
            assertEquals(AutomaticDriving.stop(), mod.drivingRule(forced))
            val released = decide(semaphoreSettings + ("forceClosed" to false),
                announced.copy(block = Occupancy.Occupied))
            assertEquals(if (redFlash) Aspect111000000.RedFlash else Aspect111000000.S, indication(released).aspect)
            assertTrue(DrivingFlag.OnSight in assertNotNull(mod.drivingRule(released)).flags)
            assertEquals(Aspect111000000.VL, indication(decide(semaphoreSettings + ("forceClosed" to false), clear)).aspect)
            assertEquals(Aspect111000000.VL, indication(decide(semaphoreSettings + ("returnToSemaphore" to false))).aspect)
            assertEquals(Reason111000000.ForcedStop,
                indication(decide(saved, announced.copy(forcedStop = true))).reason)
            assertEquals(true, saved["forceClosed"]) // Switching does not rewrite the stored preference.
            val upstream = signal(id = 3, next = 1, type = T_A_011100000.TYPE)
            assertEquals(Aspect011100000.A, mod.indication(assertNotNull(mod.decide(upstream, squareOccupied)))!!
                .of(T_A_011100000.model)!!.aspect)
        }
    }

    @Test fun faultsUnavailableProfilesAndMixedUnknownsNeverBecomeClear() {
        for (observation in listOf(clear.copy(fresh = false), clear.copy(lampFailed = true))) {
            val decision = mod.decide(signal(settings = mapOf("forceClosed" to true), observation = observation), null)!!
            assertEquals(if (observation.lampFailed) Aspect111000000.Unknown else Aspect111000000.Closed,
                mod.indication(decision)!!.of(T_C_111000000.model)!!.aspect)
            assertEquals(AutomaticDriving.stop(), mod.drivingRule(decision))
        }
        val unavailable = signal(settings = mapOf("forceClosed" to true)).copy(settingsStatus = SettingsStatus.Unavailable)
        assertEquals(Aspect111000000.Closed, mod.indication(mod.decide(unavailable, null)!!)!!.of(T_C_111000000.model)!!.aspect)
        for (types in listOf(T_A_011100000.TYPE to T_C_111000000.TYPE, T_C_111000000.TYPE to T_A_011100000.TYPE)) {
            val results = mod.evaluateNetwork(listOf(signal(1, 2, type = types.first),
                signal(2, observation = clear.copy(fresh = false), type = types.second)))
            if (types.first == T_A_011100000.TYPE)
                assertEquals(Aspect011100000.A, mod.indication(results[0])!!.of(T_A_011100000.model)!!.aspect)
            else assertEquals(Aspect111000000.Closed, mod.indication(results[0])!!.of(T_C_111000000.model)!!.aspect)
        }
    }

    @Test fun unknownProtectionClosesBeforePermissiveBalAndKeepsItsDiagnostic() {
        for (automatic in listOf(false, true)) for (redFlash in listOf(false, true)) {
            val settings = mapOf("returnToSemaphore" to automatic, "redFlashEnabled" to redFlash,
                "greenFlashWork" to true)
            for (observation in listOf(clear.copy(routeKnown = false, block = Occupancy.Occupied),
                clear.copy(routeKnown = false), clear.copy(block = Occupancy.Unknown))) {
                val input = signal(settings = settings, observation = observation.copy(approachingTrain = approaching))
                val decision = assertNotNull(mod.decide(input, null))
                val indication = assertNotNull(mod.indication(decision)?.of(T_C_111000000.model))
                assertEquals(Aspect111000000.Closed, indication.aspect)
                assertEquals(if (observation.routeKnown) Reason111000000.BlockUnknown else Reason111000000.RouteUnknown,
                    indication.reason)
                assertEquals(AutomaticDriving.stop(), mod.drivingRule(decision))
                assertTrue(mod.isFault(decision))
                assertEquals("imgs/t_c/c/b/v/111000000/tex01.svg", mod.texture(decision, 0, 500))
                val upstream = signal(next = input.id, type = T_A_011100000.TYPE)
                assertEquals(Aspect011100000.A, mod.indication(assertNotNull(mod.decide(upstream, decision)))!!
                    .of(T_A_011100000.model)!!.aspect)
            }
        }
    }

    @Test fun downstreamResolutionWaitsBeforeUsingAnAbsoluteDiagnosticFallback() {
        val input = signal(next = 2)
        assertNull(mod.decide(input, null)) // Let the SDK resolve its actual neighbour first.
        val next = mod.evaluate(T_A_011100000.TYPE, emptyMap(), clear.copy(next = Aspect011100000.VL.ordinal))
        val resolved = assertNotNull(mod.decide(input, next))
        assertEquals(Aspect111000000.VL, mod.indication(resolved)!!.of(T_C_111000000.model)!!.aspect)
        val noNeighbour = assertNotNull(mod.decide(input.copy(nextSignal = 0), null))
        assertEquals(Reason111000000.DownstreamUnknown, mod.indication(noNeighbour)!!.of(T_C_111000000.model)!!.reason)
        val missingLink = mod.evaluateNetwork(listOf(input)).single()
        assertEquals(Reason111000000.InvalidTopology, mod.indication(missingLink)!!.of(T_C_111000000.model)!!.reason)
        val cycle = mod.evaluateNetwork(listOf(signal(1, 2), signal(2, 1)))
        for (decision in listOf(noNeighbour, missingLink, mod.unknownDecision(T_C_111000000.TYPE),
            mod.invalidNetworkDecision(T_C_111000000.TYPE)) + cycle) {
            assertEquals(Aspect111000000.Closed, mod.indication(decision)!!.of(T_C_111000000.model)!!.aspect)
            assertEquals(AutomaticDriving.stop(), mod.drivingRule(decision))
            assertTrue(mod.isFault(decision))
        }
    }

    @Test fun worksCrossBothModelsWithoutOverwritingForceOrSavedSettings() {
        val source = Panel011100000.workBlocks.withValue(mapOf("greenFlashWork" to true), 2)
        for (sourceType in listOf(T_A_011100000.TYPE, T_C_111000000.TYPE)) {
            val input = listOf(signal(1, 2, source, type = sourceType),
                signal(2, 3, mapOf("returnToSemaphore" to true, "forceClosed" to true)), signal(3, 4, type = T_A_011100000.TYPE),
                signal(4, 5), signal(5, observation = clear.copy(block = Occupancy.Occupied)))
            val prepared = mod.prepareObservedNetwork(input)
            assertEquals(listOf(1L, 2L, 3L), prepared.filter { it.settings["greenFlashWork"] == true }.map { it.id })
            assertEquals(mapOf("returnToSemaphore" to true, "forceClosed" to true), input[1].settings)
            val decisions = mod.evaluateNetwork(input)
            assertEquals(Aspect111000000.Closed, mod.indication(decisions[1])!!.of(T_C_111000000.model)!!.aspect)
            val absent = input.map { if (it.id == 2L) it.copy(settingsStatus = SettingsStatus.Absent) else it }
            assertEquals(false, mod.prepareObservedNetwork(absent)[1].settings["forceClosed"])
            val boundary = input.map { if (it.id == 2L) it.copy(type = T_C_101000000.TYPE) else it }
            assertEquals(listOf(1L), mod.prepareObservedNetwork(boundary).filter { it.settings["greenFlashWork"] == true }.map { it.id })
        }
        assertEquals(listOf(Panel011100000.workBlocks), T_C_111000000.model.type.numbers)
        assertEquals(Panel011100000.checkboxes + listOf(Panel111000000.returnToSemaphore, Panel111000000.forceClosed),
            T_C_111000000.model.type.checkboxes.filterNot { it.name.startsWith("nrf.number.") })
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
