package sfr.tests

import sfr.signals.t_a.s.c.v.`011100000`.Settings as Settings011100000
import sfr.signals.t_a.s.c.v.`011100000`.Textures as Textures011100000
import sfr.signals.t_c.c.b.v.`101000000`.Signal as T_C_101000000

import nimby.*
import sfr.FrenchSignalsMod
import sfr.signals.t_a.s.c.v.`011100000`.Aspect as Aspect
import sfr.signals.t_a.s.c.v.`011100000`.Reason as Reason
import sfr.signals.t_a.s.c.v.`011100000`.Decision as SignalDecision
import kotlin.math.*

internal fun test011100000Rules() {
    var s = Settings011100000()
    var o = Observation(Occupancy.Occupied, true, true, next = Aspect.VL.ordinal)
    expect(evaluate011100000Fixture(s,o).aspect == Aspect.S)
    o = o.copy(block = Occupancy.Clear, next = Aspect.S.ordinal)
    expect(evaluate011100000Fixture(s,o).aspect == Aspect.A)
    o = o.copy(next = Aspect.A.ordinal)
    expect(evaluate011100000Fixture(s,o).aspect == Aspect.VL)
    s = s.copy(yellowFlashEnabled = true)
    expect(evaluate011100000Fixture(s,o).aspect == Aspect.YellowFlash)
    s = s.copy(yellowFlashEnabled = false, greenFlashBlock = true)
    expect(evaluate011100000Fixture(s,o).aspect == Aspect.GreenFlash)
    expect(evaluate011100000Fixture(s,o.copy(next = Aspect.YellowFlash.ordinal)).aspect == Aspect.GreenFlash)
    s = s.copy(greenFlashWork = true)
    expect(evaluate011100000Fixture(s,o.copy(next = Aspect.VL.ordinal)).aspect == Aspect.GreenFlash)
    expect(evaluate011100000Fixture(s,o.copy(next = Aspect.S.ordinal)).aspect == Aspect.A)
    o = o.copy(block = Occupancy.Occupied, redFlashCondition = true)
    expect(evaluate011100000Fixture(s,o).aspect == Aspect.S)
    s = s.copy(redFlashEnabled = true)
    expect(evaluate011100000Fixture(s,o).aspect == Aspect.RedFlash)
    expect(evaluate011100000Fixture(s,o.copy(forcedStop = true)).aspect == Aspect.S)
    expect(evaluate011100000Fixture(s,o.copy(fresh = false)).aspect == Aspect.Unknown)
    expect(evaluate011100000Fixture(s,o.copy(lampFailed = true)).reason == Reason.LampFailure)
    expect(evaluate011100000Fixture(s,o.copy(routeKnown = false)).reason == Reason.InvalidEquipment)
    val base = Observation(Occupancy.Clear, true, true)
    s = Settings011100000()
    expect(evaluate011100000Fixture(s,base).reason == Reason.DownstreamUnknown)
    expect(evaluate011100000Fixture(s,base.copy(block = Occupancy.Unknown)).reason == Reason.BlockUnknown)
    expect(evaluate011100000Fixture(s,base.copy(routeKnown = false)).reason == Reason.RouteUnknown)
    expect(evaluate011100000Fixture(s,base.copy(fresh = false)).reason == Reason.ObservationUnavailable)
    rejected { FrenchSignalsMod().evaluate(s.asMap(), Observation(next = 99)) }
    for (a in Aspect.entries) {
        expect(Textures011100000.forAspect(a).frameAt(0).isNotEmpty())
        rejected { Textures011100000.forAspect(a).frameAt(-1) }
    }
    expect(Textures011100000.forAspect(Aspect.GreenFlash).frameAt(0).endsWith("tex05.svg"))
    expect(Textures011100000.forAspect(Aspect.GreenFlash).frameAt(500).endsWith("tex06.svg"))
    expect(Textures011100000.forAspect(Aspect.YellowFlash).frameAt(500).endsWith("tex08.svg"))
    expect(Textures011100000.forAspect(Aspect.RedFlash).frameAt(0).endsWith("tex09.svg"))
    expect(Textures011100000.forAspect(Aspect.RedFlash).frameAt(500).endsWith("tex10.svg"))
}
