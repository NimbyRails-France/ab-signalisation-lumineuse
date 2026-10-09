package sfr.signals.t_c.c.b.v.`101000000`

import nimby.*

/** Règles du Carré simple Avertissement, indépendantes du BAL.
 * Fermé au repos. L'approche est une observation en amont du SDK ; ni une
 * queue de train en aval ni le changement d'un voisin ne prouvent l'approche.
 * La fin de BAL est un usage possible, pas l'identité de ce modèle.
 * Politique de ce modèle : un aval inconnu est supposé libre. Le SDK conserve
 * son observation Unknown ; cette tolérance ne s'applique pas aux règles BAL. */
internal object Rules {
    fun decide(context: SignalRuleContext): Decision {
        fun closed(reason: Reason) = Decision(Aspect.Closed, reason)
        if (context.settingsStatus != SettingsStatus.Present) return closed(Reason.ObservationUnavailable)
        if (!Settings.permitsOpening(context.settings)) return closed(Reason.Inactive)
        val observation = context.observation
        if (observation.lampFailed) return closed(Reason.LampFailure)
        if (!observation.fresh) return closed(Reason.ObservationUnavailable)
        if (observation.forcedStop) return closed(Reason.ForcedStop)
        // Dès l'entrée de la tête dans la portion aval observée, refermer.
        // La queue peut encore être en amont et un autre train déjà annoncé :
        // cette occupation prime aussi quand aucune limite aval n'est connue.
        if (observation.block == Occupancy.Occupied) return closed(Reason.BlockOccupied)
        // À la sortie du BAL, l'absence de frontière aval ne bloque pas
        // l'ouverture. Une occupation réellement détectée reste prioritaire.
        // Le SDK valide l'identifiant et la fraîcheur de l'approche. Le mod
        // choisit seulement si cette présence permet d'ouvrir le signal.
        if (!context.trainApproaching) return closed(Reason.AwaitingApproach)
        return Decision(Aspect.Warning, Reason.ApproachConfirmed)
    }
}
