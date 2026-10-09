package sfr.tests

import sfr.signals.t_a.s.c.v.`011100000`.Signal as T_A_011100000
import sfr.signals.t_a.s.c.v.`011100000`.Panel as Panel011100000
import sfr.signals.t_a.s.c.v.`011100000`.Settings as Settings011100000
import sfr.signals.t_c.c.b.v.`101000000`.Signal as T_C_101000000
import sfr.signals.t_c.c.b.v.`101000000`.Aspect as Aspect101000000
import sfr.signals.t_c.c.b.v.`111000000`.Signal as T_C_111000000

import kotlin.test.*
import nimby.*
import sfr.FrenchSignalsMod
import sfr.signals.t_a.s.c.v.`011100000`.Aspect as Aspect
import sfr.signals.t_a.s.c.v.`011100000`.Reason as Reason
import sfr.signals.t_a.s.c.v.`011100000`.Decision as SignalDecision

class SignalTypesTests {
    private val mod = FrenchSignalsMod()
    private val Decision.balAspect get() = requireNotNull(mod.indication(this)?.of(T_A_011100000.model)).aspect
    private val approaching = 0x5000000010001L
    private fun carre(observation: Observation = Observation(Occupancy.Clear, true, true)) = Signal(
        2, 3, mapOf("active" to true), observation, type = T_C_101000000.TYPE)
    private fun indication(signal: Signal) = mod.indication(mod.decide(signal, null)!!)!!.aspect

    @Test fun oneModDeclaresThreeIndependentTypes() {
        validateSignalTypes(mod.signalTypes)
        assertEquals("signalisationfrancaiserealiste",mod.id)
        assertTrue(mod.signalTypes.none { it.id==mod.id })
        assertEquals(setOf(T_A_011100000.TYPE,T_C_101000000.TYPE,T_C_111000000.TYPE),mod.signalTypes.map { it.id }.toSet())
        // This branch deliberately starts a new-map catalogue, without the old
        // C++ identifier. It must not accidentally restore a retired model.
        assertEquals(listOf("sfr_t_a_s_c_v_011100000", "sfr_t_c_c_b_v_101000000", "sfr_t_c_c_b_v_111000000"), mod.signalTypes.map { it.textureSet })
        assertEquals(listOf("greenFlashBlock", "greenFlashWork", "yellowFlashEnabled", "redFlashEnabled"),
            mod.signalTypes[0].checkboxes.take(4).map { it.name })
        assertEquals(listOf(Panel011100000.workBlocks), mod.signalTypes[0].numbers)
        assertTrue(mod.signalTypes[0].checkboxes.all { !it.defaultValue && !it.onlyWhenEnabled })
        assertTrue(mod.signalTypes[1].checkboxes.single().defaultValue)
        assertTrue(mod.signalTypes[1].observeApproach)
        assertEquals(2, mod.signalTypes[1].approachBlocks)
    }

    @Test fun carreIsClosedAtRestAndOnlyOpensWithObservedApproach() {
        val rest = carre()
        assertEquals(Aspect101000000.Closed, indication(rest))
        val approach = rest.copy(observation = rest.observation.copy(approachingTrain = approaching))
        assertEquals(Aspect101000000.Warning, indication(approach))
        for (o in listOf(
            approach.observation.copy(fresh = false),
            approach.observation.copy(block = Occupancy.Occupied),
            approach.observation.copy(forcedStop = true),
            approach.observation.copy(lampFailed = true),
            approach.observation.copy(approachingTrain = 1)
        )) assertEquals(Aspect101000000.Closed, indication(approach.copy(observation = o)))
        assertEquals(Aspect101000000.Closed, indication(approach.copy(settings = mapOf("active" to false))))
        assertEquals(Aspect101000000.Closed, indication(approach.copy(settingsStatus = SettingsStatus.Unavailable)))
        assertEquals(Aspect101000000.Closed, indication(approach.copy(settingsStatus = SettingsStatus.Absent)))
        // No remembered opening when the observation of the approach disappears.
        assertEquals(Aspect101000000.Closed, indication(rest))
    }

    @Test fun carreAcceptsUnknownDownstreamOnlyWithFreshApproach() {
        val endOfBal = carre(Observation(Occupancy.Unknown, true, false, approachingTrain = approaching))
            .copy(nextSignal = 0)
        assertEquals(Aspect101000000.Warning, indication(endOfBal))
        assertEquals(Aspect101000000.Warning, indication(endOfBal.copy(observation = endOfBal.observation.copy(routeKnown = true))))
        assertEquals(Aspect101000000.Closed, indication(endOfBal.copy(observation = endOfBal.observation.copy(block = Occupancy.Occupied))))
        assertEquals(Aspect101000000.Closed, indication(endOfBal.copy(observation = endOfBal.observation.copy(fresh = false))))
        // Head passage removes the approach, even while its tail remains behind.
        val afterPassage = endOfBal.copy(observation = endOfBal.observation.copy(approachingTrain = null))
        assertEquals(Aspect101000000.Closed, indication(afterPassage))
        assertEquals(Aspect101000000.Warning, indication(afterPassage.copy(observation = afterPassage.observation.copy(approachingTrain = approaching + 1))))
        // The tolerance belongs to this model; it must not leak to BAL.
        assertNotEquals(Aspect.VL, mod.evaluate(emptyMap(), Observation(Occupancy.Unknown, true, false)).balAspect)
    }

