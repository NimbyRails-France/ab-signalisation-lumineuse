package sfr.tests

import sfr.signals.bal.*
import sfr.signals.carreavertissement.CarreAvertissement

import nimby.*
import sfr.FrenchSignalsMod
import sfr.signals.bal.BalSettings
import sfr.signals.bal.BalAspect as Aspect
import sfr.signals.bal.BalReason as Reason
import sfr.signals.bal.BalDecision as SignalDecision
import kotlin.math.*

internal fun testBalRules() {
    var s = BalSettings()
    var o = Observation(Occupancy.Occupied, true, true, next = Aspect.VL.ordinal)
    expect(evaluateBalFixture(s,o).aspect == Aspect.S)
    o = o.copy(block = Occupancy.Clear, next = Aspect.S.ordinal)
    expect(evaluateBalFixture(s,o).aspect == Aspect.A)
    o = o.copy(next = Aspect.A.ordinal)
    expect(evaluateBalFixture(s,o).aspect == Aspect.VL)
    s = s.copy(yellowFlashEnabled = true)
    expect(evaluateBalFixture(s,o).aspect == Aspect.YellowFlash)
    s = s.copy(yellowFlashEnabled = false, greenFlashBlock = true)
    expect(evaluateBalFixture(s,o).aspect == Aspect.GreenFlash)
    expect(evaluateBalFixture(s,o.copy(next = Aspect.YellowFlash.ordinal)).aspect == Aspect.GreenFlash)
    s = s.copy(greenFlashWork = true)
    expect(evaluateBalFixture(s,o.copy(next = Aspect.VL.ordinal)).aspect == Aspect.GreenFlash)
    expect(evaluateBalFixture(s,o.copy(next = Aspect.S.ordinal)).aspect == Aspect.A)
    o = o.copy(block = Occupancy.Occupied, redFlashCondition = true)
    expect(evaluateBalFixture(s,o).aspect == Aspect.S)
    s = s.copy(redFlashEnabled = true)
    expect(evaluateBalFixture(s,o).aspect == Aspect.RedFlash)
    expect(evaluateBalFixture(s,o.copy(forcedStop = true)).aspect == Aspect.S)
    expect(evaluateBalFixture(s,o.copy(fresh = false)).aspect == Aspect.Unknown)
    expect(evaluateBalFixture(s,o.copy(lampFailed = true)).reason == Reason.LampFailure)
    expect(evaluateBalFixture(s,o.copy(routeKnown = false)).reason == Reason.InvalidEquipment)
    val base = Observation(Occupancy.Clear, true, true)
    s = BalSettings()
    expect(evaluateBalFixture(s,base).reason == Reason.DownstreamUnknown)
    expect(evaluateBalFixture(s,base.copy(block = Occupancy.Unknown)).reason == Reason.BlockUnknown)
    expect(evaluateBalFixture(s,base.copy(routeKnown = false)).reason == Reason.RouteUnknown)
    expect(evaluateBalFixture(s,base.copy(fresh = false)).reason == Reason.ObservationUnavailable)
    rejected { FrenchSignalsMod().evaluate(s.asMap(), Observation(next = 99)) }
    for (a in Aspect.entries) {
        expect(BalTextures.path(a,0).isNotEmpty())
        expect(BalTextures.path(a,-1).endsWith("xx.svg"))
        expect(BalTextures.path(a,0,99).endsWith("xx.svg"))
        expect(BalTextures.path(a,0,10001).endsWith("xx.svg"))
    }
    expect(BalTextures.path(Aspect.GreenFlash,0).endsWith("tex05.svg"))
    expect(BalTextures.path(Aspect.GreenFlash,500).endsWith("tex06.svg"))
    expect(BalTextures.path(Aspect.YellowFlash,500).endsWith("tex08.svg"))
    expect(BalTextures.path(Aspect.RedFlash,0).endsWith("tex09.svg"))
    expect(BalTextures.path(Aspect.RedFlash,500).endsWith("tex10.svg"))
}
