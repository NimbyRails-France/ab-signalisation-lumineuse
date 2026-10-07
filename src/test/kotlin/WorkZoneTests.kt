@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package sfr.tests

import kotlin.test.*
import nimby.*
import sfr.FrenchSignalsMod
import sfr.signals.bal.*
import sfr.signals.carreavertissement.CarreAvertissement
import kotlinx.cinterop.*
import kotlin.random.Random

class WorkZoneTests {
    private val mod = FrenchSignalsMod()
    private fun chain(count: Int = 8) = (1..count).map { Signal(it.toLong(), if(it == count) 0 else it+1L,
        emptyMap(), Observation(if(it == count) Occupancy.Occupied else Occupancy.Clear, true, true), type = BalSignals.TYPE) }
    private fun List<Signal>.source(id: Long, count: Int, enabled: Boolean = true) = map {
        if(it.id != id) it else it.copy(settings = BalPanel.workBlocks.withValue(it.settings + ("greenFlashWork" to enabled), count)) }
    private fun List<Signal>.covered() = mod.prepareObservedNetwork(this).filter { it.settings["greenFlashWork"] == true }.map { it.id }.toSet()

    @Test fun exactCountRemovalOverlapAndOrder() {
        val base = chain()
        assertEquals(setOf(1L), base.source(1,0).covered())
        assertEquals(setOf(1L,2L,3L), base.source(1,2).covered())
        assertEquals(setOf(1L,2L), base.source(1,1).covered())
        assertEquals(emptySet(), base.source(1,4,false).covered())
        val overlapping = base.source(1,2).source(3,2)
        assertEquals(setOf(1L,2L,3L,4L,5L), overlapping.covered())
        assertEquals(overlapping.covered(), overlapping.reversed().covered())
        assertTrue(base.all { it.settings.isEmpty() })
        assertEquals(setOf(3L,4L,5L), overlapping.source(1,2,false).covered())
    }
    @Test fun derivedValuesDoNotBecomeNewSourcesAndAbsentProfilesUseDefaults() {
        val input = chain().source(1,1).map { if(it.id==2L) it.copy(
            settings=BalPanel.workBlocks.withValue(emptyMap(),64)) else it }
        assertEquals(setOf(1L,2L), input.covered())
        val absent = chain().source(1,2).map { if(it.id==2L) it.copy(settingsStatus=SettingsStatus.Absent) else it }
        val result = mod.evaluateNetwork(absent)
        assertEquals(BalReason.Work160, mod.indication(result[1])!!.of(BalSignals.model)!!.reason)
    }
    @Test fun unknownTopologyUnavailableSettingsAndCyclesStopTraversal() {
        val base = chain().source(1,64)
        for (broken in listOf(
            base.map { if(it.id==2L) it.copy(nextSignal=999) else it },
            base.map { if(it.id==2L) it.copy(nextSignal=1) else it },
            base.map { if(it.id==2L) it.copy(observation=it.observation.copy(routeKnown=false)) else it },
            base.map { if(it.id==2L) it.copy(observation=it.observation.copy(fresh=false)) else it }))
            assertEquals(setOf(1L,2L), broken.covered())
        assertEquals(setOf(1L), base.map { if(it.id==2L) it.copy(settingsStatus=SettingsStatus.Unavailable) else it }.covered())
        assertFailsWith<IllegalArgumentException> { mod.prepareObservedNetwork(base+base.first()) }
    }
    @Test fun stopsAndAnnouncementsRemainMoreRestrictive() {
        val results = mod.evaluateNetwork(chain().source(1,64)).map { mod.indication(it)!!.of(BalSignals.model)!! }
        assertTrue(results.take(6).all { it.aspect==BalAspect.GreenFlash && it.reason==BalReason.Work160 })
        assertEquals(BalAspect.A,results[6].aspect)
        assertEquals(BalAspect.S,results[7].aspect)
        val forced = chain().source(1,3).map { if(it.id==2L) it.copy(observation=it.observation.copy(forcedStop=true)) else it }
        assertEquals(BalAspect.S,mod.indication(mod.evaluateNetwork(forced)[1])!!.of(BalSignals.model)!!.aspect)
    }
    @Test fun boundsAndConstructionDefaults() {
        for(value in listOf(0,1,2,31,63,64)) assertEquals(value,BalPanel.workBlocks.read(BalPanel.workBlocks.withValue(emptyMap(),value)))
        assertFailsWith<IllegalArgumentException> { BalPanel.workBlocks.withValue(emptyMap(),65) }
        assertFailsWith<IllegalArgumentException> { BalPanel.workBlocks.withValue(emptyMap(),-1) }
        assertTrue(mod.signalTypes.all { it.construction!!.size==4 })
        assertTrue(mod.signalTypes.all { it.construction!!.left })
    }

