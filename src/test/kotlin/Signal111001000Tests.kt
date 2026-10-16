package sfr.tests

import kotlin.test.*
import nimby.*
import sfr.FrenchSignalsMod
import sfr.signals.t_a.s.c.v.`011100000`.Signal as T_A_011100000
import sfr.signals.t_a.s.c.v.`011100000`.Aspect as Aspect011100000
import sfr.signals.t_a.s.c.v.`011100000`.Panel as BalPanel
import sfr.signals.t_c.c.b.v.`101000000`.Signal as T_C_101000000
import sfr.signals.t_c.c.b.v.`111000000`.Signal as T_C_111000000
import sfr.signals.t_c.c.b.v.`111000000`.Aspect as Aspect111000000
import sfr.signals.t_c.c.b.v.`111000000`.Rules as Rules111000000
import sfr.signals.t_c.c.b.v.`111000000`.Driving as Driving111000000
import sfr.signals.t_c.c.b.v.`111000000`.Diagnostics as Diagnostics111000000
import sfr.signals.t_c.c.b.v.`111000000`.Textures as Textures111000000
import sfr.signals.t_c.c.b.v.`111001000`.Signal as T_C_111001000
import sfr.signals.t_c.c.b.v.`111001000`.Aspect as Aspect111001000
import sfr.signals.t_c.c.b.v.`111001000`.Reason as Reason111001000
import sfr.signals.t_c.c.b.v.`111001000`.Rules as Rules111001000
import sfr.signals.t_c.c.b.v.`111001000`.Driving as Driving111001000
import sfr.signals.t_c.c.b.v.`111001000`.Diagnostics as Diagnostics111001000
import sfr.signals.t_c.c.b.v.`111001000`.Textures as Textures111001000

class Signal111001000Tests {
    private val mod = FrenchSignalsMod()
    private val approaching = 0x5000000010001L
    private val clear = Observation(Occupancy.Clear, fresh = true, routeKnown = true)
    private val nextClear get() = mod.evaluate(T_C_111000000.TYPE, mapOf("returnToSemaphore" to true),
        clear.copy(next = Aspect111000000.VL.ordinal))

    @Test fun fourthModelOwnsItsDeclarationAndRetainsTheBalConfiguration() {
        val original = T_C_111000000.model.type
        val added = T_C_111001000.model.type
        assertEquals(added, mod.signalTypes[3])
        assertEquals("sfr.t_c.c.b.v.111001000", added.id)
        assertEquals("sfr_t_c_c_b_v_111001000", added.textureSet)
        assertEquals(BalPanel.checkboxes, added.checkboxes.filterNot { it.name.startsWith("nrf.number.") })
        assertTrue(added.checkboxes.none { it.name in setOf("returnToSemaphore", "automaticOpening", "forceClosed") })
        assertEquals(original.numbers, added.numbers)
        assertTrue(added.observeApproach)
        assertEquals(2, added.approachBlocks)
        assertTrue(added.observeRoute)
        assertEquals(4, added.construction!!.size)
        assertTrue(added.construction!!.left)
        assertEquals(Aspect111000000.entries.map { it.name },
            Aspect111001000.entries.take(Aspect111000000.entries.size).map { it.name })
        assertEquals(listOf("White", "WhiteFlash"), Aspect111001000.entries.takeLast(2).map { it.name })
    }

