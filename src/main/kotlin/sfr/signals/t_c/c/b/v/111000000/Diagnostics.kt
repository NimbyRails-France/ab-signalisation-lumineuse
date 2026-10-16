package sfr.signals.t_c.c.b.v.`111000000`

import sfr.signals.common.bal.BalDiagnostics

internal object Diagnostics {
    // Une attente d'approche est un carré fermé normal, pas un défaut BAL.
    fun isFault(decision: Decision) = decision.aspect == Aspect.Unknown ||
        decision.reason.bal?.let(BalDiagnostics::isFault) == true
}
