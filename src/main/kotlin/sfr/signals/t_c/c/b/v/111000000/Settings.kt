package sfr.signals.t_c.c.b.v.`111000000`

import sfr.signals.common.bal.CarreBalSettings
import sfr.signals.common.bal.CarreBalReturnMode

internal typealias ReturnMode = CarreBalReturnMode

/** Les valeurs et clés restent déclarées dans le panneau de ce modèle. */
internal object Settings {
    fun from(values: Map<String, Boolean>) =
        CarreBalSettings.from(values, Panel.forceClosed, Panel.returnToSemaphore)
}
