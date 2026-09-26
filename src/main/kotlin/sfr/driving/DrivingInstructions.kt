package sfr.driving

import nimby.*
import sfr.signalling.*
import sfr.driving.DrivingLimits.RED_PASSING_SPEED
import sfr.driving.DrivingLimits.ON_SIGHT_SPEED
import sfr.driving.DrivingLimits.GREEN_SPEED
import sfr.driving.DrivingLimits.REOPENED_PASSING_SPEED

internal object DrivingInstructions {
    fun fromDecision(decision: SignalDecision): DrivingRule? = when (decision.aspect) {
        Aspect.Inactive -> null
        Aspect.VL -> AutomaticDriving.clear()
        Aspect.Unknown -> AutomaticDriving.stop()
        Aspect.S -> if (decision.reason == Reason.BlockOccupied)
            AutomaticDriving.restrictedUntilNextSignal(0.0, ON_SIGHT_SPEED, stopFirst = true)
            else AutomaticDriving.stop()
        // Politique BAL : apres avertissement, une reouverture autorise le
        // passage a 30 au panneau cible. Un jaune est franchissable mais ne
        // libere pas les autres restrictions : il annonce le panneau suivant.
        Aspect.A -> AutomaticDriving.announceStop(1, REOPENED_PASSING_SPEED,
            passableHere = true, followTargetSpeed = true)
        Aspect.YellowFlash -> AutomaticDriving.announceStop(2, REOPENED_PASSING_SPEED,
            passableHere = true, followTargetSpeed = true, cancelAtNextClear = true)
        Aspect.GreenFlash -> AutomaticDriving.limitUntilClearThenRear(GREEN_SPEED, 1, passableHere = true)
        Aspect.RedFlash -> AutomaticDriving.restrictedUntilNextSignal(RED_PASSING_SPEED, ON_SIGHT_SPEED, stopFirst = false)
    }
}
