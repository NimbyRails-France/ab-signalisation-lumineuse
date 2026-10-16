package sfr.tests

import kotlin.test.*
import nimby.*
import sfr.FrenchSignalsMod
import sfr.signals.t_a.s.c.v.`011100000`.Signal as BalSignal
import sfr.signals.t_c.c.b.v.`101000000`.Signal as SimpleCarre
import sfr.signals.t_c.c.b.v.`111000000`.Signal as OriginalCarre
import sfr.signals.t_c.c.b.v.`111000000`.Aspect as OriginalAspect
import sfr.signals.t_c.c.b.v.`111001000`.Signal as Carre
import sfr.signals.t_c.c.b.v.`111001000`.Aspect
import sfr.signals.t_c.c.b.v.`111001000`.Reason
import sfr.signals.t_c.c.b.v.`111001000`.Rules
import sfr.signals.uti.sidm.b.z.`200000000`.Markers
import sfr.signals.uti.sidm.b.z.`200000000`.Textures as MarkerTextures

/** Les balises décrivent exclusivement le parcours sélectionné fourni au modèle.
 * Une branche voisine, un catalogue ressemblant ou une limite de capture ne
 * constituent jamais une demande de manœuvre. */
class Signal111001000ManoeuvreTests {
    private val mod = FrenchSignalsMod()
    private val train = 0x5000000010001L
    private val boundary = 0x8000000020001L
    private val markerId = 0x8000000030001L
    private fun marker(catalogue: String, state: Int = 0, offset: Long = 0) =
        SignalRouteMarker(markerId + offset, catalogue, state)
    private fun route(markers: List<SignalRouteMarker>, destination: RouteDestination = RouteDestination.NativeBoundary,
                      occupation: Occupancy = Occupancy.Clear, observedTrain: Long = train) =
        SelectedSignalRoute.observed(observedTrain, boundary, destination, occupation, markers)
    private fun terminal(markers: List<SignalRouteMarker>) = Observation(
        block = Occupancy.Unknown, fresh = true, routeKnown = false, approachingTrain = train,
        observedRoute = route(markers))
    private fun decision(observation: Observation, settings: Map<String, Boolean> = emptyMap(),
                         status: SettingsStatus = SettingsStatus.Present, next: Decision? = null): Decision =
        assertNotNull(mod.decide(Signal(31, if (next == null) 0 else boundary,
            settings, observation, status, Carre.TYPE), next))
    private fun indication(value: Decision) = assertNotNull(mod.indication(value)?.of(Carre.model))
    private fun nextClear() = mod.evaluate(OriginalCarre.TYPE, mapOf("returnToSemaphore" to true),
        Observation(Occupancy.Clear, fresh = true, routeKnown = true, next = OriginalAspect.VL.ordinal))

    @Test fun utilityMarkersHaveTheirOwnSingleStateAndNeverIssueMovementInstructions() {
        assertEquals(listOf(Markers.T32_TYPE, Markers.T33_TYPE), mod.signalTypes.takeLast(2).map { it.id })
        for ((model, texture) in listOf(Markers.t32 to MarkerTextures.MANOEUVRE,
            Markers.t33 to MarkerTextures.MANOEUVRE_CLIGNOTANTE)) {
            val type = model.type
            val construction = assertNotNull(type.construction)
            assertEquals("marker", construction.kind)
            assertEquals(listOf(texture), construction.states)
            assertEquals(4, construction.size)
            assertTrue(construction.left)
            assertTrue(type.checkboxes.isEmpty() && type.numbers.isEmpty() && type.actions.isEmpty())
            assertFalse(type.observeApproach || type.observeRoute || type.observeBlockTrains)
            val value = assertNotNull(mod.decide(Signal(type = type.id), null))
            assertFalse(mod.isActive(value))
            assertFalse(mod.isFault(value))
            assertNull(mod.drivingRule(value))
            assertEquals(texture, mod.texture(value, 0, 500))
            assertEquals(texture, mod.texture(value, 500, 500))
            assertEquals(texture, mod.texture(mod.invalidNetworkDecision(type.id), 0, 500))
        }
        assertEquals(19, MarkerTextures.catalogue.size)
        assertTrue(mod.signalTypes.none { it.textureSet == "sfr_uti_sidm_b_z_200000000" })
    }

