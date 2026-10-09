package sfr.signals.common.bal

import nimby.*
import sfr.signals.t_a.s.c.v.`011100000`.Signal as T_A_011100000
import sfr.signals.t_c.c.b.v.`111000000`.Signal as T_C_111000000
import sfr.signals.common.bal.BalAspect

/** Interprétation des modèles du paquet, hors du calcul BAL commun.
 * Les inconnus des deux modèles restent inconnus ; un carré fermé annonce un arrêt. */
internal object BalNeighbours {
    fun aspect(next: SignalNeighbour?): BalAspect? {
        if (next == null) return null
        next.of(T_A_011100000.model)?.let { return BalAspect.valueOf(it.aspect.name) }
        next.of(T_C_111000000.model)?.let { return it.aspect.bal ?: BalAspect.S }
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
