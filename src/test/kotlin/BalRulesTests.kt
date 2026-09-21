package sfr.tests

import nimby.*
import sfr.FrenchSignalsMod
import sfr.settings.SignalSettings
import sfr.signalling.*
import sfr.rendering.SignalTextures
import sfr.driving.*
import kotlin.math.*

internal fun testBalRules() {
    var s = SignalSettings(active = true)
    var o = SignalObservation(Occupancy.Occupied, true, true, next = Aspect.VL)
    expect(BalRules.evaluate(s,o).aspect == Aspect.S)
    o = o.copy(block = Occupancy.Clear, next = Aspect.S)
    expect(BalRules.evaluate(s,o).aspect == Aspect.A)
    o = o.copy(next = Aspect.A)
    expect(BalRules.evaluate(s,o).aspect == Aspect.VL)
    s = s.copy(reducedAnnouncement = true)
    expect(BalRules.evaluate(s,o).reason == Reason.InvalidEquipment)
    s = s.copy(yellowFlash = true)
    expect(BalRules.evaluate(s,o).aspect == Aspect.YellowFlash)
    s = s.copy(reducedAnnouncement = false, preannouncement = true, greenFlash = true)
    expect(BalRules.evaluate(s,o).aspect == Aspect.GreenFlash)
    expect(BalRules.evaluate(s,o.copy(next = Aspect.YellowFlash)).aspect == Aspect.GreenFlash)
    s = s.copy(work160 = true)
    expect(BalRules.evaluate(s,o.copy(next = Aspect.VL)).aspect == Aspect.GreenFlash)
    expect(BalRules.evaluate(s,o.copy(next = Aspect.S)).aspect == Aspect.A)
    o = o.copy(block = Occupancy.Occupied, redFlashCondition = true)
    s = s.copy(redFlash = true)
    expect(BalRules.evaluate(s,o).aspect == Aspect.S)
    s = s.copy(redFlashUseDeclared = true)
    expect(BalRules.evaluate(s,o).aspect == Aspect.RedFlash)
    expect(BalRules.evaluate(s,o.copy(forcedStop = true)).aspect == Aspect.S)
    expect(BalRules.evaluate(s,o.copy(fresh = false)).aspect == Aspect.Unknown)
    expect(BalRules.evaluate(s.copy(active = false),o).aspect == Aspect.Inactive)
    expect(BalRules.evaluate(s,o.copy(lampFailed = true)).reason == Reason.LampFailure)
    val base = SignalObservation(Occupancy.Clear, true, true)
    s = SignalSettings(active = true)
    expect(BalRules.evaluate(s,base).reason == Reason.DownstreamUnknown)
    expect(BalRules.evaluate(s,base.copy(block = Occupancy.Unknown)).reason == Reason.BlockUnknown)
    expect(BalRules.evaluate(s,base.copy(routeKnown = false)).reason == Reason.RouteUnknown)
    expect(BalRules.evaluate(s,base.copy(fresh = false)).reason == Reason.ObservationUnavailable)
    expect(BalRules.evaluate(s.copy(endOfBal = true),base).reason == Reason.DeclaredBoundary)
    rejected { FrenchSignalsMod().evaluate(s.asMap(), Observation(next = 99)) }
    for (a in Aspect.entries) {
        expect(SignalTextures.path(a,0).isNotEmpty())
        expect(SignalTextures.path(a,-1).endsWith("xx.svg"))
        expect(SignalTextures.path(a,0,99).endsWith("xx.svg"))
        expect(SignalTextures.path(a,0,10001).endsWith("xx.svg"))
    }
    expect(SignalTextures.path(Aspect.GreenFlash,0).endsWith("tex05.svg"))
    expect(SignalTextures.path(Aspect.GreenFlash,500).endsWith("tex06.svg"))
    expect(SignalTextures.path(Aspect.YellowFlash,500).endsWith("tex08.svg"))
    expect(SignalTextures.path(Aspect.RedFlash,0).endsWith("tex09.svg"))
    expect(SignalTextures.path(Aspect.RedFlash,500).endsWith("tex10.svg"))
}
