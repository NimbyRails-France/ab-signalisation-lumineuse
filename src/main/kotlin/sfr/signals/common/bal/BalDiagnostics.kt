package sfr.signals.common.bal

/** Un défaut est identifié par son motif, pas seulement par l'image choisie.
 * Un canton occupé sans itinéraire connu peut afficher S tout en devant être
 * journalisé. Le SDK borne et cadence le journal ; aucun I/O dans les règles. */
internal object BalDiagnostics {
    fun isFault(reason: BalReason): Boolean = when (reason) {
        BalReason.MissingObservation, BalReason.InvalidEquipment, BalReason.LampFailure,
        BalReason.InvalidTopology, BalReason.ObservationUnavailable, BalReason.BlockUnknown,
        BalReason.RouteUnknown, BalReason.DownstreamUnknown -> true
        else -> false
    }
}
