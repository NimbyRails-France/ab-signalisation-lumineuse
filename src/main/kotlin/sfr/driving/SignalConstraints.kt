package sfr.driving

import nimby.Constraint
import sfr.signalling.Aspect
import sfr.driving.DrivingLimits.RED_PASSING_SPEED
import sfr.driving.DrivingLimits.GREEN_SPEED

internal object SignalConstraints {
    fun fromSignal(aspect: Aspect, source: Long, position: Double, next: Double? = null,
        announcedStop: Double? = null, announcedTarget: Aspect = Aspect.S): Constraint? {
        if (source == 0L || !position.isFinite()) return null
        fun target(at: Double, speed: Double, rear: Boolean = false) = Constraint(source, at, Double.MAX_VALUE, speed, rear)
        return when (aspect) {
            Aspect.S -> target(position, 0.0)
            Aspect.RedFlash -> target(position, RED_PASSING_SPEED)
            Aspect.A -> if (next != null && next.isFinite() && next > position && announcedTarget in setOf(Aspect.S, Aspect.RedFlash))
                target(next, if (announcedTarget == Aspect.RedFlash) RED_PASSING_SPEED else 0.0) else null
            Aspect.YellowFlash -> if (announcedTarget == Aspect.S && next != null && announcedStop != null &&
                next.isFinite() && announcedStop.isFinite() && next > position && announcedStop > next) target(announcedStop, 0.0) else null
            Aspect.GreenFlash -> if (next != null && next.isFinite() && next > position) target(next, GREEN_SPEED, true) else null
            else -> null
        }
    }
}
