package sfr.signals.t_c.c.b.v.`111000000`

internal object Diagnostics {
    fun isFault(decision: Decision) = decision.aspect == Aspect.Unknown
}
