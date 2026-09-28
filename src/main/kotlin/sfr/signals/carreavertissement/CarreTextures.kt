package sfr.signals.carreavertissement

/** Associe les indications du Carré à ses SVG ; ne décide pas d'une ouverture. */
internal object CarreTextures {
    // Indices du catalogue publié, conservés pour les signaux déjà construits.
    val catalogue = listOf("tex00.svg", "tex01.svg", "tex03.svg", "xx.svg").map { "imgs/cc/cs_a/$it" }
    fun path(aspect: CarreAspect): String {
        val file = when (aspect) {
            CarreAspect.Closed -> "tex01.svg"
            CarreAspect.Warning -> "tex03.svg"
        }
        return "imgs/cc/cs_a/$file"
    }
}
