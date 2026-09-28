package sfr.signals.carreavertissement

/** Une indication de Carré ne peut pas recevoir un motif ou un état BAL. */
internal typealias CarreDecision = nimby.Indication<CarreAspect, CarreReason>
