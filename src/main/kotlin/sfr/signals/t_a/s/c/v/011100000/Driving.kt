package sfr.signals.t_a.s.c.v.`011100000`

internal object Driving {
    fun fromDecision(decision: Decision) = sfr.signals.common.bal.BalDriving.fromDecision(decision.common())
}
