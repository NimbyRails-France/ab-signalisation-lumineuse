package sfr.signals.t_c.c.b.v.`111000000`

import nimby.*
import sfr.signals.common.bal.*

internal object Rules {
    private fun local(value: CarreBalDecision): Decision = when (value) {
        is CarreBalDecision.Bal -> Decision(
            Aspect.entries.first { it.bal == value.value.aspect },
            Reason.entries.first { it.bal == value.value.reason })
        is CarreBalDecision.Closed -> Decision(Aspect.Closed,
            Reason.entries.first { it.bal == value.reason })
        CarreBalDecision.AwaitingApproach -> Decision(Aspect.Closed, Reason.AwaitingApproach)
        CarreBalDecision.RouteConflict -> Decision(Aspect.Closed, Reason.RouteConflict)
    }

    fun evaluate(settings: Map<String, Boolean>, observation: Observation, next: Aspect): Decision =
        local(CarreBalRules.evaluate(Settings.from(settings), observation, next.bal ?: BalAspect.S))

    fun decide(context: SignalRuleContext): Decision? =
        CarreBalRules.decide(Settings.from(context.settings), context, BalNeighbours.aspect(context.next))?.let(::local)
}
