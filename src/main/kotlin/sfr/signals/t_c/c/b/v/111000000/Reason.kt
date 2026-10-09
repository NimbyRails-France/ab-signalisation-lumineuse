package sfr.signals.t_c.c.b.v.`111000000`

import nimby.*
import sfr.signals.common.bal.*

internal enum class Reason(val bal: BalReason) {
    MissingObservation(BalReason.MissingObservation), BlockOccupied(BalReason.BlockOccupied),
    ForcedStop(BalReason.ForcedStop), StopAnnouncement(BalReason.StopAnnouncement),
    ReducedAnnouncement(BalReason.ReducedAnnouncement), Preannouncement(BalReason.Preannouncement),
    Work160(BalReason.Work160), Clear(BalReason.Clear), InvalidEquipment(BalReason.InvalidEquipment),
    LampFailure(BalReason.LampFailure), InvalidTopology(BalReason.InvalidTopology),
    ObservationUnavailable(BalReason.ObservationUnavailable), BlockUnknown(BalReason.BlockUnknown),
    RouteUnknown(BalReason.RouteUnknown), DownstreamUnknown(BalReason.DownstreamUnknown);
    val description get() = bal.description
}
