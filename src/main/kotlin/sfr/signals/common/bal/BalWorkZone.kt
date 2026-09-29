package sfr.signals.common.bal

import nimby.*

/** Coverage is derived from the saved sources, never from another derived signal.
 * Only follow observed, unambiguous downstream BAL links. Local restrictive
 * indications still take precedence in BalRules. */
internal object BalWorkZone {
    fun apply(signals: List<Signal>, types: List<SignalType>): List<Signal> {
        val declarations = types.associateBy { it.id }
        val byId = signals.associateBy { it.id }
        val covered = mutableSetOf<Long>()
        for (source in signals) {
            if (declarations[source.type] == null || source.settingsStatus != SettingsStatus.Present ||
                source.settings["greenFlashWork"] != true) continue
            var current = source
            val visited = mutableSetOf(source.id)
            for (block in 0 until BalPanel.workBlocks.read(source.settings)) {
                if (!current.observation.fresh || !current.observation.routeKnown || current.settingsStatus == SettingsStatus.Unavailable) break
                val next = byId[current.nextSignal] ?: break
                if (declarations[next.type] == null || !visited.add(next.id) || next.settingsStatus == SettingsStatus.Unavailable) break
                covered.add(next.id)
                current = next
            }
        }
        return signals.map { signal ->
            if (signal.id !in covered) signal else {
                val settings = if (signal.settingsStatus == SettingsStatus.Absent)
                    requireNotNull(declarations[signal.type]).checkboxes.associate { it.name to it.defaultValue } else signal.settings
                signal.copy(settings = settings + ("greenFlashWork" to true), settingsStatus = SettingsStatus.Present)
            }
        }
    }
}
