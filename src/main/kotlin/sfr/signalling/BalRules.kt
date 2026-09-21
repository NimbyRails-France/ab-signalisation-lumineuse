package sfr.signalling

import nimby.Occupancy
import sfr.settings.SignalSettings

/** Priorites : panne, fermeture, occupation, annonces, travaux puis voie libre. */
internal object BalRules {
    fun evaluate(settings: SignalSettings, observation: SignalObservation): SignalDecision {
        if (!settings.active) return SignalDecision(Aspect.Inactive, Reason.Inactive)
        if (observation.lampFailed) return SignalDecision(Aspect.Unknown, Reason.LampFailure)
        if (!observation.fresh) return SignalDecision(Aspect.Unknown, Reason.ObservationUnavailable)
        if (observation.forcedStop) return SignalDecision(Aspect.S, Reason.ForcedStop)
        if (observation.block == Occupancy.Occupied) {
            if (observation.redFlashCondition && settings.redFlashUseDeclared) {
                if (!settings.redFlash || !observation.routeKnown) return SignalDecision(Aspect.Unknown, Reason.InvalidEquipment)
                return SignalDecision(Aspect.RedFlash, Reason.BlockOccupied)
            }
            return SignalDecision(Aspect.S, if (observation.routeKnown) Reason.BlockOccupied else Reason.RouteUnknown)
        }
        if (!observation.routeKnown) return SignalDecision(Aspect.Unknown, Reason.RouteUnknown)
        if (observation.block != Occupancy.Clear) return SignalDecision(Aspect.Unknown, Reason.BlockUnknown)
        val next = if (settings.endOfBal) Aspect.VL else observation.next
        if (next == Aspect.Unknown || next == Aspect.Inactive) return SignalDecision(Aspect.Unknown, Reason.DownstreamUnknown)
        if (next == Aspect.S || next == Aspect.RedFlash) return SignalDecision(Aspect.A, Reason.StopAnnouncement)
        if (next == Aspect.A && settings.reducedAnnouncement) {
            return if (settings.yellowFlash) SignalDecision(Aspect.YellowFlash, Reason.ReducedAnnouncement)
                else SignalDecision(Aspect.Unknown, Reason.InvalidEquipment)
        }
        if ((next == Aspect.A || next == Aspect.YellowFlash) && settings.preannouncement) {
            return if (settings.greenFlash) SignalDecision(Aspect.GreenFlash, Reason.Preannouncement)
                else SignalDecision(Aspect.Unknown, Reason.InvalidEquipment)
        }
        if (settings.work160) return if (settings.greenFlash) SignalDecision(Aspect.GreenFlash, Reason.Work160)
            else SignalDecision(Aspect.Unknown, Reason.InvalidEquipment)
        return SignalDecision(Aspect.VL, if (settings.endOfBal) Reason.DeclaredBoundary else Reason.Clear)
    }

    fun decide(signal: NetworkSignal, nextDecision: SignalDecision?): SignalDecision? {
        val settings = signal.settings
        var observation = signal.observation
        if (signal.nextSignal != 0L) observation = observation.copy(next = nextDecision?.aspect ?: Aspect.Unknown)
        val result = evaluate(settings, observation)
        val waiting = !settings.endOfBal && signal.nextSignal != 0L && nextDecision == null &&
            result.reason == Reason.DownstreamUnknown && observation.fresh && observation.routeKnown && observation.block == Occupancy.Clear
        return if (waiting) null else result
    }
}