    @Test fun aClearQualifiedNativeBoundarySelectsTheExactManoeuvreMarker() {
        val cases = listOf(Markers.T32_TEXTURES to (Aspect.White to Reason.Manoeuvre),
            Markers.T33_TEXTURES to (Aspect.WhiteFlash to Reason.FlashingManoeuvre))
        for ((catalogue, expected) in cases) {
            val observation = terminal(listOf(marker(catalogue)))
            assertFalse(observation.routeKnown) // No downstream protecting signal bounds this extent.
            assertEquals(Occupancy.Unknown, observation.block)
            assertEquals(expected.first, Rules.evaluate(emptyMap(), observation, Aspect.Unknown).aspect)
            val value = decision(observation)
            assertEquals(expected.first, indication(value).aspect)
            assertEquals(expected.second, indication(value).reason)
            assertFalse(mod.isFault(value))
            for (settings in listOf(emptyMap(), mapOf("greenFlashWork" to true),
                mapOf("returnToSemaphore" to true, "forceClosed" to true, "automaticOpening" to true))) {
                assertEquals(expected.first, indication(decision(observation, settings)).aspect)
            }
            assertEquals(expected.first, indication(decision(observation,
                mapOf("forceClosed" to true), SettingsStatus.Absent)).aspect)
        }
    }

    @Test fun anyOtherProtectingSignalKeepsTheNormalBalIndication() {
        val markers = listOf(marker(Markers.T32_TEXTURES), marker(Markers.T33_TEXTURES, offset = 1))
        val observation = terminal(markers).copy(block = Occupancy.Clear, routeKnown = true,
            observedRoute = route(markers, RouteDestination.ProtectingSignal))
        val value = decision(observation, next = nextClear())
        assertEquals(Aspect.VL, indication(value).aspect)
        assertEquals(Reason.Clear, indication(value).reason)
        assertEquals(AutomaticDriving.clear(), mod.drivingRule(value))
        // A real protecting neighbour still needs resolution; a marker cannot skip it.
        assertNull(mod.decide(Signal(31, 32, observation = observation, type = Carre.TYPE), null))
        assertEquals(Aspect.Closed, indication(decision(observation)).aspect)
    }

    @Test fun catalogueNamesAndStatesMustMatchWithoutGuessingFromTheImageOrItsNumber() {
        val notManoeuvre = listOf(
            emptyList(),
            listOf(marker(Markers.T32_TEXTURES, state = 1)),
            listOf(marker(Markers.T33_TEXTURES, state = 3)),
            listOf(marker("foreign_uti_sidm_b_z_200000000_t32")),
            listOf(marker("sfr_uti_sidm_b_z_200000000_t31")),
            listOf(marker("sfr_uti_sidm_b_z_200000000", state = 3)),
            listOf(marker("sfr_uti_sidm_b_z_200000000", state = 4)))
        for (markers in notManoeuvre) {
            val value = decision(terminal(markers))
            assertEquals(Aspect.Closed, indication(value).aspect)
            assertEquals(Reason.RouteUnknown, indication(value).reason)
            assertEquals(AutomaticDriving.stop(), mod.drivingRule(value))
        }
    }

    @Test fun positiveOccupationConflictsAndUnavailableSettingsKeepTheirPriority() {
        for (catalogue in listOf(Markers.T32_TEXTURES, Markers.T33_TEXTURES)) {
            val markers = listOf(marker(catalogue))
            val normal = terminal(markers)
            val refusals = listOf(
                normal.copy(forcedStop = true) to Reason.ForcedStop,
                normal.copy(block = Occupancy.Occupied) to Reason.BlockOccupied,
                normal.copy(observedRoute = route(markers, occupation = Occupancy.Occupied)) to Reason.BlockOccupied,
                normal.copy(observedRoute = route(markers, occupation = Occupancy.Unknown)) to Reason.BlockUnknown) +
                RouteConflict.entries.map { normal.copy(routeConflict = it) to Reason.RouteConflict }
            for ((observation, reason) in refusals) {
                val value = decision(observation)
                assertEquals(Aspect.Closed, indication(value).aspect)
                assertEquals(reason, indication(value).reason)
                assertEquals(AutomaticDriving.stop(), mod.drivingRule(value))
            }
            val unavailable = decision(normal, status = SettingsStatus.Unavailable)
            assertEquals(Aspect.Closed, indication(unavailable).aspect)
            assertEquals(Reason.ObservationUnavailable, indication(unavailable).reason)
            assertTrue(mod.isFault(unavailable))
            val failed = decision(normal.copy(lampFailed = true, forcedStop = true))
            assertEquals(Aspect.Unknown, indication(failed).aspect)
            assertEquals(Reason.LampFailure, indication(failed).reason)
            assertEquals(AutomaticDriving.stop(), mod.drivingRule(failed))
        }
    }

