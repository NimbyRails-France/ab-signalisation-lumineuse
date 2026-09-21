package sfr.tests

import nimby.*
import sfr.FrenchSignalsMod
import sfr.settings.SignalSettings
import sfr.signalling.*
import sfr.rendering.SignalTextures
import sfr.driving.*
import kotlin.math.*

private val Decision.indication get() = Aspect.entries[aspect]
private val Decision.motif get() = Reason.entries[reason]
private fun signal(id: Long, next: Long = 0, block: Occupancy = Occupancy.Clear) = Signal(
    id, next, SignalSettings(active = true).asMap(), Observation(block, true, true, next = Aspect.VL.ordinal))

internal fun testNetworkAndInstructions() {
    val mod = FrenchSignalsMod()
    val chain = listOf(signal(1,2),signal(2,3),signal(3))
    expect(mod.evaluateNetwork(chain).all { it.indication == Aspect.VL })
    val occupied = mod.evaluateNetwork(chain.map { if (it.id == 3L) it.copy(observation = it.observation.copy(block = Occupancy.Occupied)) else it })
    expect(occupied.map { it.indication } == listOf(Aspect.VL,Aspect.A,Aspect.S))
    val reduced = chain.map { if (it.id == 1L) it.copy(settings = SignalSettings(active = true, yellowFlash = true, reducedAnnouncement = true).asMap())
        else if (it.id == 3L) it.copy(observation = it.observation.copy(block = Occupancy.Occupied)) else it }
    expect(mod.evaluateNetwork(reduced).map { it.indication } == listOf(Aspect.YellowFlash,Aspect.A,Aspect.S))
    expect(mod.evaluateNetwork(listOf(signal(1,99))).single().motif == Reason.InvalidTopology)
    expect(mod.evaluateNetwork(listOf(signal(1,2),signal(2,1))).all { it.motif == Reason.InvalidTopology })
    expect(mod.evaluateNetwork(listOf(signal(1,2),signal(2,1,Occupancy.Occupied))).map { it.indication } == listOf(Aspect.A,Aspect.S))
    expect(mod.evaluateNetwork(listOf(signal(1,999).copy(settings = SignalSettings(active = true,endOfBal = true).asMap()))).single().indication == Aspect.VL)
    rejected { mod.evaluateNetwork(listOf(signal(1),signal(1))) }
    rejected { mod.evaluateNetwork(listOf(signal(0))) }
    rejected { mod.evaluateNetwork(List(513) { signal(it.toLong()+1) }) }
    expect(mod.evaluateNetwork(List(512) { signal(it.toLong()+1,if(it==511)0 else it.toLong()+2) }).all { it.indication == Aspect.VL })
    expect(mod.decide(signal(1).copy(settingsStatus = SettingsStatus.Absent),null)?.indication == Aspect.Inactive)
    expect(mod.decide(signal(1).copy(settingsStatus = SettingsStatus.Unavailable),null)?.motif == Reason.ObservationUnavailable)
    val red = signal(1,block=Occupancy.Occupied).copy(settings=SignalSettings(active=true,redFlash=true,redFlashUseDeclared=true,redFlashConditionActive=true).asMap())
    expect(mod.decide(mod.fromLive(red),null)?.indication == Aspect.RedFlash)
    val instruction = DrivingInstructions.fromDecision(SignalDecision(Aspect.YellowFlash,Reason.ReducedAnnouncement))!!
    expect(instruction.signalsAhead == 2 && instruction.flags == setOf(DrivingFlag.FollowTarget,DrivingFlag.CancelAtNextClear))
    expect(DrivingInstructions.fromDecision(SignalDecision(Aspect.S,Reason.BlockOccupied))!!.flags == setOf(DrivingFlag.Stop,DrivingFlag.OnSight,DrivingFlag.StopThenProceed))
    expect(DrivingInstructions.fromDecision(SignalDecision(Aspect.S,Reason.ForcedStop))!!.flags == setOf(DrivingFlag.Stop))
    expect(DrivingInstructions.fromDecision(SignalDecision(Aspect.S,Reason.RouteUnknown))!!.flags == setOf(DrivingFlag.Stop))
    expect(DrivingInstructions.fromDecision(SignalDecision(Aspect.Unknown,Reason.MissingObservation))!!.flags == setOf(DrivingFlag.Stop))
    expect(DrivingInstructions.fromDecision(SignalDecision(Aspect.RedFlash,Reason.BlockOccupied))!!.speedMps == 15.0/3.6)
    expect(DrivingInstructions.fromDecision(SignalDecision(Aspect.Inactive,Reason.Inactive)) == null)
}