    @Test fun overlappingSourcesMatchIndependentBoundedWalksAcrossBrokenRoutes() {
        val random = Random(8127)
        repeat(120) {
            val input = chain(96).map { signal -> signal.copy(
                nextSignal = random.nextInt(0, 102).toLong(),
                settings = BalPanel.workBlocks.withValue(mapOf("greenFlashWork" to (random.nextInt(4) == 0)), random.nextInt(65)),
                settingsStatus = SettingsStatus.entries[random.nextInt(SettingsStatus.entries.size)],
                observation = signal.observation.copy(fresh = random.nextInt(10) != 0, routeKnown = random.nextInt(10) != 0),
                type = if (random.nextInt(10) == 0) CarreAvertissement.TYPE else BalSignals.TYPE)
            }
            val byId = input.associateBy { it.id }
            val expected = input.filter { it.settings["greenFlashWork"] == true }.map { it.id }.toMutableSet()
            for (source in input.filter { it.type == BalSignals.TYPE && it.settingsStatus == SettingsStatus.Present && it.settings["greenFlashWork"] == true }) {
                var cursor = source
                val visited = mutableSetOf(source.id)
                for (step in 0 until BalPanel.workBlocks.read(source.settings)) {
                    if (!cursor.observation.fresh || !cursor.observation.routeKnown || cursor.settingsStatus == SettingsStatus.Unavailable) break
                    val next = byId[cursor.nextSignal] ?: break
                    if (next.type != BalSignals.TYPE || next.settingsStatus == SettingsStatus.Unavailable || !visited.add(next.id)) break
                    expected.add(next.id)
                    cursor = next
                }
            }
            assertEquals(expected, input.covered())
            assertEquals(expected, input.reversed().covered())
        }
    }

    @Test fun denseSavedSourcesAreNotCopied() {
        val input = chain(4096).source(1, 64).map { it.copy(
            settings = BalPanel.workBlocks.withValue(mapOf("greenFlashWork" to true), 64)) }
        val prepared = mod.prepareObservedNetwork(input)
        input.indices.forEach { assertSame(input[it], prepared[it]) }
    }
    @Test fun nativeNetworkPreparationPreservesInputsAndEncodesDerivedSettings() = memScoped {
        val input = chain(4).source(1,2).map { if(it.id==2L) it.copy(settingsStatus=SettingsStatus.Absent) else it }
        val declaration = mod.signalTypes.first { it.id==BalSignals.TYPE }
        val typeIndex = mod.signalTypes.indexOf(declaration)
        val ids = allocArray<LongVar>(16); val fields = allocArray<IntVar>(36)
        val masks = allocArray<LongVar>(4); val statuses = allocArray<IntVar>(4)
        input.forEachIndexed { i, s ->
            ids[i*4]=s.id;ids[i*4+1]=s.nextSignal;ids[i*4+2]=declaration.checkboxes.foldIndexed(0L) { bit,mask,box ->
                if(s.settings[box.name] ?: box.defaultValue) mask or (1L shl bit) else mask };ids[i*4+3]=0
            val values=listOf(typeIndex,s.settingsStatus.ordinal,s.observation.block.ordinal,1,1,0,0,0,0)
            values.forEachIndexed { j,v -> fields[i*9+j]=v }
        }
        val before=List(16){ids[it]}
        assertEquals(0,nimby.internal.prepareNetwork(4,ids,fields,masks,statuses))
        assertEquals(before,List(16){ids[it]})
        val workBit=1L shl declaration.checkboxes.indexOfFirst { it.name=="greenFlashWork" }
        assertEquals(listOf(true,true,true,false),List(4){masks[it] and workBit != 0L})
        assertEquals(SettingsStatus.Present.ordinal,statuses[1])
        assertEquals(0,nimby.internal.prepareNetwork(0,null,null,null,null))
        assertTrue(nimby.internal.prepareNetwork(513,null,null,null,null)<0)
    }
}
