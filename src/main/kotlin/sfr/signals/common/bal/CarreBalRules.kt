package sfr.signals.common.bal

import nimby.*

/** Politique commune des Carrés BAL. Le mode carré conserve les annonces BAL
 * ouvertes, remplace S/S cli par C et attend une tête dans les deux cantons
 * déclarés par le modèle. Aucune observation ne réserve une voie. */
internal object CarreBalRules {
    private fun fromBal(value: BalDecision): CarreBalDecision =
        if (value.aspect == BalAspect.Unknown && value.reason != BalReason.LampFailure)
            CarreBalDecision.Closed(value.reason) else CarreBalDecision.Bal(value)

    private fun closure(settings: CarreBalSettings, observation: Observation, trainApproaching: Boolean): CarreBalDecision? = when {
        observation.lampFailed -> CarreBalDecision.Bal(BalDecision(BalAspect.Unknown, BalReason.LampFailure))
        !observation.fresh -> CarreBalDecision.Closed(BalReason.ObservationUnavailable)
        observation.forcedStop || settings.mode == CarreBalReturnMode.Semaphore && settings.forceClosed ->
            CarreBalDecision.Closed(BalReason.ForcedStop)
        observation.routeConflict != null -> CarreBalDecision.RouteConflict
        !observation.routeKnown -> CarreBalDecision.Closed(BalReason.RouteUnknown)
        observation.block == Occupancy.Unknown -> CarreBalDecision.Closed(BalReason.BlockUnknown)
        settings.mode == CarreBalReturnMode.Square && observation.block == Occupancy.Occupied ->
            CarreBalDecision.Closed(BalReason.BlockOccupied)
        settings.mode == CarreBalReturnMode.Square && !trainApproaching -> CarreBalDecision.AwaitingApproach
        else -> null
    }

    fun evaluate(settings: CarreBalSettings, observation: Observation, next: BalAspect): CarreBalDecision =
        closure(settings, observation, observation.approachingTrain != null) ?:
            fromBal(BalRules.evaluate(settings.bal, observation, next))

    fun decide(settings: CarreBalSettings, context: SignalRuleContext, next: BalAspect?): CarreBalDecision? =
        closure(settings, context.observation, context.trainApproaching) ?:
            // Preserve null while the SDK resolves the actual downstream signal.
            BalRules.decide(context, next)?.let(::fromBal)
}
