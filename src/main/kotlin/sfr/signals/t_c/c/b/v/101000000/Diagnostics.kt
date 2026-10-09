package sfr.signals.t_c.c.b.v.`101000000`

/** Un Carré fermé au repos est normal. Seuls les motifs ci-dessous sont des
 * défauts à journaliser ; ce diagnostic ne change pas l'indication affichée. */
internal object Diagnostics {
    private val faults = setOf(Reason.MissingObservation, Reason.ObservationUnavailable,
        Reason.RouteUnknown, Reason.BlockUnknown, Reason.LampFailure, Reason.InvalidTopology)
    fun isFault(decision: Decision) = decision.aspect == Aspect.Closed && decision.reason in faults
}
