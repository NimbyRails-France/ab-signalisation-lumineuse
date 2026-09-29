package sfr.signals.carrebal

internal object CarreBalDiagnostics {
    fun isFault(decision: CarreBalDecision) = decision.aspect == CarreBalAspect.Unknown
}
