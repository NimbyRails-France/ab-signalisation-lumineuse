package sfr.signals.t_a.s.c.v.`011100000`

internal typealias Decision = nimby.Indication<Aspect, Reason>

internal fun sfr.signals.common.bal.BalDecision.toSemaphore() =
    Decision(Aspect.valueOf(aspect.name), Reason.valueOf(reason.name))

internal fun Decision.common() = sfr.signals.common.bal.BalDecision(
    sfr.signals.common.bal.BalAspect.valueOf(aspect.name), sfr.signals.common.bal.BalReason.valueOf(reason.name))
