package sfr.signals.carreavertissement

/** États constructibles de ce modèle uniquement. Closed n'est pas un sémaphore. */
internal enum class CarreAspect(val label: String) {
    Closed("Carré"),
    Warning("Carré simple Avertissement : A")
}
