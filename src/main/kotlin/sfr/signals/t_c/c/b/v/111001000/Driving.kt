package sfr.signals.t_c.c.b.v.`111001000`

import nimby.*
import sfr.signals.common.bal.*

internal object Driving {
    // Plafond dès ce panneau, conservé jusqu'au dégagement d'un signal ouvert.
    // La réservation native reste nécessaire ; aucune marche à vue ne la remplace.
    private val manoeuvre = AutomaticDriving.limitUntilClearThenRear(
        30.0 / 3.6, signalsAhead = 0, passableHere = true)

    fun driving(value: Decision): DrivingRule? = when (value.aspect) {
        Aspect.Closed -> AutomaticDriving.stop()
        Aspect.White, Aspect.WhiteFlash -> manoeuvre
        else -> BalDriving.fromDecision(BalDecision(requireNotNull(value.aspect.bal), requireNotNull(value.reason.bal)))
    }
}
