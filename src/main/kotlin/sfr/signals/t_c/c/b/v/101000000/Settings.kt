package sfr.signals.t_c.c.b.v.`101000000`

/** Réglage propre au Carré ; aucune option BAL n'est lue par ce modèle. */
internal object Settings {
    fun permitsOpening(values: Map<String, Boolean>) =
        values[Panel.automaticOpening.name] ?: Panel.automaticOpening.defaultValue
}
