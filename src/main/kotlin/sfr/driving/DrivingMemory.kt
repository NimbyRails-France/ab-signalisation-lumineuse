package sfr.driving

import nimby.Constraint
import sfr.driving.DrivingLimits.ON_SIGHT_SPEED
import sfr.driving.DrivingLimits.GREEN_SPEED
import kotlin.math.min

/** Annonces et restrictions reçues par un train sur un trajet donné. */
internal class DrivingMemory {
    private data class Announcement(
        val signal: Long,
        var target: Double,
        var approach: Double,
        var open: Boolean = false
    )

    private val announcements = mutableListOf<Announcement>()
    private val restrictions = mutableListOf<Constraint>()
    private var greenTarget: Double? = null
    private var greenExit: Double? = null
    var sessionId: Long = 0
        private set

    fun reset(sessionId: Long) {
        this.sessionId = sessionId
        announcements.clear()
        restrictions.clear()
        greenTarget = null
        greenExit = null
    }

    fun announceStop(signal: Long, target: Double, approach: Double = ON_SIGHT_SPEED) {
        require(signal != 0L && target.isFinite() && approach.positive() && approach <= ON_SIGHT_SPEED)
        val existing = announcements.find { it.signal == signal }
        if (existing != null) {
            existing.target = target
            existing.approach = min(existing.approach, approach)
        } else {
            announcements.add(Announcement(signal, target, approach))
        }
    }

    fun observeAnnouncedSignal(signal: Long, open: Boolean) {
        announcements.find { it.signal == signal }?.open = open
    }

    fun passSignal(signal: Long) {
        announcements.removeAll { it.signal == signal }
    }

    fun setRestriction(restriction: Constraint) {
        require(restriction.valid())
        removeRestriction(restriction.source)
        restrictions.add(restriction)
    }

    fun removeRestriction(source: Long) {
        restrictions.removeAll { it.source == source }
    }

    fun receiveGreenFlash(next: Double) {
        require(next.isFinite())
        greenTarget = greenTarget?.let { min(it, next) } ?: next
        greenExit = null
    }

    fun passGreenExit(position: Double) {
        require(position.isFinite())
        if (greenTarget != null && greenExit == null) greenExit = position
    }

    fun constraints(head: Double, length: Double): List<Constraint> {
        require(head.isFinite() && length.positive())
        val result = restrictions.toMutableList()
        announcements.forEach { announcement ->
            result.add(Constraint(
                source = announcement.signal,
                beginM = announcement.target,
                endM = Double.MAX_VALUE,
                speedMps = if (announcement.open) announcement.approach else 0.0,
                releaseByRear = false
            ))
        }
        // Le nez passé ne suffit pas : seule la queue libère la sortie.
        if (greenExit?.let { head - length > it } == true) {
            greenTarget = null
            greenExit = null
        }
        greenTarget?.let { target ->
            result.add(Constraint(-1L, target, Double.MAX_VALUE, GREEN_SPEED, releaseByRear = true))
        }
        return result
    }
}
