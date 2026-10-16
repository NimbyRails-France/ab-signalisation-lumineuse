package sfr.signals.t_a.s.c.v.`011100000`

import sfr.signals.common.bal.BalDiagnostics
import sfr.signals.common.bal.BalReason

/** Classification pour le journal : elle ne modifie ni le feu ni la conduite. */
internal object Diagnostics {
    fun isFault(decision: Decision) = decision.aspect == Aspect.Unknown ||
        BalDiagnostics.isFault(BalReason.valueOf(decision.reason.name))
}
