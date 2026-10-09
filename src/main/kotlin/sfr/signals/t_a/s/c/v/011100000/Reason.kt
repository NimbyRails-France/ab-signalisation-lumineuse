package sfr.signals.t_a.s.c.v.`011100000`

/** Motifs produits par les règles BAL de cette version. */
internal enum class Reason(val description: String) {
    MissingObservation("observation ou itinéraire inconnu"),
    BlockOccupied("canton occupé"),
    ForcedStop("fermeture demandée"),
    StopAnnouncement("annonce du signal restrictif aval"),
    ReducedAnnouncement("annonce à distance réduite configurée"),
    Preannouncement("préannonce configurée"),
    Work160("zone de travaux 160 configurée"),
    Clear("cantonnement libre observé"),
    InvalidEquipment("équipement incompatible"),
    LampFailure("défaut du signal"),
    InvalidTopology("lien aval manquant ou boucle sans référence"),
    ObservationUnavailable("observation périmée ou indisponible"),
    BlockUnknown("occupation du canton inconnue"),
    RouteUnknown("limite du canton ou itinéraire inconnu"),
    DownstreamUnknown("indication du signal suivant inconnue ou inactive")
}
