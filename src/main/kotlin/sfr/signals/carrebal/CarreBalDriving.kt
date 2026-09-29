package sfr.signals.carrebal

import nimby.*
import sfr.signals.common.bal.*

internal object CarreBalDriving {
    fun driving(value: CarreBalDecision): DrivingRule? = if (value.aspect == CarreBalAspect.Closed)
        AutomaticDriving.stop() else BalDriving.fromDecision(BalDecision(requireNotNull(value.aspect.bal), value.reason.bal))
}
