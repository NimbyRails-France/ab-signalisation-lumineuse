package sfr.signals.t_c.c.b.v.`111000000`

import nimby.*
import sfr.signals.common.bal.*

/** Les annonces ouvertes conservent leur motif BAL. L'attente d'approche
 * appartient à ce modèle et ne devient jamais une occupation permissive. */
internal enum class Reason(val description: String, val bal: BalReason?) {
    MissingObservation(BalReason.MissingObservation), BlockOccupied(BalReason.BlockOccupied),
    ForcedStop(BalReason.ForcedStop), StopAnnouncement(BalReason.StopAnnouncement),
    ReducedAnnouncement(BalReason.ReducedAnnouncement), Preannouncement(BalReason.Preannouncement),
    Work160(BalReason.Work160), Clear(BalReason.Clear), InvalidEquipment(BalReason.InvalidEquipment),
    LampFailure(BalReason.LampFailure), InvalidTopology(BalReason.InvalidTopology),
    ObservationUnavailable(BalReason.ObservationUnavailable), BlockUnknown(BalReason.BlockUnknown),
    RouteUnknown(BalReason.RouteUnknown), DownstreamUnknown(BalReason.DownstreamUnknown),
    AwaitingApproach("carré fermé au repos, aucune approche confirmée"),
    RouteConflict("itinéraire croisé occupé ou réservé");

    constructor(bal: BalReason) : this(bal.description, bal)
    constructor(description: String) : this(description, null)
}