    @Test fun fixedSquareMatchesTheOriginalSquarePolicyAndIgnoresInjectedModeKeys() {
        val announced = clear.copy(approachingTrain = approaching)
        val observations = listOf(announced, announced.copy(block = Occupancy.Occupied),
            announced.copy(block = Occupancy.Occupied, redFlashCondition = true),
            announced.copy(block = Occupancy.Unknown), announced.copy(routeKnown = false),
            announced.copy(routeKnown = false, block = Occupancy.Occupied),
            announced.copy(fresh = false), announced.copy(lampFailed = true),
            announced.copy(forcedStop = true), announced.copy(approachingTrain = null)) +
            RouteConflict.entries.map { announced.copy(routeConflict = it) }
        val seen = mutableSetOf<Aspect111001000>()
        for (mask in 0 until 16) for (square in listOf(false, true)) for (force in listOf(false, true)) {
            val bal = BalPanel.checkboxes.mapIndexed { i, box -> box.name to (mask and (1 shl i) != 0) }.toMap()
            val settings = bal + mapOf("returnToSemaphore" to !square, "automaticOpening" to square, "forceClosed" to force)
            val originalSquare = bal + mapOf("returnToSemaphore" to false, "forceClosed" to false)
            for (next in Aspect111000000.entries) for (observation in observations) {
                val expected = Rules111000000.evaluate(originalSquare, observation, next)
                val actual = Rules111001000.evaluate(settings, observation, Aspect111001000.valueOf(next.name))
                seen += actual.aspect
                assertEquals(expected.aspect.name, actual.aspect.name)
                assertEquals(expected.reason.name, actual.reason.name)
                assertEquals(Driving111000000.driving(expected), Driving111001000.driving(actual))
                assertEquals(Diagnostics111000000.isFault(expected), Diagnostics111001000.isFault(actual))
            }
        }
        assertEquals(Aspect111000000.entries.filterNot {
            it == Aspect111000000.S || it == Aspect111000000.RedFlash
        }.map { Aspect111001000.valueOf(it.name) }.toSet(), seen)
    }

    @Test fun liveRulesAlwaysUseSquareAndIgnoreHiddenOrLegacyPreferences() {
        fun decide(settings: Map<String, Boolean>, observation: Observation = clear,
                   status: SettingsStatus = SettingsStatus.Present) = assertNotNull(mod.decide(
            Signal(31, 32, settings, observation, status, T_C_111001000.TYPE), nextClear))
        fun indication(value: Decision) = assertNotNull(mod.indication(value)?.of(T_C_111001000.model))
        val resting = decide(emptyMap())
        assertEquals(Aspect111001000.Closed, indication(resting).aspect)
        assertEquals(Reason111001000.AwaitingApproach, indication(resting).reason)
        assertFalse(mod.isFault(resting))
        val announced = clear.copy(approachingTrain = approaching)
        val occupied = announced.copy(block = Occupancy.Occupied)
        for (semaphore in listOf(false, true)) for (force in listOf(false, true)) for (old in listOf(false, true)) {
            val injected = mapOf("returnToSemaphore" to semaphore, "forceClosed" to force,
                "automaticOpening" to old, "redFlashEnabled" to true)
            assertEquals(Aspect111001000.VL, indication(decide(injected, announced)).aspect)
            assertEquals(Reason111001000.AwaitingApproach, indication(decide(injected)).reason)
            val closed = decide(injected, occupied)
            assertEquals(Aspect111001000.Closed, indication(closed).aspect)
            assertEquals(Reason111001000.BlockOccupied, indication(closed).reason)
            assertEquals(AutomaticDriving.stop(), mod.drivingRule(closed))
            assertFalse(mod.isFault(closed))
            val stopped = decide(injected, announced.copy(forcedStop = true))
            assertEquals(Reason111001000.ForcedStop, indication(stopped).reason)
            assertEquals(AutomaticDriving.stop(), mod.drivingRule(stopped))
        }
        val unused = mapOf("returnToSemaphore" to true, "forceClosed" to true, "automaticOpening" to false)
        val absent = decide(unused, status = SettingsStatus.Absent)
        assertEquals(Reason111001000.AwaitingApproach, indication(absent).reason)
        for (observation in listOf(announced.copy(fresh = false), announced.copy(routeKnown = false),
            announced.copy(block = Occupancy.Unknown), announced.copy(approachingTrain = 1))) {
            val value = decide(unused, observation)
            assertEquals(Aspect111001000.Closed, indication(value).aspect)
            assertEquals(AutomaticDriving.stop(), mod.drivingRule(value))
            assertTrue(mod.isFault(value))
        }
        val unavailable = decide(emptyMap(), status = SettingsStatus.Unavailable)
        assertEquals(Aspect111001000.Closed, indication(unavailable).aspect)
        assertTrue(mod.isFault(unavailable))
        assertEquals(Aspect111001000.Unknown, indication(decide(unused, announced.copy(lampFailed = true))).aspect)
        for (conflict in RouteConflict.entries) {
            val unknown = announced.copy(routeKnown = false, block = Occupancy.Unknown)
            val refused = decide(unused, unknown.copy(routeConflict = conflict))
            assertEquals(Reason111001000.RouteConflict, indication(refused).reason)
            assertFalse(mod.isFault(refused))
            for (unqualified in listOf(unknown.copy(routeConflict = conflict, fresh = false),
                unknown.copy(routeConflict = conflict, approachingTrain = 1))) {
                val value = decide(unused, unqualified)
                assertEquals(Reason111001000.ObservationUnavailable, indication(value).reason)
                assertEquals(AutomaticDriving.stop(), mod.drivingRule(value))
                assertTrue(mod.isFault(value))
            }
            assertEquals(Aspect111001000.Closed, indication(decide(unused, unknown)).aspect)
            assertEquals(Aspect111001000.VL, indication(decide(unused, announced)).aspect)
        }
    }

