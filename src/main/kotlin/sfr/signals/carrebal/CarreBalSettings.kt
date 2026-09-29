package sfr.signals.carrebal

import sfr.signals.common.bal.BalSettings

internal data class CarreBalSettings(val bal: BalSettings, val forceClosed: Boolean) {
    companion object {
        fun from(values: Map<String, Boolean>) = CarreBalSettings(
            BalSettings.from(values), values[CarreBalPanel.forceClosed.name] == true)
    }
}
