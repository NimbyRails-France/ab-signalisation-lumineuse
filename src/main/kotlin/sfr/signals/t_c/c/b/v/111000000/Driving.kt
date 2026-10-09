package sfr.signals.t_c.c.b.v.`111000000`

import nimby.*
import sfr.signals.common.bal.*

internal object Driving {
    fun driving(value: Decision): DrivingRule? = if (value.aspect == Aspect.Closed)
        AutomaticDriving.stop() else BalDriving.fromDecision(BalDecision(requireNotNull(value.aspect.bal), value.reason.bal))
}
