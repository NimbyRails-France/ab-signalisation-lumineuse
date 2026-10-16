package sfr.signals.t_c.c.b.v.`111001000`

import sfr.signals.common.bal.BalReason

internal enum class Reason(val description: String, val bal: BalReason?) {
    MissingObservation(BalReason.MissingObservation), BlockOccupied(BalReason.BlockOccupied),
    ForcedStop(BalReason.ForcedStop), StopAnnouncement(BalReason.StopAnnouncement),
    ReducedAnnouncement(BalReason.ReducedAnnouncement), Preannouncement(BalReason.Preannouncement),
    Work160(BalReason.Work160), Clear(BalReason.Clear), InvalidEquipment(BalReason.InvalidEquipment),
    LampFailure(BalReason.LampFailure), InvalidTopology(BalReason.InvalidTopology),
    ObservationUnavailable(BalReason.ObservationUnavailable), BlockUnknown(BalReason.BlockUnknown),
    RouteUnknown(BalReason.RouteUnknown), DownstreamUnknown(BalReason.DownstreamUnknown),
    AwaitingApproach("carré fermé au repos, aucune approche confirmée"),
    RouteConflict("itinéraire croisé occupé ou réservé"),
    Manoeuvre("parcours borné sans signal suivant, marqueur T32"),
    FlashingManoeuvre("parcours borné sans signal suivant, marqueur T33"),
    SelectedRouteUnavailable("parcours sélectionné incomplet"),
    ConflictingManoeuvreMarkers("marqueurs T32 et T33 incompatibles sur le même parcours");

    constructor(bal: BalReason) : this(bal.description, bal)
    constructor(description: String) : this(description, null)
}