    @Test fun crossModelNeighboursPreserveClosedAndUnknownIndications() {
        val fixtures: List<Triple<Map<String, Boolean>, Observation, String>> = listOf(
            Triple(emptyMap(), clear.copy(forcedStop = true), "A"),
            Triple(emptyMap(), clear.copy(block = Occupancy.Occupied), "A"),
            Triple(mapOf("redFlashEnabled" to true), clear.copy(block = Occupancy.Occupied, redFlashCondition = true), "A"),
            Triple(emptyMap(), clear.copy(next = Aspect111000000.S.ordinal), "YellowFlash"),
            Triple(mapOf("yellowFlashEnabled" to true), clear.copy(next = Aspect111000000.A.ordinal), "VL"),
            Triple(mapOf("greenFlashWork" to true), clear.copy(next = Aspect111000000.VL.ordinal), "VL"),
            Triple(emptyMap(), clear.copy(next = Aspect111000000.VL.ordinal), "VL"))
        for (downstreamType in listOf(T_C_111000000.TYPE, T_C_111001000.TYPE))
            for (upstreamType in listOf(T_A_011100000.TYPE, T_C_111000000.TYPE, T_C_111001000.TYPE)) {
                val upstream = Signal(21, 22, mapOf("yellowFlashEnabled" to true, "returnToSemaphore" to true),
                    clear.copy(approachingTrain = approaching), type = upstreamType)
                for ((settings, observation, expected) in fixtures) {
                    val downstream = mod.evaluate(downstreamType, settings + ("returnToSemaphore" to true),
                        observation.copy(approachingTrain = approaching))
                    val actual = assertNotNull(mod.decide(upstream, downstream))
                    assertEquals(expected, mod.indication(actual)!!.aspect.name)
                }
                val unknown = mod.evaluate(downstreamType, emptyMap(), clear.copy(lampFailed = true))
                val actual = assertNotNull(mod.decide(upstream, unknown))
                assertEquals(if (upstreamType == T_A_011100000.TYPE) "Unknown" else "Closed", mod.indication(actual)!!.aspect.name)
                assertEquals("DownstreamUnknown", mod.indication(actual)!!.reason.name)
                assertEquals(AutomaticDriving.stop(), mod.drivingRule(actual))
            }
    }

