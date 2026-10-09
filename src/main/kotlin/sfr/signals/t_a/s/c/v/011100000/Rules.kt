package sfr.signals.t_a.s.c.v.`011100000`

import nimby.*
import sfr.signals.common.bal.BalNeighbours
import sfr.signals.common.bal.BalRules as CommonRules
import sfr.signals.common.bal.BalAspect as CommonAspect

internal object Rules {
    fun evaluate(settings: Settings, observation: Observation, next: Aspect = Aspect.Unknown,
                 redFlashCondition: Boolean = observation.redFlashCondition): Decision =
        CommonRules.evaluate(settings.common(), observation, CommonAspect.valueOf(next.name), redFlashCondition).toSemaphore()

    fun decide(context: SignalRuleContext): Decision? =
        CommonRules.decide(context, BalNeighbours.aspect(context.next))?.toSemaphore()
}
