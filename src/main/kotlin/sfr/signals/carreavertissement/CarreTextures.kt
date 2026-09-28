package sfr.signals.carreavertissement

/** Associe les indications du Carré à ses SVG ; ne décide pas d'une ouverture. */
internal object CarreTextures {
    fun path(aspect: CarreAspect, simulationMs: Long, halfPeriodMs: Long): String {
        val file = when {
            simulationMs < 0 || halfPeriodMs !in 100..10000 -> "xx.svg"
            aspect == CarreAspect.Closed -> "tex01.svg"
            else -> "tex03.svg"
        }
        return "imgs/cc/cs_a/$file"
    }
}
