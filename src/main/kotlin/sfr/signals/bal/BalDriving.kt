package sfr.signals.bal

internal object BalDriving {
    fun fromDecision(decision: BalDecision) = sfr.signals.common.bal.BalDriving.fromDecision(decision.common())
}
