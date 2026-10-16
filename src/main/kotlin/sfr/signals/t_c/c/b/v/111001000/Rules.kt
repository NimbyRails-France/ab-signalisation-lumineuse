package sfr.signals.t_c.c.b.v.`111001000`

import nimby.*
import sfr.signals.common.bal.*
import sfr.signals.uti.sidm.b.z.`200000000`.Markers

internal object Rules {
    private fun local(value: CarreBalDecision): Decision = when (value) {
        is CarreBalDecision.Bal -> Decision(
            Aspect.entries.first { it.bal == value.value.aspect },
            Reason.entries.first { it.bal == value.value.reason })
        is CarreBalDecision.Closed -> Decision(Aspect.Closed,
            Reason.entries.first { it.bal == value.reason })
        CarreBalDecision.AwaitingApproach -> Decision(Aspect.Closed, Reason.AwaitingApproach)
        CarreBalDecision.RouteConflict -> Decision(Aspect.Closed, Reason.RouteConflict)
    }

    /** La manœuvre utilise une borne native qualifiée du parcours analysé pour
     * le train en approche, jamais l'absence d'un voisin dans le réseau du mod.
     * Les marqueurs ne donnent ni une réservation ni une autorisation de passage. */
    private fun manoeuvre(observation: Observation): Decision? {
        val route = observation.selectedRoute
        // Un préfixe partiel peut prouver une occupation, jamais une voie libre.
        if (route.destination != RouteDestination.NativeBoundary && route.occupation != Occupancy.Occupied) return null

        // Ces refus restent prioritaires même si l'étendue observée diffère du
        // canton à signal aval utilisé par le calcul BAL historique.
        when {
            observation.lampFailed -> return Decision(Aspect.Unknown, Reason.LampFailure)
            !observation.fresh -> return Decision(Aspect.Closed, Reason.ObservationUnavailable)
            observation.forcedStop -> return Decision(Aspect.Closed, Reason.ForcedStop)
            observation.routeConflict != null -> return Decision(Aspect.Closed, Reason.RouteConflict)
            observation.approachingTrain == null -> return Decision(Aspect.Closed, Reason.AwaitingApproach)
            observation.block == Occupancy.Occupied -> return Decision(Aspect.Closed, Reason.BlockOccupied)
            route.occupation == Occupancy.Occupied -> return Decision(Aspect.Closed, Reason.BlockOccupied)
            !route.complete -> return Decision(Aspect.Closed, Reason.SelectedRouteUnavailable)
            route.occupation == Occupancy.Unknown -> return Decision(Aspect.Closed, Reason.BlockUnknown)
        }

        var white = false
        var flashing = false
        for (index in route.markers.indices) {
            val marker = route.markers[index]
            if (marker.textureState != 0) continue
            when (marker.textureSet) {
                Markers.T32_TEXTURES -> white = true
                Markers.T33_TEXTURES -> flashing = true
            }
        }
        return when {
            white && flashing -> Decision(Aspect.Closed, Reason.ConflictingManoeuvreMarkers)
            flashing -> Decision(Aspect.WhiteFlash, Reason.FlashingManoeuvre)
            white -> Decision(Aspect.White, Reason.Manoeuvre)
            else -> null
        }
    }

    fun evaluate(settings: Map<String, Boolean>, observation: Observation, next: Aspect): Decision =
        manoeuvre(observation) ?:
            local(CarreBalRules.evaluate(Settings.from(settings), observation, next.bal ?: BalAspect.S))

    fun decide(context: SignalRuleContext): Decision? =
        manoeuvre(context.observation) ?:
            CarreBalRules.decide(Settings.from(context.settings), context, BalNeighbours.aspect(context.next))?.let(::local)
}
