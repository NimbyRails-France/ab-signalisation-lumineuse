package sfr.signals.t_c.c.b.v.`111001000`

import sfr.signals.common.bal.BalDiagnostics

internal object Diagnostics {
    fun isFault(decision: Decision) = decision.aspect == Aspect.Unknown ||
        decision.reason == Reason.SelectedRouteUnavailable ||
        decision.reason == Reason.ConflictingManoeuvreMarkers ||
        decision.reason.bal?.let(BalDiagnostics::isFault) == true
}
