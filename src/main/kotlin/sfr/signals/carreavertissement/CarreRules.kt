package sfr.signals.carreavertissement

import nimby.*

/** Règles du Carré simple Avertissement, indépendantes du BAL.
 * Fermé au repos. L'approche est une observation en amont du SDK ; ni une
 * queue de train en aval ni le changement d'un voisin ne prouvent l'approche.
 * La fin de BAL est un usage possible, pas l'identité de ce modèle.
 * Politique de ce modèle : un aval inconnu est supposé libre. Le SDK conserve
 * son observation Unknown ; cette tolérance ne s'applique pas aux règles BAL. */
internal object CarreRules {
    fun decide(context: SignalRuleContext): CarreDecision {
        fun closed(reason: CarreReason) = CarreDecision(CarreAspect.Closed, reason)
        if (context.settingsStatus != SettingsStatus.Present) return closed(CarreReason.ObservationUnavailable)
        if (!CarreSettings.permitsOpening(context.settings)) return closed(CarreReason.Inactive)
        val observation = context.observation
        if (observation.lampFailed) return closed(CarreReason.LampFailure)
        if (!observation.fresh) return closed(CarreReason.ObservationUnavailable)
        if (observation.forcedStop) return closed(CarreReason.ForcedStop)
        if (observation.block == Occupancy.Occupied) return closed(CarreReason.BlockOccupied)
        // À la sortie du BAL, l'absence de frontière aval ne bloque pas
        // l'ouverture. Une occupation réellement détectée reste prioritaire.
        val approaching = observation.approachingTrain ?: return closed(CarreReason.AwaitingApproach)
        if (approaching ushr 48 != 5L) return closed(CarreReason.MissingObservation)
        return CarreDecision(CarreAspect.Warning, CarreReason.ApproachConfirmed)
    }
}
