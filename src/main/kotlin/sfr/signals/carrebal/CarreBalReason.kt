package sfr.signals.carrebal

import nimby.*
import sfr.signals.common.bal.*

internal enum class CarreBalReason(val bal: BalReason) {
    MissingObservation(BalReason.MissingObservation), BlockOccupied(BalReason.BlockOccupied),
    ForcedStop(BalReason.ForcedStop), StopAnnouncement(BalReason.StopAnnouncement),
    ReducedAnnouncement(BalReason.ReducedAnnouncement), Preannouncement(BalReason.Preannouncement),
    Work160(BalReason.Work160), Clear(BalReason.Clear), InvalidEquipment(BalReason.InvalidEquipment),
    LampFailure(BalReason.LampFailure), InvalidTopology(BalReason.InvalidTopology),
    ObservationUnavailable(BalReason.ObservationUnavailable), BlockUnknown(BalReason.BlockUnknown),
    RouteUnknown(BalReason.RouteUnknown), DownstreamUnknown(BalReason.DownstreamUnknown);
    val description get() = bal.description
}

