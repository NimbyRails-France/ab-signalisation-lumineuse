package sfr.signals.bal

internal typealias BalDecision = nimby.Indication<BalAspect, BalReason>

internal fun sfr.signals.common.bal.BalDecision.toSemaphore() =
    BalDecision(BalAspect.valueOf(aspect.name), BalReason.valueOf(reason.name))

internal fun BalDecision.common() = sfr.signals.common.bal.BalDecision(
    sfr.signals.common.bal.BalAspect.valueOf(aspect.name), sfr.signals.common.bal.BalReason.valueOf(reason.name))
