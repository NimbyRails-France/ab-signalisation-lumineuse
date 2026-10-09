@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package sfr.tests

import kotlinx.cinterop.*
import kotlin.test.*
import nimby.*
import sfr.FrenchSignalsMod
import sfr.signals.t_a.s.c.v.`011100000`.Signal as T_A_011100000
import sfr.signals.t_a.s.c.v.`011100000`.Aspect as Aspect011100000
import sfr.signals.t_a.s.c.v.`011100000`.Reason as Reason011100000
import sfr.signals.t_a.s.c.v.`011100000`.Panel as Panel011100000

class LargeNetworkTests {
    private val mod = FrenchSignalsMod()
    private fun chain(count: Int) = List(count) { i -> Signal(i + 1L, if(i + 1 == count) 0 else i + 2L,
        observation = Observation(if(i + 1 == count) Occupancy.Occupied else Occupancy.Clear, true, true),
        settingsStatus = SettingsStatus.Absent, type = T_A_011100000.TYPE) }

    @Test fun fourThousandSignalsKeepStopsWarningsAndInputOrder() {
        val input = chain(4096)
        val result = mod.evaluateNetwork(input).map { mod.indication(it)!!.of(T_A_011100000.model)!!.aspect }
        assertEquals(4096, result.size)
        assertTrue(result.take(4094).all { it == Aspect011100000.VL })
        assertEquals(listOf(Aspect011100000.A, Aspect011100000.S), result.takeLast(2))
        assertEquals(result.reversed(), mod.evaluateNetwork(input.reversed()).map { mod.indication(it)!!.of(T_A_011100000.model)!!.aspect })
        assertFailsWith<IllegalArgumentException> { mod.evaluateNetwork(chain(4097)) }
        val cycle = input.map { it.copy(nextSignal = if(it.id == 4096L) 1 else it.nextSignal, observation = Observation(Occupancy.Clear, true, true)) }
        assertTrue(mod.evaluateNetwork(cycle).all { mod.indication(it)!!.of(T_A_011100000.model)!!.reason == Reason011100000.InvalidTopology })
    }

    @Test fun largeWorkZonesStayBoundedAndDoNotChangeTheSavedInput() {
        val input = chain(4096).map { if(it.id % 64 != 1L) it else it.copy(settingsStatus = SettingsStatus.Present,
            settings = Panel011100000.workBlocks.withValue(mapOf("greenFlashWork" to true), 64)) }
        val prepared = mod.prepareObservedNetwork(input)
        assertTrue(prepared.all { it.settings["greenFlashWork"] == true })
        assertEquals(64, input.count { it.settings["greenFlashWork"] == true })
        assertEquals(listOf(Aspect011100000.A, Aspect011100000.S), mod.evaluateNetwork(input).takeLast(2).map { mod.indication(it)!!.of(T_A_011100000.model)!!.aspect })
    }

    @Test fun nativePreparationAccepts4096AndRejectsOversizeBeforeDereferencing() = memScoped {
        assertEquals(4096, nimby.internal.networkLimit())
        val count = 4096
        val ids = allocArray<LongVar>(count * 4); val fields = allocArray<IntVar>(count * 9)
        val masks = allocArray<LongVar>(count); val statuses = allocArray<IntVar>(count)
        for(i in 0 until count) {
            ids[i*4] = i+1L; ids[i*4+1] = if(i+1 == count) 0 else i+2L; ids[i*4+2] = 0; ids[i*4+3] = 0
            for(j in 0 until 9) fields[i*9+j] = 0
            fields[i*9+1] = SettingsStatus.Absent.ordinal; fields[i*9+2] = Occupancy.Clear.ordinal
            fields[i*9+3] = 1; fields[i*9+4] = 1
        }
        assertEquals(0, nimby.internal.prepareNetwork(count, ids, fields, masks, statuses))
        assertTrue((0 until count).all { masks[it] == 0L && statuses[it] == SettingsStatus.Absent.ordinal })
        assertTrue(nimby.internal.prepareNetwork(count+1, null, null, null, null) < 0)
        ids[0] = ids[4]
        assertTrue(nimby.internal.prepareNetwork(count, ids, fields, masks, statuses) < 0)
    }
}