    @Test fun balRecognizesTheOtherTypeAndNeverTreatsCarreAsPermissiveStop() {
        val upstream = Signal(1, 2, Settings011100000().asMap(),
            Observation(Occupancy.Clear, true, true), type = T_A_011100000.TYPE)
        assertEquals(listOf(Aspect.A, Aspect101000000.Closed),
            mod.evaluateNetwork(listOf(upstream, carre())).map { mod.indication(it)!!.aspect })
        val reduced = upstream.copy(settings = mapOf("yellowFlashEnabled" to true))
        val open = carre().let { it.copy(observation = it.observation.copy(approachingTrain = approaching)) }
        assertEquals(listOf(Aspect.YellowFlash, Aspect101000000.Warning),
            mod.evaluateNetwork(listOf(reduced, open)).map { mod.indication(it)!!.aspect })
        val closed = mod.decide(carre(), null)!!
        assertEquals(setOf(DrivingFlag.Stop), mod.drivingRule(closed)!!.flags)
        assertEquals("imgs/t_c/c/b/v/101000000/tex01.svg", mod.texture(closed, 0, 500))
        assertEquals("imgs/t_c/c/b/v/101000000/tex03.svg", mod.texture(mod.decide(open, null)!!, 0, 500))
        assertNull(mod.forcedDecision(T_C_101000000.TYPE, Aspect.S.ordinal))
        assertNull(mod.forcedDecision(T_A_011100000.TYPE, 100))
        assertNull(mod.forcedDecision(T_C_101000000.TYPE, Aspect101000000.Closed.ordinal))
    }

    @Test fun headEntryClosesCarreEvenWithAnotherTrainApproachingAndNoExitSignal() {
        val approachingCarre = carre(Observation(Occupancy.Unknown, true, false,
            approachingTrain = approaching)).copy(nextSignal = 0)
        assertEquals(Aspect101000000.Warning, indication(approachingCarre))
        // The leading head is downstream, its tail is still upstream. A second
        // approach must not reopen the signal while the known prefix is occupied.
        val entered = approachingCarre.copy(observation = approachingCarre.observation.copy(
            block = Occupancy.Occupied, approachingTrain = approaching + 1))
        val decision = assertNotNull(mod.decide(entered, null))
        assertEquals(Aspect101000000.Closed, mod.indication(decision)!!.aspect)
        assertEquals(AutomaticDriving.stop(), mod.drivingRule(decision))
        assertEquals(Aspect101000000.Closed, indication(entered.copy(observation =
            entered.observation.copy(approachingTrain = null))))
    }

    @Test fun currentSettingsRoundTripWithoutAnyMigration() {
        for (mask in 0 until 16) {
            val values = Panel011100000.checkboxes.mapIndexed { i, box ->
                box.name to (mask and (1 shl i) != 0)
            }.toMap()
            assertEquals(values, Settings011100000.from(values).asMap())
        }
        assertEquals(Settings011100000(), Settings011100000.from(emptyMap()))
        val clear = Observation(Occupancy.Clear, true, true, next = Aspect.VL.ordinal)
        assertEquals(Aspect.VL, mod.evaluate(emptyMap(), clear).balAspect)
        assertEquals(Aspect.GreenFlash, mod.evaluate(mapOf("greenFlashWork" to true), clear).balAspect)
        assertEquals(Aspect.YellowFlash, mod.evaluate(mapOf("yellowFlashEnabled" to true),
            clear.copy(next = Aspect.A.ordinal)).balAspect)
    }

    @Test fun carreWarningHasTheSameDrivingObligationAsBalWarning() {
        val warning = mod.decide(carre(Observation(Occupancy.Clear, true, true,
            approachingTrain = approaching)), null)!!
        val balWarning = mod.evaluate(T_A_011100000.TYPE, emptyMap(),
            Observation(Occupancy.Clear, true, true, next = Aspect.S.ordinal))
        val rule = assertNotNull(mod.drivingRule(warning))
        assertEquals(mod.drivingRule(balWarning), rule)
        assertEquals(1, rule.signalsAhead)
        assertEquals(0.0, rule.speedMps) // Stop target unless a fresh target instruction changes it.
        assertEquals(30.0 / 3.6, rule.reopenedSpeedMps)
        assertEquals(setOf(DrivingFlag.FollowTarget, DrivingFlag.ApproachPassable), rule.flags)
        assertFalse(DrivingFlag.Clear in rule.flags) // Cannot release a received approach early.
        val closed = mod.decide(carre(), null)!!
        assertEquals(AutomaticDriving.stop(), mod.drivingRule(closed))
    }

    @Test fun balDeclaresItsFlashingFramesToTheSdk() {
        val decision = mod.evaluate(T_A_011100000.TYPE, mapOf("greenFlashWork" to true),
            Observation(Occupancy.Clear, true, true, next = Aspect.VL.ordinal))
        val animation = assertNotNull(mod.animation(decision))
        assertEquals(500L, animation.everyMs)
        assertEquals("imgs/t_a/s/c/v/011100000/tex05.svg", animation.first)
        assertEquals("imgs/t_a/s/c/v/011100000/tex06.svg", animation.alternate)
        assertEquals(animation.alternate, mod.texture(decision, 500, 250))
        val fixed = assertNotNull(mod.animation(mod.decide(carre(), null)!!))
        assertEquals(0L, fixed.everyMs)
        assertEquals(fixed.first, fixed.alternate)
        assertEquals(fixed.first, fixed.frameAt(Long.MAX_VALUE))
    }

}
