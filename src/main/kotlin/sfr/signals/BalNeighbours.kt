package sfr.signals

import nimby.*
import sfr.signals.bal.BalSignals
import sfr.signals.carrebal.CarreBal
import sfr.signals.common.bal.BalAspect

/** Interprétation des modèles du paquet, hors du calcul BAL commun.
 * Les inconnus des deux modèles restent inconnus ; un carré fermé annonce un arrêt. */
internal object BalNeighbours {
    fun aspect(next: SignalNeighbour?): BalAspect? {
        if (next == null) return null
        next.of(BalSignals.model)?.let { return BalAspect.valueOf(it.aspect.name) }
        next.of(CarreBal.model)?.let { return it.aspect.bal ?: BalAspect.S }
        if (!next.active) return BalAspect.Unknown
        val rule = next.drivingRule ?: return BalAspect.Unknown
        return when {
            DrivingFlag.Stop in rule.flags -> BalAspect.S
            DrivingFlag.OnSight in rule.flags -> BalAspect.RedFlash
            rule.speedMps == 0.0 && rule.signalsAhead == 1 -> BalAspect.A
            rule.speedMps == 0.0 && rule.signalsAhead == 2 -> BalAspect.YellowFlash
            DrivingFlag.Clear in rule.flags -> BalAspect.VL
            DrivingFlag.HoldToClear in rule.flags && rule.speedMps > 0.0 && rule.signalsAhead == 1 -> BalAspect.GreenFlash
            else -> BalAspect.Unknown
        }
    }
}
