package sfr.signals.bal

import nimby.*
import sfr.signals.BalNeighbours
import sfr.signals.common.bal.BalRules as CommonRules
import sfr.signals.common.bal.BalAspect as CommonAspect

internal object BalRules {
    fun evaluate(settings: BalSettings, observation: Observation, next: BalAspect = BalAspect.Unknown,
                 redFlashCondition: Boolean = observation.redFlashCondition): BalDecision =
        CommonRules.evaluate(settings.common(), observation, CommonAspect.valueOf(next.name), redFlashCondition).toSemaphore()

    fun decide(context: SignalRuleContext): BalDecision? =
        CommonRules.decide(context, BalNeighbours.aspect(context.next))?.toSemaphore()
}
