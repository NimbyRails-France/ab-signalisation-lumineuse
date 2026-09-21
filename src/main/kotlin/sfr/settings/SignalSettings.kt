package sfr.settings

/** Equipement et conditions declares pour un signal. */
internal data class SignalSettings(
    val active: Boolean = false,
    val greenFlash: Boolean = false,
    val yellowFlash: Boolean = false,
    val redFlash: Boolean = false,
    val preannouncement: Boolean = false,
    val reducedAnnouncement: Boolean = false,
    val work160: Boolean = false,
    val redFlashUseDeclared: Boolean = false,
    val endOfBal: Boolean = false,
    val redFlashConditionActive: Boolean = false
) {
    fun asMap() = mapOf(
        "active" to active,
        "greenFlash" to greenFlash,
        "yellowFlash" to yellowFlash,
        "redFlash" to redFlash,
        "preannouncement" to preannouncement,
        "reducedAnnouncement" to reducedAnnouncement,
        "work160" to work160,
        "redFlashUseDeclared" to redFlashUseDeclared,
        "endOfBal" to endOfBal,
        "redFlashConditionActive" to redFlashConditionActive
    )
    companion object {
        fun from(values: Map<String, Boolean>) = SignalSettings(
            active = values["active"] == true,
            greenFlash = values["greenFlash"] == true,
            yellowFlash = values["yellowFlash"] == true,
            redFlash = values["redFlash"] == true,
            preannouncement = values["preannouncement"] == true,
            reducedAnnouncement = values["reducedAnnouncement"] == true,
            work160 = values["work160"] == true,
            redFlashUseDeclared = values["redFlashUseDeclared"] == true,
            endOfBal = values["endOfBal"] == true,
            redFlashConditionActive = values["redFlashConditionActive"] == true
        )
    }
}
