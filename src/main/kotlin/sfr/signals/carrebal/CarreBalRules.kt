package sfr.signals.carrebal

import nimby.*
import sfr.signals.common.bal.*
import sfr.signals.BalNeighbours

internal object CarreBalRules {
    private fun fromBal(value: BalDecision) = CarreBalDecision(
        CarreBalAspect.entries.first { it.bal == value.aspect },
        CarreBalReason.entries.first { it.bal == value.reason })

    private fun closure(settings: Map<String, Boolean>, observation: Observation): CarreBalDecision? = when {
        observation.lampFailed -> CarreBalDecision(CarreBalAspect.Unknown, CarreBalReason.LampFailure)
        !observation.fresh -> CarreBalDecision(CarreBalAspect.Unknown, CarreBalReason.ObservationUnavailable)
        CarreBalSettings.from(settings).forceClosed || observation.forcedStop ->
            CarreBalDecision(CarreBalAspect.Closed, CarreBalReason.ForcedStop)
        else -> null
    }

    fun evaluate(settings: Map<String, Boolean>, observation: Observation, next: CarreBalAspect): CarreBalDecision =
        closure(settings, observation) ?: fromBal(BalRules.evaluate(BalSettings.from(settings), observation,
            next.bal ?: BalAspect.S))

    fun decide(context: SignalRuleContext): CarreBalDecision? =
        closure(context.settings, context.observation) ?: BalRules.decide(context, BalNeighbours.aspect(context.next))?.let(::fromBal)

}
