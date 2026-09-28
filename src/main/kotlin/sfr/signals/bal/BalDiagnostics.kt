package sfr.signals.bal

/** Classification pour le journal : elle ne modifie ni le feu ni la conduite. */
internal object BalDiagnostics {
    fun isFault(decision: BalDecision) = decision.aspect == BalAspect.Unknown
}
