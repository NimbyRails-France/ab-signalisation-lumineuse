package sfr.signals.t_c.c.b.v.`101000000`

import nimby.*

/** Règles du Carré simple Avertissement, indépendantes du BAL.
 * Fermé au repos. L'approche est une observation en amont du SDK ; ni une
 * queue de train en aval ni le changement d'un voisin ne prouvent l'approche.
 * La fin de BAL est un usage possible, pas l'identité de ce modèle. L'ouverture
 * exige une limite aval connue et une occupation effectivement observée libre. */
internal object Rules {
    fun decide(context: SignalRuleContext): Decision {
        fun closed(reason: Reason) = Decision(Aspect.Closed, reason)
        if (context.settingsStatus != SettingsStatus.Present) return closed(Reason.ObservationUnavailable)
        if (!Settings.permitsOpening(context.settings)) return closed(Reason.Inactive)
        val observation = context.observation
        if (observation.lampFailed) return closed(Reason.LampFailure)
        if (!observation.fresh) return closed(Reason.ObservationUnavailable)
        if (observation.forcedStop) return closed(Reason.ForcedStop)
        if (observation.routeConflict != null) return closed(Reason.RouteConflict)
        // Dès l'entrée de la tête dans la portion aval observée, refermer.
        // La queue peut encore être en amont et un autre train déjà annoncé :
        // cette occupation prime aussi quand aucune limite aval n'est connue.
        if (observation.block == Occupancy.Occupied) return closed(Reason.BlockOccupied)
        // Un parcours tronqué ou une présence inconnue ne prouve jamais libre
        // la portion protégée, y compris en fin de BAL ou vers un terminus.
        if (!observation.routeKnown) return closed(Reason.RouteUnknown)
        if (observation.block != Occupancy.Clear) return closed(Reason.BlockUnknown)
        // Le SDK valide l'identifiant et la fraîcheur de l'approche. Le mod
        // choisit seulement si cette présence permet d'ouvrir le signal.
        if (!context.trainApproaching) return closed(Reason.AwaitingApproach)
        return Decision(Aspect.Warning, Reason.ApproachConfirmed)
    }
}
