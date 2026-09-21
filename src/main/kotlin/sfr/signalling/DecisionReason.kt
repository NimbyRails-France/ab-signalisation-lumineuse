package sfr.signalling

/** Motif de diagnostic et de conduite. */
internal enum class Reason(val description: String) {
    Inactive("script inactif"),
    MissingObservation("observation ou itineraire inconnu"),
    BlockOccupied("canton occupe"),
    ForcedStop("fermeture demandee"),
    StopAnnouncement("annonce du signal restrictif aval"),
    ReducedAnnouncement("annonce a distance reduite configuree"),
    Preannouncement("preannonce configuree"),
    Work160("zone de travaux 160 configuree"),
    Clear("cantonnement libre observe"),
    InvalidEquipment("equipement incompatible"),
    LampFailure("defaut du signal"),
    InvalidTopology("lien aval manquant ou boucle sans reference"),
    ObservationUnavailable("observation perimee ou indisponible"),
    BlockUnknown("occupation du canton inconnue"),
    RouteUnknown("limite du canton ou itineraire inconnu"),
    DownstreamUnknown("indication du signal suivant inconnue ou inactive"),
    DeclaredBoundary("canton libre, fin de zone declaree avec aval suppose libre")
}
