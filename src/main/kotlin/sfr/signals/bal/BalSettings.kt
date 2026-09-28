package sfr.signals.bal

/** Les quatre réglages du modèle. Aucun ancien nom ni profil à convertir.
 * Les libellés et valeurs par défaut de l'interface sont dans BalPanel. */
internal data class BalSettings(
    val greenFlashBlock: Boolean = false,
    val greenFlashWork: Boolean = false,
    val yellowFlashEnabled: Boolean = false,
    val redFlashEnabled: Boolean = false
) {
    fun asMap() = mapOf(
        "greenFlashBlock" to greenFlashBlock,
        "greenFlashWork" to greenFlashWork,
        "yellowFlashEnabled" to yellowFlashEnabled,
        "redFlashEnabled" to redFlashEnabled
    )
    companion object {
        fun from(values: Map<String, Boolean>) = BalSettings(
            greenFlashBlock = values["greenFlashBlock"] == true,
            greenFlashWork = values["greenFlashWork"] == true,
            yellowFlashEnabled = values["yellowFlashEnabled"] == true,
            redFlashEnabled = values["redFlashEnabled"] == true
        )
    }
}
