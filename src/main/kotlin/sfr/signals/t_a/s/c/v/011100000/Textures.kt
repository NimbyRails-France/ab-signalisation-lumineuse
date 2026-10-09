package sfr.signals.t_a.s.c.v.`011100000`

import nimby.*

/** Le mod choisit les SVG et la cadence. Le SDK calcule et affiche la phase
 * sur l'horloge du jeu, y compris quand la simulation est en pause ou accélérée.
 * Les descriptions sont créées une seule fois, pas à chaque observation. */
internal object Textures {
    private const val folder = "imgs/t_a/s/c/v/011100000/"
    // Catalogue de cette référence : tex01 n'existe pas dans le tableau.
    // Les états sont déclarés explicitement, sans ancien emplacement réservé.
    val catalogue = listOf(folder + "tex00.svg") +
        (2..10).map { folder + "tex${it.toString().padStart(2, '0')}.svg" } + (folder + "xx.svg")
    private val unknown = steady(folder + "xx.svg")
    private val clear = steady(folder + "tex02.svg")
    private val warning = steady(folder + "tex03.svg")
    private val stop = steady(folder + "tex04.svg")
    private val greenFlash = blink(folder + "tex05.svg", folder + "tex06.svg", everyMs = 500)
    private val yellowFlash = blink(folder + "tex07.svg", folder + "tex08.svg", everyMs = 500)
    private val redFlash = blink(folder + "tex09.svg", folder + "tex10.svg", everyMs = 500)

    fun forAspect(aspect: Aspect): SignalAnimation = when (aspect) {
        Aspect.Unknown -> unknown
        Aspect.VL -> clear
        Aspect.A -> warning
        Aspect.S -> stop
        Aspect.GreenFlash -> greenFlash
        Aspect.YellowFlash -> yellowFlash
        Aspect.RedFlash -> redFlash
    }
}
