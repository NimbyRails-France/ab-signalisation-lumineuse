package sfr.signalling

import nimby.Occupancy
import sfr.settings.SignalSettings

internal data class SignalDecision(val aspect: Aspect, val reason: Reason)

/** Une observation absente ne signifie jamais voie libre. */
internal data class SignalObservation(
    val block: Occupancy = Occupancy.Unknown,
    val fresh: Boolean = false,
    val routeKnown: Boolean = false,
    val forcedStop: Boolean = false,
    val lampFailed: Boolean = false,
    val redFlashCondition: Boolean = false,
    val next: Aspect = Aspect.Unknown
)

internal data class NetworkSignal(
    val id: Long = 0,
    val nextSignal: Long = 0,
    val settings: SignalSettings = SignalSettings(),
    val observation: SignalObservation = SignalObservation()
)
