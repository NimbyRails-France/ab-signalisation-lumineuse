package sfr.signals.t_c.c.b.v.`101000000`

import nimby.steady

/** Associe les indications du Carré à ses SVG ; ne décide pas d'une ouverture. */
internal object Textures {
    private const val folder = "imgs/t_c/c/b/v/101000000/"
    // Ordre explicite des états utilisés par cette référence.
    val catalogue = listOf("tex00.svg", "tex01.svg", "tex03.svg", "xx.svg").map { folder + it }
    private val closed = steady(folder + "tex01.svg")
    private val warning = steady(folder + "tex03.svg")
    fun forAspect(aspect: Aspect) = when(aspect) {
        Aspect.Closed -> closed
        Aspect.Warning -> warning
    }
    fun path(aspect: Aspect): String = forAspect(aspect).first
}
