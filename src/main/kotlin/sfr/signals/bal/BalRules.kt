package sfr.signals.bal

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
    fun decide(context: SignalRuleContext): BalDecision? {
        val settings = BalSettings.from(context.settings)
        val observation = context.observation
        val next = downstreamAspect(context.next)
        val result = evaluate(settings, observation, next ?: BalAspect.Unknown,
            redFlashCondition = settings.redFlashEnabled)
        val waiting = context.signal.nextSignal != 0L && next == null &&
            result.reason == BalReason.DownstreamUnknown && observation.fresh && observation.routeKnown && observation.block == Occupancy.Clear
        return if (waiting) null else result
    }

    /** Politique d'annonce du BAL, à partir de la consigne publiée par le voisin.
     * Aucune enum ni classe d'un autre modèle n'est nécessaire. Le SDK fournit
     * les données ; le choix de ce que le BAL annonce reste ici, dans ses règles.
     * Une consigne non prise en charge reste inconnue, jamais voie libre. */
    private fun downstreamAspect(next: SignalNeighbour?): BalAspect? {
        if (next == null) return null
        // Même modèle : conserver notamment Unknown et les variantes de préannonce.
        next.of(BalSignals.model)?.let { return it.aspect }
        if (!next.active) return BalAspect.Unknown
        val rule = next.drivingRule ?: return BalAspect.Unknown
        return when {
            DrivingFlag.Stop in rule.flags -> BalAspect.S
            DrivingFlag.OnSight in rule.flags -> BalAspect.RedFlash
            rule.speedMps == 0.0 && rule.signalsAhead == 1 -> BalAspect.A
            rule.speedMps == 0.0 && rule.signalsAhead == 2 -> BalAspect.YellowFlash
            DrivingFlag.Clear in rule.flags -> BalAspect.VL
            DrivingFlag.HoldToClear in rule.flags && rule.speedMps > 0.0 && rule.signalsAhead == 1 -> BalAspect.GreenFlash
            else -> BalAspect.Unknown
        }
    }
}
