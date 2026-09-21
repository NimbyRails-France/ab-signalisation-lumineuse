package sfr.driving

import nimby.*
import sfr.driving.DrivingLimits.ON_SIGHT_SPEED
import kotlin.math.*

internal fun Double.nonnegative() = isFinite() && this >= 0
internal fun Double.positive() = isFinite() && this > 0
internal fun Constraint.valid() = source != 0L && beginM.isFinite() && endM.isFinite() && endM >= beginM && speedMps.nonnegative()


internal object DrivingModel {
    /** Approximation sur voie horizontale : réaction, puis décélération constante. */
    private fun brakingCeiling(target: Double, distance: Double, deceleration: Double, response: Double): Double {
        if (distance <= 0) return target
        val responseSpeed = deceleration * response
        return max(target, sqrt(responseSpeed * responseSpeed + target * target + 2 * deceleration * distance) - responseSpeed)
    }

    fun plan(vehicle: Vehicle, settings: DrivingSettings, input: DrivingInput, restrictions: List<Constraint>): DrivingPlan {
        if (!input.fresh || !input.routeKnown || !input.headM.isFinite() || !input.speedMps.nonnegative() || !input.lineSpeedMps.positive() ||
            !vehicle.maxSpeedMps.positive() || !vehicle.serviceBrakingMps2.positive() || !vehicle.emptyMassKg.positive() || !vehicle.extraMassKg.nonnegative() ||
            !vehicle.lengthM.positive() || !vehicle.maxAccelerationMps2.nonnegative() || !vehicle.powerW.nonnegative() || !vehicle.tractiveEffortN.nonnegative() ||
            !settings.brakeUse.positive() || settings.brakeUse > 1 || !settings.responseSeconds.nonnegative() || !settings.marginM.nonnegative()) return DrivingPlan()
        val mass = vehicle.emptyMassKg + vehicle.extraMassKg
        val deceleration = vehicle.serviceBrakingMps2 * settings.brakeUse * (vehicle.emptyMassKg / mass)
        if (!mass.positive() || !deceleration.positive()) return DrivingPlan()
        var ceiling = min(vehicle.maxSpeedMps, input.lineSpeedMps)
        var source = 0L
        for (restriction in restrictions) {
            if (!restriction.valid()) return DrivingPlan()
            val releasePosition = if (restriction.releaseByRear) input.headM - vehicle.lengthM else input.headM
            if (releasePosition > restriction.endM) continue
            val limit = brakingCeiling(restriction.speedMps, restriction.beginM - input.headM - settings.marginM, deceleration, settings.responseSeconds)
            if (limit < ceiling) {
                ceiling = limit
                source = restriction.source
            }
        }
        if (input.onSight) {
            val visible = input.visibleClearM ?: return DrivingPlan()
            if (!visible.nonnegative()) return DrivingPlan()
            val limit = min(ON_SIGHT_SPEED, brakingCeiling(0.0, visible - settings.marginM, deceleration, settings.responseSeconds))
            if (limit < ceiling) {
                ceiling = limit
                source = 0
            }
        }
        if (!ceiling.nonnegative()) return DrivingPlan()
        val traction = minOf(vehicle.maxAccelerationMps2, vehicle.tractiveEffortN / mass, vehicle.powerW / (mass * max(input.speedMps, 0.1)))
        val braking = input.speedMps > ceiling
        val acceleration = when {
            braking -> -deceleration
            input.speedMps < ceiling -> traction
            else -> 0.0
        }
        return DrivingPlan(
            available = true,
            speedCeilingMps = ceiling,
            serviceDecelerationMps2 = deceleration,
            accelerationMps2 = acceleration,
            brakingRequired = braking,
            limitingSource = source
        )
    }

}
