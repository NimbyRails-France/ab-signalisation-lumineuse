package sfr.tests

import sfr.signals.t_a.s.c.v.`011100000`.Signal as T_A_011100000
import sfr.signals.t_a.s.c.v.`011100000`.Settings as Settings011100000
import sfr.signals.t_a.s.c.v.`011100000`.Driving as Driving011100000
import sfr.signals.t_c.c.b.v.`101000000`.Signal as T_C_101000000

import nimby.*
import sfr.FrenchSignalsMod
import sfr.signals.t_a.s.c.v.`011100000`.Aspect as Aspect
import sfr.signals.t_a.s.c.v.`011100000`.Reason as Reason
import sfr.signals.t_a.s.c.v.`011100000`.Decision as SignalDecision
import kotlin.math.*

private val decodeMod = FrenchSignalsMod()
private val Decision.typed get() = requireNotNull(decodeMod.indication(this)?.of(T_A_011100000.model))
private val Decision.indication get() = typed.aspect
private val Decision.motif get() = typed.reason
private fun signal(id: Long, next: Long = 0, block: Occupancy = Occupancy.Clear) = Signal(
    id, next, Settings011100000().asMap(), Observation(block, true, true))

internal fun testNetworkAndInstructions() {
    val mod = FrenchSignalsMod()
    val chain = listOf(signal(1,2),signal(2,3),signal(3))
    expect(mod.evaluateNetwork(chain).all { it.indication == Aspect.Unknown })
    val occupied = mod.evaluateNetwork(chain.map { if (it.id == 3L) it.copy(observation = it.observation.copy(block = Occupancy.Occupied)) else it })
    expect(occupied.map { it.indication } == listOf(Aspect.VL,Aspect.A,Aspect.S))
    val reduced = chain.map { if (it.id == 1L) it.copy(settings = Settings011100000(yellowFlashEnabled = true).asMap())
        else if (it.id == 3L) it.copy(observation = it.observation.copy(block = Occupancy.Occupied)) else it }
    expect(mod.evaluateNetwork(reduced).map { it.indication } == listOf(Aspect.YellowFlash,Aspect.A,Aspect.S))
    expect(mod.evaluateNetwork(listOf(signal(1,99))).single().motif == Reason.InvalidTopology)
    expect(mod.evaluateNetwork(listOf(signal(1,2),signal(2,1))).all { it.motif == Reason.InvalidTopology })
    expect(mod.evaluateNetwork(listOf(signal(1,2),signal(2,1,Occupancy.Occupied))).map { it.indication } == listOf(Aspect.A,Aspect.S))
    rejected { mod.evaluateNetwork(listOf(signal(1),signal(1))) }
    rejected { mod.evaluateNetwork(listOf(signal(0))) }
    rejected { mod.evaluateNetwork(List(4097) { signal(it.toLong()+1) }) }
    expect(mod.evaluateNetwork(List(4096) { signal(it.toLong()+1,if(it==4095)0 else it.toLong()+2) }).all { it.indication == Aspect.Unknown })
    expect(mod.decide(signal(1).copy(settingsStatus = SettingsStatus.Absent),null)?.indication == Aspect.Unknown)
    expect(mod.decide(signal(1).copy(settingsStatus = SettingsStatus.Unavailable),null)?.motif == Reason.ObservationUnavailable)
    val red = signal(1,block=Occupancy.Occupied).copy(settings=Settings011100000(redFlashEnabled=true).asMap())
    expect(mod.decide(red,null)?.indication == Aspect.RedFlash)
    val instruction = Driving011100000.fromDecision(SignalDecision(Aspect.YellowFlash,Reason.ReducedAnnouncement))!!
    expect(instruction.signalsAhead == 2 && instruction.flags == setOf(DrivingFlag.FollowTarget,DrivingFlag.CancelAtNextClear,DrivingFlag.ApproachPassable))
    val warning = Driving011100000.fromDecision(SignalDecision(Aspect.A,Reason.StopAnnouncement))!!
    expect(warning.signalsAhead == 1 && warning.reopenedSpeedMps == 30.0/3.6)
    expect(warning.flags == setOf(DrivingFlag.FollowTarget,DrivingFlag.ApproachPassable))
    // The generic API has no BAL speed; a consumer supplies its own policy.
    expect(DrivingRule().reopenedSpeedMps == 0.0)
    expect(AutomaticDriving.announceStop(1,17.0/3.6,passableHere=false).reopenedSpeedMps == 17.0/3.6)
    rejected { AutomaticDriving.announceStop(1,Double.NaN,passableHere=true) }
    rejected { AutomaticDriving.announceStop(1,10.0,passableHere=true,cancelAtNextClear=true) }
    rejected { AutomaticDriving.restrictedUntilNextSignal(10.0,5.0,stopFirst=false) }
    expect(Driving011100000.fromDecision(SignalDecision(Aspect.S,Reason.BlockOccupied))!!.flags == setOf(DrivingFlag.Stop,DrivingFlag.OnSight,DrivingFlag.StopThenProceed))
    expect(Driving011100000.fromDecision(SignalDecision(Aspect.S,Reason.ForcedStop))!!.flags == setOf(DrivingFlag.Stop))
    expect(Driving011100000.fromDecision(SignalDecision(Aspect.S,Reason.RouteUnknown))!!.flags == setOf(DrivingFlag.Stop))
    expect(Driving011100000.fromDecision(SignalDecision(Aspect.Unknown,Reason.MissingObservation))!!.flags == setOf(DrivingFlag.Stop))
    expect(Driving011100000.fromDecision(SignalDecision(Aspect.RedFlash,Reason.BlockOccupied))!!.speedMps == 15.0/3.6)
}
