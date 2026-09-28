package sfr.signals.carreavertissement

/** Réglage propre au Carré ; aucune option BAL n'est lue par ce modèle. */
internal object CarreSettings {
    fun permitsOpening(values: Map<String, Boolean>) =
        values[CarrePanel.automaticOpening.name] ?: CarrePanel.automaticOpening.defaultValue
}
