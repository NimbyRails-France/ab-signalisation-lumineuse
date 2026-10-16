package sfr.signals.t_c.c.b.v.`101000000`

/** Un Carré fermé au repos est normal. Les fermetures causées par des données
 * manquantes restent des défauts, même avec l'image d'arrêt absolu attendue.
 * L'ancien motif de tolérance reste reconnu pour les diagnostics existants. */
internal object Diagnostics {
    private val faults = setOf(Reason.MissingObservation, Reason.ObservationUnavailable,
        Reason.RouteUnknown, Reason.BlockUnknown, Reason.LampFailure, Reason.InvalidTopology,
        Reason.ApproachUnknownDownstream)
    fun isFault(decision: Decision) = decision.reason in faults
}
