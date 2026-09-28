package sfr.signals.carreavertissement

import nimby.*

/** Consignes propres au Carré. L'arrêt fermé est absolu ; aucune règle
 * permissive du sémaphore n'est réutilisée implicitement. Vitesses en m/s. */
internal object CarreDriving {
    fun fromDecision(decision: CarreDecision): DrivingRule = when (decision.aspect) {
        CarreAspect.Closed -> AutomaticDriving.stop()
        // Comme l'avertissement BAL : annoncer la cible du prochain signal,
        // avec arrêt par défaut si sa consigne n'est pas connue. Ne jamais
        // envoyer clear : une approche déjà mémorisée doit rester applicable.
        // passableHere permet aussi à un train ayant reçu l'annonce précédente
        // de franchir CE Carré rouvert, à sa vitesse d'approche mémorisée.
        CarreAspect.Warning -> AutomaticDriving.announceStop(
            signalsAhead = 1,
            passageSpeedMps = CarreSpeeds.REOPENED_PASSING_SPEED,
            passableHere = true,
            followTargetSpeed = true
        )
    }
}
