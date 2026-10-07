package sfr.signals.carreavertissement

import nimby.steady

/** Associe les indications du Carré à ses SVG ; ne décide pas d'une ouverture. */
internal object CarreTextures {
    // Indices du catalogue publié, conservés pour les signaux déjà construits.
    val catalogue = listOf("tex00.svg", "tex01.svg", "tex03.svg", "xx.svg").map { "imgs/cc/cs_a/$it" }
    private val closed = steady("imgs/cc/cs_a/tex01.svg")
    private val warning = steady("imgs/cc/cs_a/tex03.svg")
    fun forAspect(aspect: CarreAspect) = when(aspect) {
        CarreAspect.Closed -> closed
        CarreAspect.Warning -> warning
    }
    fun path(aspect: CarreAspect): String = forAspect(aspect).first
}