    @Test fun oldPartialOrAnotherTrainsRouteCannotRetainAWhiteIndication() {
        val markers = listOf(marker(Markers.T32_TEXTURES))
        val normal = terminal(markers)
        assertEquals(Aspect.White, indication(decision(normal)).aspect)
        val incomplete = SelectedSignalRoute.observed(train, 0, RouteDestination.Unknown,
            Occupancy.Unknown, markers, complete = false)
        for (lost in listOf(normal.copy(fresh = false), normal.copy(approachingTrain = null),
            normal.copy(approachingTrain = train + 1), normal.copy(approachingTrain = 1),
            normal.copy(observedRoute = SelectedSignalRoute.Unknown), normal.copy(observedRoute = incomplete))) {
            val value = decision(lost)
            assertEquals(Aspect.Closed, indication(value).aspect)
            assertEquals(AutomaticDriving.stop(), mod.drivingRule(value))
        }
        assertEquals(Aspect.White, indication(decision(normal)).aspect) // No remembered refusal or opening.
        // Unknown route data does not disable the pre-existing, separately qualified BAL path.
        val legacy = normal.copy(block = Occupancy.Clear, routeKnown = true,
            observedRoute = SelectedSignalRoute.Unknown)
        assertEquals(Aspect.VL, indication(decision(legacy, next = nextClear())).aspect)
        val occupiedPrefix = SelectedSignalRoute.observed(train, 0, RouteDestination.Unknown,
            Occupancy.Occupied, markers, complete = false)
        val occupied = decision(legacy.copy(observedRoute = occupiedPrefix), next = nextClear())
        assertEquals(Aspect.Closed, indication(occupied).aspect)
        assertEquals(Reason.BlockOccupied, indication(occupied).reason)
        assertEquals(AutomaticDriving.stop(), mod.drivingRule(occupied))
    }

    @Test fun incompatibleManoeuvreRequestsCloseButRepeatedMatchingMarkersRemainValid() {
        val mixed = terminal(listOf(marker(Markers.T32_TEXTURES), marker(Markers.T33_TEXTURES, offset = 1)))
        val refused = decision(mixed)
        assertEquals(Aspect.Closed, indication(refused).aspect)
        assertEquals(Reason.ConflictingManoeuvreMarkers, indication(refused).reason)
        assertTrue(mod.isFault(refused))
        assertEquals(AutomaticDriving.stop(), mod.drivingRule(refused))
        for ((catalogue, aspect) in listOf(Markers.T32_TEXTURES to Aspect.White,
            Markers.T33_TEXTURES to Aspect.WhiteFlash)) {
            assertEquals(aspect, indication(decision(terminal(listOf(marker(catalogue),
                marker(catalogue, offset = 1), marker("foreign-marker", offset = 2))))).aspect)
        }
    }

    @Test fun manoeuvreNeverBecomesAClearAspectForTheUpstreamBalOrAnotherModel() {
        for (catalogue in listOf(Markers.T32_TEXTURES, Markers.T33_TEXTURES)) {
            val observation = terminal(listOf(marker(catalogue)))
            val downstream = decision(observation)
            for (type in listOf(BalSignal.TYPE, OriginalCarre.TYPE, Carre.TYPE)) {
                val input = Signal(21, 31, observation = Observation(Occupancy.Clear,
                    fresh = true, routeKnown = true, approachingTrain = train), type = type)
                val upstream = assertNotNull(mod.decide(input, downstream))
                assertEquals("A", assertNotNull(mod.indication(upstream)).aspect.name)
            }
            for (type in listOf(OriginalCarre.TYPE, SimpleCarre.TYPE)) {
                val other = assertNotNull(mod.decide(Signal(31, observation = observation, type = type), null))
                assertEquals("Closed", assertNotNull(mod.indication(other)).aspect.name)
                assertEquals(AutomaticDriving.stop(), mod.drivingRule(other))
            }
        }
    }

    @Test fun bothWhiteAspectsAllowThirtyFromThisSignalWithoutReplacingNativeAdmission() {
        for (catalogue in listOf(Markers.T32_TEXTURES, Markers.T33_TEXTURES)) {
            val value = decision(terminal(listOf(marker(catalogue))))
            val rule = assertNotNull(mod.drivingRule(value))
            assertEquals(30.0 / 3.6, rule.speedMps)
            assertEquals(0, rule.signalsAhead)
            assertEquals(setOf(DrivingFlag.HoldToClear, DrivingFlag.ApproachPassable), rule.flags)
        }
    }
}
