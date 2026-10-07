@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package sfr.tests

import kotlinx.cinterop.*
import kotlin.test.*
import nimby.*
import sfr.FrenchSignalsMod
import sfr.signals.bal.*
import sfr.signals.carreavertissement.*
import sfr.signals.carrebal.CarreBal

private enum class ForeignAspect { Open }
private enum class ForeignReason { Normal }

class ModelIsolationTests {
    private val mod = FrenchSignalsMod()

    @Test fun sdkContextSuppliesSettingsDefaultsAndUnavailableObservation() {
        var observed: SignalRuleContext? = null
        val model = signalModel("context", "Context", "context-textures",
            Indication(ForeignAspect.Open, ForeignReason.Normal)) {
            val option = checkbox("enabled", "Enabled", defaultValue = true)
            rules {
                observed = this
                assertEquals(settings[option.name] ?: option.defaultValue, enabled(option))
                Indication(ForeignAspect.Open, ForeignReason.Normal)
            }
            images { "context.svg" }
        }
        val contextMod = signalMod("contexts", "Contexts") { signal(model) }
        val input = Signal(type = model.type.id, settings = mapOf("enabled" to false),
            observation = Observation(Occupancy.Clear, true, true), settingsStatus = SettingsStatus.Absent)
        contextMod.decide(input, null)
        assertEquals(true, observed!!.settings["enabled"])
        assertTrue(observed!!.fresh)
        assertEquals(SettingsStatus.Absent, observed!!.settingsStatus)
        assertEquals(false, input.settings["enabled"]) // Caller input was not mutated.
        contextMod.decide(input.copy(settingsStatus = SettingsStatus.Unavailable), null)
        assertFalse(observed!!.fresh)
        assertFalse(observed!!.observation.fresh)
        assertFalse(observed!!.signal.observation.fresh)
        assertEquals(SettingsStatus.Unavailable, observed!!.settingsStatus)
        contextMod.decide(input.copy(settingsStatus = SettingsStatus.Present), null)
        assertEquals(false, observed!!.settings["enabled"])
        assertTrue(observed!!.fresh)
    }

    @Test fun sameLocalCodeKeepsItsModelAndCannotBeCastToAnother() {
        assertEquals(BalAspect.Unknown.ordinal, CarreAspect.Closed.ordinal)
        val bal = mod.unknownDecision(BalSignals.TYPE)
        val carre = mod.unknownDecision(CarreAvertissement.TYPE)
        assertNotEquals(bal, carre)
        assertEquals(BalAspect.Unknown, mod.indication(bal)!!.of(BalSignals.model)!!.aspect)
        assertEquals(CarreAspect.Closed, mod.indication(carre)!!.of(CarreAvertissement.model)!!.aspect)
        assertNull(mod.indication(bal)!!.of(CarreAvertissement.model))
        assertNull(mod.indication(carre)!!.of(BalSignals.model))
        assertNull(mod.indication(Decision(bal.aspect, carre.reason)))
        assertFailsWith<IllegalArgumentException> { mod.texture(Decision(bal.aspect, carre.reason), 0, 500) }
        val impostor = signalModel(BalSignals.TYPE, "Other", "other-textures",
            Indication(ForeignAspect.Open, ForeignReason.Normal)) {
            rules { Indication(ForeignAspect.Open, ForeignReason.Normal) }; images { "other.svg" }
        }
        assertNull(mod.indication(bal)!!.of(impostor)) // Same ID does not prove Kotlin type identity.
        assertFailsWith<IllegalArgumentException> {
            signalMod("duplicate", "Duplicate") { signal(BalSignals.model); signal(impostor) }
        }
    }

    @Test fun newNeighbourNeedsAnExplicitBalInterpretation() {
        val foreign = signalModel("foreign", "Foreign", "foreign-textures",
            Indication(ForeignAspect.Open, ForeignReason.Normal)) {
            rules { Indication(ForeignAspect.Open, ForeignReason.Normal) }; images { "foreign.svg" }
        }
        val combined = signalMod("mixed", "Mixed") { signal(BalSignals.model); signal(foreign) }
        val result = combined.evaluateNetwork(listOf(
            Signal(1, 2, observation = Observation(Occupancy.Clear, true, true), type = BalSignals.TYPE),
            Signal(2, type = foreign.type.id)
        ))
        assertEquals(BalReason.DownstreamUnknown, combined.indication(result[0])!!.of(BalSignals.model)!!.reason)
    }

