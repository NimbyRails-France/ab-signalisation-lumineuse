package sfr.signals.common.bal

import nimby.Checkbox

internal enum class CarreBalReturnMode { Semaphore, Square }

/** Réglages métier partagés ; le panneau reste local au modèle. forceClosed
 * conserve la préférence mémorisée, appliquée uniquement en régime Sémaphore. */
internal data class CarreBalSettings(val bal: BalSettings, val forceClosed: Boolean, val mode: CarreBalReturnMode) {
    companion object {
        fun fixedSquare(values: Map<String, Boolean>) = CarreBalSettings(
            BalSettings.from(values), forceClosed = false, mode = CarreBalReturnMode.Square)

        fun from(values: Map<String, Boolean>, forceClosed: Checkbox, returnToSemaphore: Checkbox) = CarreBalSettings(
            BalSettings.from(values), values[forceClosed.name] ?: forceClosed.defaultValue,
            if (values[returnToSemaphore.name] ?: returnToSemaphore.defaultValue)
                CarreBalReturnMode.Semaphore else CarreBalReturnMode.Square)
    }
}
