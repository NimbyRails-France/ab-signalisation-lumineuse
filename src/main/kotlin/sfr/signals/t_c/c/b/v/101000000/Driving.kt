package sfr.signals.t_c.c.b.v.`101000000`

import nimby.*

/** Consignes propres au Carré. L'arrêt fermé est absolu ; aucune règle
 * permissive du sémaphore n'est réutilisée implicitement. Vitesses en m/s. */
internal object Driving {
    fun fromDecision(decision: Decision): DrivingRule = when (decision.aspect) {
        Aspect.Closed -> AutomaticDriving.stop()
        // Comme l'avertissement BAL : annoncer la cible du prochain signal,
        // avec arrêt par défaut si sa consigne n'est pas connue. Ne jamais
        // envoyer clear : une approche déjà mémorisée doit rester applicable.
        // passableHere permet aussi à un train ayant reçu l'annonce précédente
        // de franchir CE Carré rouvert, à sa vitesse d'approche mémorisée.
        Aspect.Warning -> AutomaticDriving.announceStop(
            signalsAhead = 1,
            passageSpeedMps = Speeds.REOPENED_PASSING_SPEED,
            passableHere = true,
            followTargetSpeed = true
        )
    }
}
