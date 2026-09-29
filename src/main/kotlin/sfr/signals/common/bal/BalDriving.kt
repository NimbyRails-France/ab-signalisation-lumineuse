package sfr.signals.common.bal

import nimby.*
import sfr.signals.common.bal.BalSpeeds.RED_PASSING_SPEED
import sfr.signals.common.bal.BalSpeeds.ON_SIGHT_SPEED
import sfr.signals.common.bal.BalSpeeds.GREEN_SPEED
import sfr.signals.common.bal.BalSpeeds.REOPENED_PASSING_SPEED

/** Traduit uniquement la décision BAL en consigne. Le SDK exécute le freinage,
 * les limites et leur libération ; les vitesses restent définies dans BalSpeeds. */
internal object BalDriving {
    fun fromDecision(decision: BalDecision): DrivingRule? = when(decision.aspect) {
        BalAspect.VL -> AutomaticDriving.clear()
        BalAspect.Unknown -> AutomaticDriving.stop()
        BalAspect.S -> if(decision.reason==BalReason.BlockOccupied)
            AutomaticDriving.restrictedUntilNextSignal(0.0,ON_SIGHT_SPEED,stopFirst=true)
            else AutomaticDriving.stop()
        // La réouverture du panneau cible conserve la vitesse d'approche reçue.
        BalAspect.A -> AutomaticDriving.announceStop(1,REOPENED_PASSING_SPEED,passableHere=true,followTargetSpeed=true)
        BalAspect.YellowFlash -> AutomaticDriving.announceStop(2,REOPENED_PASSING_SPEED,
            passableHere=true,followTargetSpeed=true,cancelAtNextClear=true)
        BalAspect.GreenFlash -> AutomaticDriving.limitUntilClearThenRear(GREEN_SPEED,1,passableHere=true)
        BalAspect.RedFlash -> AutomaticDriving.restrictedUntilNextSignal(RED_PASSING_SPEED,ON_SIGHT_SPEED,stopFirst=false)
    }
}