    @Test fun workZonesCrossAllBalModelsAndStopAtTheSimpleCarre() {
        val types = listOf(T_A_011100000.TYPE, T_C_111000000.TYPE, T_C_111001000.TYPE)
        for (source in types) {
            val ordered = listOf(source) + types.filterNot { it == source }
            val saved = BalPanel.workBlocks.withValue(mapOf("greenFlashWork" to true), 2)
            val input = ordered.mapIndexed { i, type -> Signal(i.toLong() + 1, if (i == 2) 0 else i.toLong() + 2,
                settings = (if (i == 0) saved else emptyMap()) +
                    (if (type == T_C_111000000.TYPE) mapOf("returnToSemaphore" to true, "forceClosed" to (i != 0)) else emptyMap()),
                observation = clear.copy(block = if (i == 2) Occupancy.Occupied else Occupancy.Clear,
                    approachingTrain = approaching), type = type) }
            val prepared = mod.prepareObservedNetwork(input)
            assertTrue(prepared.all { it.settings["greenFlashWork"] == true })
            assertTrue(input.drop(1).none { it.settings["greenFlashWork"] == true })
            val decisions = mod.evaluateNetwork(input)
            input.forEachIndexed { i, signal -> if (signal.settings["forceClosed"] == true) {
                assertEquals("Closed", mod.indication(decisions[i])!!.aspect.name)
                assertEquals(AutomaticDriving.stop(), mod.drivingRule(decisions[i]))
            } }
            val boundary = input.map { if (it.id == 2L) it.copy(type = T_C_101000000.TYPE,
                settings = mapOf("active" to true)) else it }
            assertEquals(listOf(1L), mod.prepareObservedNetwork(boundary)
                .filter { it.settings["greenFlashWork"] == true }.map { it.id })
        }
    }

    @Test fun ownCataloguePreservesBalFramesAndProvidesBothWhiteIndications() {
        val folder = "imgs/t_c/c/b/v/111001000/"
        val expected = ((0..10).toList() + listOf(12, 13, 14)).map {
            folder + "tex${it.toString().padStart(2, '0')}.svg" } + (folder + "xx.svg")
        assertEquals(expected, Textures111001000.catalogue)
        assertFalse(Textures111001000.catalogue.any { it.endsWith("tex11.svg") })
        for (aspect in Aspect111000000.entries) {
            val added = Textures111001000.forAspect(Aspect111001000.valueOf(aspect.name))
            val original = Textures111000000.forAspect(aspect)
            assertEquals(original.everyMs, added.everyMs)
            for (time in listOf(0L, 500L)) {
                val frame = added.frameAt(time)
                assertEquals(original.frameAt(time).substringAfterLast('/'), frame.substringAfterLast('/'))
                assertTrue(frame in expected)
            }
        }
        val white = Textures111001000.forAspect(Aspect111001000.White)
        val flashing = Textures111001000.forAspect(Aspect111001000.WhiteFlash)
        assertEquals(folder + "tex12.svg", white.frameAt(0))
        assertEquals(white.frameAt(0), white.frameAt(500))
        assertEquals(500L, flashing.everyMs)
        assertEquals(folder + "tex13.svg", flashing.frameAt(0))
        assertEquals(folder + "tex13.svg", flashing.frameAt(499))
        assertEquals(folder + "tex14.svg", flashing.frameAt(500))
        assertEquals(folder + "tex14.svg", flashing.frameAt(999))
        assertEquals(folder + "tex13.svg", flashing.frameAt(1000))
    }

    @Test fun localEnumsFallbacksAndUnresolvedNetworksStayIndependent() {
        val added = mod.evaluate(T_C_111001000.TYPE, emptyMap(), clear.copy(next = Aspect111001000.VL.ordinal))
        val original = mod.evaluate(T_C_111000000.TYPE, emptyMap(), clear.copy(next = Aspect111000000.VL.ordinal))
        assertNotEquals(added, original)
        assertNull(mod.indication(added)!!.of(T_C_111000000.model))
        assertNull(mod.indication(original)!!.of(T_C_111001000.model))
        val unresolved = Signal(41, 42, observation = clear.copy(approachingTrain = approaching), type = T_C_111001000.TYPE)
        assertNull(mod.decide(unresolved, null))
        val fallbacks = listOf(mod.unknownDecision(T_C_111001000.TYPE),
            mod.invalidNetworkDecision(T_C_111001000.TYPE), mod.evaluateNetwork(listOf(unresolved)).single())
        val cycle = mod.evaluateNetwork(listOf(unresolved,
            Signal(42, 41, observation = clear.copy(approachingTrain = approaching), type = T_C_111000000.TYPE)))
        for (value in fallbacks + cycle) {
            assertEquals("Closed", mod.indication(value)!!.aspect.name)
            assertEquals(AutomaticDriving.stop(), mod.drivingRule(value))
            assertTrue(mod.isFault(value))
        }
    }
}