    @Test fun balCanReadAnUnrelatedModelsDeclaredInstructionThroughSdkNeighbour() {
        var seen: SignalNeighbour? = null
        val foreign = signalModel("foreign-stop", "Foreign", "foreign-stop-textures",
            Indication(ForeignAspect.Open, ForeignReason.Normal)) {
            rules { Indication(ForeignAspect.Open, ForeignReason.Normal) }
            images { "foreign.svg" }
            // The name Open has no shared meaning. The instruction is authoritative.
            driving { AutomaticDriving.stop() }
        }
        val inspector = signalModel("inspector", "Inspector", "inspector-textures",
            Indication(ForeignAspect.Open, ForeignReason.Normal)) {
            rules {
                if (next == null) null else {
                    seen = next
                    Indication(ForeignAspect.Open, ForeignReason.Normal)
                }
            }
            images { "inspector.svg" }
        }
        val combined = signalMod("mixed", "Mixed") { signal(BalSignals.model); signal(foreign); signal(inspector) }
        val result = combined.evaluateNetwork(listOf(
            Signal(1, 2, observation = Observation(Occupancy.Clear, true, true), type = BalSignals.TYPE),
            Signal(2, type = foreign.type.id), Signal(3, 2, type = inspector.type.id)
        ))
        assertEquals(BalAspect.A, combined.indication(result[0])!!.of(BalSignals.model)!!.aspect)
        assertEquals(2L, seen!!.id)
        assertEquals(foreign.type, seen!!.type)
        assertEquals(AutomaticDriving.stop(), seen!!.drivingRule)
        assertEquals(ForeignAspect.Open, seen!!.of(foreign)!!.aspect)
        assertNull(seen!!.of(BalSignals.model))
    }

    @Test fun mixedCyclesAndMissingLinksUseEachModelsOwnFallback() {
        val first = signalModel("first", "First", "first-textures",
            BalDecision(BalAspect.Unknown, BalReason.InvalidTopology)) {
            rules { null }; images { "first.svg" }
        }
        val second = signalModel("second", "Second", "second-textures",
            CarreDecision(CarreAspect.Closed, CarreReason.InvalidTopology)) {
            rules { null }; images { "second.svg" }
        }
        val mixed = signalMod("mixed", "Mixed") { signal(first); signal(second) }
        for (signals in listOf(
            listOf(Signal(1, 2, type = "first"), Signal(2, 1, type = "second")),
            listOf(Signal(1, 99, type = "first"), Signal(2, 99, type = "second"))
        )) {
            val results = mixed.evaluateNetwork(signals)
            assertEquals(BalReason.InvalidTopology, mixed.indication(results[0])!!.of(first)!!.reason)
            assertEquals(CarreReason.InvalidTopology, mixed.indication(results[1])!!.of(second)!!.reason)
            assertEquals(listOf("first.svg", "second.svg"), results.map { mixed.texture(it, 0, 500) })
        }
    }

    @Test fun nativeExportsKeepModelFallbackAndRecipeCodes() = memScoped {
        assertEquals(8, nimby.internal.version()) // Numeric settings and network preparation require the matching adapter.
        val raw = allocArray<IntVar>(2)
        val local = allocArray<IntVar>(2)
        for ((index, model) in listOf(BalSignals.model, CarreAvertissement.model, CarreBal.model).withIndex()) {
            assertEquals(0, nimby.internal.fallbackType(index, 1, raw))
            val expected = mod.invalidNetworkDecision(model.type.id)
            assertEquals(expected, Decision(raw[0], raw[1]))
            assertEquals(0, nimby.internal.localDecision(raw[0], raw[1], local))
            assertEquals(mod.indication(expected)!!.aspect.ordinal, local[0])
            assertEquals(mod.indication(expected)!!.reason.ordinal, local[1])
            assertEquals(1, nimby.internal.forceType(index, 0, raw)) // SFR no longer accepts forced states.
        }
        assertTrue(nimby.internal.fallbackType(3, 1, raw) < 0)
        assertTrue(nimby.internal.fallbackType(0, 2, raw) < 0)
        assertTrue(nimby.internal.localDecision(0, 0, local) < 0)
        val first = allocArray<ByteVar>(96)
        val alternate = allocArray<ByteVar>(96)
        val period = alloc<LongVar>()
        val flash = mod.evaluate(BalSignals.TYPE, mapOf("greenFlashWork" to true),
            Observation(Occupancy.Clear, true, true, next = BalAspect.VL.ordinal))
        assertEquals(0, nimby.internal.textureAnimation(flash.aspect, flash.reason, first, alternate, 96, period.ptr))
        assertEquals("imgs/ca/sem_bal/tex05.svg", first.toKString())
        assertEquals("imgs/ca/sem_bal/tex06.svg", alternate.toKString())
        assertEquals(500L, period.value)
        assertTrue(nimby.internal.textureAnimation(flash.aspect, flash.reason, first, alternate, 1, period.ptr) < 0)
        val fixed = mod.unknownDecision(CarreAvertissement.TYPE)
        assertEquals(0, nimby.internal.textureAnimation(fixed.aspect, fixed.reason, first, alternate, 96, period.ptr))
        assertEquals("imgs/cc/cs_a/tex01.svg", first.toKString())
        assertEquals(first.toKString(), alternate.toKString())
        assertEquals(0L, period.value)
    }
}
