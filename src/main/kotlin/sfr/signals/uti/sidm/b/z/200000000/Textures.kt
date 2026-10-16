package sfr.signals.uti.sidm.b.z.`200000000`

/** Ressources UTI SIDM. Les trois correspondances ci-dessous sont confirmées
 * par le créateur. T32 et T33 disposent chacun d'un modèle passif dans Markers.
 * Le catalogue complet reste une préparation de ressources non enregistrée.
 */
internal object Textures {
    private const val folder = "imgs/uti/sidm/b/z/200000000/"

    const val BUTOIR = folder + "tex31.png"
    const val MANOEUVRE = folder + "tex32.png"
    // La balise elle-même reste fixe ; elle demande M cli au panneau 111001000.
    const val MANOEUVRE_CLIGNOTANTE = folder + "tex33.png"

    // Ordre explicite et noms d'origine. Les autres images restent sans
    // signification attribuée ; leur numéro ne constitue pas une vitesse.
    val catalogue = listOf(
        folder + "tex00.png",
        BUTOIR,
        MANOEUVRE,
        MANOEUVRE_CLIGNOTANTE,
        folder + "tex34.png",
        folder + "tex35.png",
        folder + "tex36.png",
        folder + "tex37.png",
        folder + "tex38.png",
        folder + "tex39.png",
        folder + "tex40.png",
        folder + "tex41.png",
        folder + "tex42.png",
        folder + "tex43.png",
        folder + "tex44.png",
        folder + "tex45.png",
        folder + "tex46.png",
        folder + "texv.png",
        folder + "xx.png",
    )
}
