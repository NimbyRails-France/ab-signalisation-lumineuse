package sfr.signals.carreavertissement

/** Un Carré fermé au repos est normal. Seuls les motifs ci-dessous sont des
 * défauts à journaliser ; ce diagnostic ne change pas l'indication affichée. */
internal object CarreDiagnostics {
    private val faults = setOf(CarreReason.MissingObservation, CarreReason.ObservationUnavailable,
        CarreReason.RouteUnknown, CarreReason.BlockUnknown, CarreReason.LampFailure, CarreReason.InvalidTopology)
    fun isFault(decision: CarreDecision) = decision.aspect == CarreAspect.Closed && decision.reason in faults
}
