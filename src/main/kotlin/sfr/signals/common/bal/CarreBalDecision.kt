package sfr.signals.common.bal

/** Résultat commun, sans texture ni enum d'un modèle concret. Un motif BAL
 * peut rester diagnostique même si son indication devient un arrêt absolu. */
internal sealed interface CarreBalDecision {
    data class Bal(val value: BalDecision) : CarreBalDecision
    data class Closed(val reason: BalReason) : CarreBalDecision
    data object AwaitingApproach : CarreBalDecision
    data object RouteConflict : CarreBalDecision
}
