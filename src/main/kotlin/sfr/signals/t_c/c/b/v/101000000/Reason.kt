package sfr.signals.t_c.c.b.v.`101000000`

/** Motifs du Carré. Leur numérotation est indépendante de celle du BAL. */
internal enum class Reason(val description: String) {
    Inactive("ouverture automatique désactivée"),
    MissingObservation("observation d'approche inconnue"),
    ObservationUnavailable("observation périmée ou indisponible"),
    InvalidTopology("lien aval manquant ou boucle sans référence"),
    BlockOccupied("canton occupé"),
    ForcedStop("fermeture demandée"),
    LampFailure("défaut du signal"),
    RouteUnknown("limite du canton ou itinéraire inconnu"),
    BlockUnknown("occupation du canton inconnue"),
    AwaitingApproach("carré fermé au repos, aucune approche confirmée"),
    ApproachConfirmed("approche en amont confirmée et canton protégé libre")
}
