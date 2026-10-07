package sfr.signals.common.bal

import nimby.*

/** Coverage is derived from the saved sources, never from another derived signal.
 * Only follow observed, unambiguous downstream BAL links. Local restrictive
 * indications still take precedence in BalRules. */
internal class BalWorkZone(types: List<SignalType>) {
    private val declarations = types.associateBy { it.id }
    private val defaults = types.associate { type -> type.id to type.checkboxes.associate { it.name to it.defaultValue } }

    fun apply(signals: List<Signal>): List<Signal> {
        // Most observations have no source with downstream coverage. Avoid
        // building an index/copy of the entire map in that common case.
        val sources = signals.filter { it.type in declarations && it.settingsStatus == SettingsStatus.Present &&
            it.settings["greenFlashWork"] == true && BalPanel.workBlocks.read(it.settings) > 0 }
        if(sources.isEmpty()) return signals
        val byId = HashMap<Long, Int>(signals.size)
        for (index in signals.indices) byId[signals[index].id] = index
        val covered = BooleanArray(signals.size)
        var anyCovered = false
        val remaining = IntArray(signals.size)
        val budgets = Array(BalPanel.workBlocks.maximum + 1) { mutableListOf<Int>() }
        for (source in sources) {
            val index = byId.getValue(source.id)
            val count = BalPanel.workBlocks.read(source.settings)
            remaining[index] = count
            budgets[count].add(index)
        }
        // Le plus grand budget arrive d'abord : une zone qui rejoint une autre
        // n'en reparcourt pas les cantons. Chaque signal est développé au plus
        // une fois, même avec 4096 sources superposées ou une boucle de voies.
        // Seuls les réglages sauvegardés initialisent les budgets ; une valeur
        // dérivée ne devient jamais une nouvelle source de travaux.
        for (budget in budgets.lastIndex downTo 1) {
            for (index in budgets[budget]) {
                if (remaining[index] != budget) continue
                val current = signals[index]
                if (!current.observation.fresh || !current.observation.routeKnown || current.settingsStatus == SettingsStatus.Unavailable) continue
                val nextIndex = byId[current.nextSignal] ?: continue
                val next = signals[nextIndex]
                if (next.type !in declarations || next.settingsStatus == SettingsStatus.Unavailable) continue
                covered[nextIndex] = true
                anyCovered = true
                if (remaining[nextIndex] < budget - 1) {
                    remaining[nextIndex] = budget - 1
                    budgets[budget - 1].add(nextIndex)
                }
            }
        }
        if (!anyCovered) return signals
        return signals.mapIndexed { index, signal ->
            if (!covered[index] || (signal.settingsStatus == SettingsStatus.Present && signal.settings["greenFlashWork"] == true)) signal else {
                val settings = if (signal.settingsStatus == SettingsStatus.Absent)
                    requireNotNull(defaults[signal.type]) else signal.settings
                signal.copy(settings = settings + ("greenFlashWork" to true), settingsStatus = SettingsStatus.Present)
            }
        }
    }
}
