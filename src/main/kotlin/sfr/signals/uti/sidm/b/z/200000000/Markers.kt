package sfr.signals.uti.sidm.b.z.`200000000`

import nimby.*

/** Balises passives : elles décrivent le parcours et n'accordent aucun mouvement.
 * Une image par catalogue distingue leur identité sans modifier le catalogue
 * historique préparé de cette famille. Aucun script de réservation n'est installé. */
internal object Markers {
    const val T32_TYPE = "sfr.uti.sidm.b.z.200000000.t32"
    const val T33_TYPE = "sfr.uti.sidm.b.z.200000000.t33"
    const val T32_TEXTURES = "sfr_uti_sidm_b_z_200000000_t32"
    const val T33_TEXTURES = "sfr_uti_sidm_b_z_200000000_t33"

    enum class Aspect { Passive }
    enum class Reason { Passive }
    private val indication = Indication(Aspect.Passive, Reason.Passive)

    private fun marker(type: String, textureSet: String, image: String, titleKey: String) = signalModel(
        SignalType(type, tr(titleKey), textureSet), fallback = indication, invalidNetwork = indication
    ) {
        construction(listOf(image), name = tr(titleKey), kind = "marker", size = 4, left = true)
        evaluate { _, _ -> indication }
        rules { indication }
        appearance { steady(image) }
        activeWhen { false }
        aspectNames { "marqueur passif" }
        reasonNames { "aucune consigne de conduite" }
    }

    val t32 = marker(T32_TYPE, T32_TEXTURES, Textures.MANOEUVRE,
        "signal.uti.sidm.b.z.200000000.t32.name")
    val t33 = marker(T33_TYPE, T33_TEXTURES, Textures.MANOEUVRE_CLIGNOTANTE,
        "signal.uti.sidm.b.z.200000000.t33.name")
}
