package sfr.signals.common.bal

import nimby.*

/** Priorites : panne, fermeture, occupation, annonces, travaux puis voie libre. */
internal object BalRules {
    fun evaluate(settings: BalSettings, observation: Observation, next: BalAspect = BalAspect.Unknown,
                 redFlashCondition: Boolean = observation.redFlashCondition): BalDecision {
        if (observation.lampFailed) return BalDecision(BalAspect.Unknown, BalReason.LampFailure)
        if (!observation.fresh) return BalDecision(BalAspect.Unknown, BalReason.ObservationUnavailable)
        if (observation.forcedStop) return BalDecision(BalAspect.S, BalReason.ForcedStop)
        if (observation.block == Occupancy.Occupied) {
            if (redFlashCondition && settings.redFlashEnabled) {
                if (!observation.routeKnown) return BalDecision(BalAspect.Unknown, BalReason.InvalidEquipment)
                return BalDecision(BalAspect.RedFlash, BalReason.BlockOccupied)
            }
            return BalDecision(BalAspect.S, if (observation.routeKnown) BalReason.BlockOccupied else BalReason.RouteUnknown)
        }
        if (!observation.routeKnown) return BalDecision(BalAspect.Unknown, BalReason.RouteUnknown)
        if (observation.block != Occupancy.Clear) return BalDecision(BalAspect.Unknown, BalReason.BlockUnknown)
        if (next == BalAspect.Unknown) return BalDecision(BalAspect.Unknown, BalReason.DownstreamUnknown)
        if (next == BalAspect.S || next == BalAspect.RedFlash) return BalDecision(BalAspect.A, BalReason.StopAnnouncement)
        if (next == BalAspect.A && settings.yellowFlashEnabled) {
            return BalDecision(BalAspect.YellowFlash, BalReason.ReducedAnnouncement)
        }
        if ((next == BalAspect.A || next == BalAspect.YellowFlash) && settings.greenFlashBlock) {
            return BalDecision(BalAspect.GreenFlash, BalReason.Preannouncement)
        }
        if (settings.greenFlashWork) return BalDecision(BalAspect.GreenFlash, BalReason.Work160)
        return BalDecision(BalAspect.VL, BalReason.Clear)
    }

    /** Le SDK fournit le contexte prêt à lire. Seuls les choix BAL restent ici :
     * réglages métier, lecture du voisin et usage déclaré du rouge clignotant. */
    fun decide(context: SignalRuleContext, next: BalAspect?): BalDecision? {
        val settings = BalSettings.from(context.settings)
        val observation = context.observation
        val result = evaluate(settings, observation, next ?: BalAspect.Unknown,
            redFlashCondition = settings.redFlashEnabled)
        val waiting = context.signal.nextSignal != 0L && next == null &&
            result.reason == BalReason.DownstreamUnknown && observation.fresh && observation.routeKnown && observation.block == Occupancy.Clear
        return if (waiting) null else result
    }

}
