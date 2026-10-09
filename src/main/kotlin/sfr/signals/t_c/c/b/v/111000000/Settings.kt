package sfr.signals.t_c.c.b.v.`111000000`

import sfr.signals.common.bal.BalSettings

internal data class Settings(val bal: BalSettings, val forceClosed: Boolean) {
    companion object {
        fun from(values: Map<String, Boolean>) = Settings(
            BalSettings.from(values), values[Panel.forceClosed.name] == true)
    }
}
