package sfr.driving

import nimby.*
import sfr.signalling.*
import sfr.driving.DrivingLimits.RED_PASSING_SPEED
import sfr.driving.DrivingLimits.ON_SIGHT_SPEED
import sfr.driving.DrivingLimits.GREEN_SPEED

internal object DrivingInstructions {
    fun fromDecision(decision: SignalDecision): DrivingRule? = when (decision.aspect) {
        Aspect.Inactive -> null
        Aspect.VL -> DrivingRule(flags = setOf(DrivingFlag.Clear))
        Aspect.Unknown -> DrivingRule(0.0, flags = setOf(DrivingFlag.Stop))
        Aspect.S -> DrivingRule(0.0, flags = if (decision.reason == Reason.BlockOccupied)
            setOf(DrivingFlag.Stop, DrivingFlag.OnSight, DrivingFlag.StopThenProceed) else setOf(DrivingFlag.Stop))
        Aspect.A -> DrivingRule(0.0, signalsAhead = 1, flags = setOf(DrivingFlag.FollowTarget))
        Aspect.YellowFlash -> DrivingRule(0.0, signalsAhead = 2, flags = setOf(DrivingFlag.FollowTarget, DrivingFlag.CancelAtNextClear))
        Aspect.GreenFlash -> DrivingRule(GREEN_SPEED, signalsAhead = 1, flags = setOf(DrivingFlag.HoldToClear))
        Aspect.RedFlash -> DrivingRule(RED_PASSING_SPEED, flags = setOf(DrivingFlag.OnSight))
    }
}
