package sfr.signals.t_c.c.b.v.`111000000`

import nimby.*
import sfr.signals.common.bal.*
import sfr.signals.common.bal.BalNeighbours

internal object Rules {
    private fun fromBal(value: BalDecision) = Decision(
        Aspect.entries.first { it.bal == value.aspect },
        Reason.entries.first { it.bal == value.reason })

    private fun closure(settings: Map<String, Boolean>, observation: Observation): Decision? = when {
        observation.lampFailed -> Decision(Aspect.Unknown, Reason.LampFailure)
        !observation.fresh -> Decision(Aspect.Unknown, Reason.ObservationUnavailable)
        Settings.from(settings).forceClosed || observation.forcedStop ->
            Decision(Aspect.Closed, Reason.ForcedStop)
        else -> null
    }

    fun evaluate(settings: Map<String, Boolean>, observation: Observation, next: Aspect): Decision =
        closure(settings, observation) ?: fromBal(BalRules.evaluate(BalSettings.from(settings), observation,
            next.bal ?: BalAspect.S))

    fun decide(context: SignalRuleContext): Decision? =
        closure(context.settings, context.observation) ?: BalRules.decide(context, BalNeighbours.aspect(context.next))?.let(::fromBal)

}
