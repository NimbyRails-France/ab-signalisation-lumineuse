package sfr.signals.bal

import nimby.*

/** Le mod choisit les SVG et la cadence. Le SDK calcule et affiche la phase
 * sur l'horloge du jeu, y compris quand la simulation est en pause ou accélérée.
 * Les descriptions sont créées une seule fois, pas à chaque observation. */
internal object BalTextures {
    private const val folder = "imgs/ca/sem_bal/"
    // Ordre du catalogue historique : les parties enregistrent ces indices.
    // Même les images non utilisées par une règle doivent garder leur place.
    val catalogue = (0..10).map { folder + "tex${it.toString().padStart(2, '0')}.svg" } + (folder + "xx.svg")
    private val unknown = steady(folder + "xx.svg")
    private val clear = steady(folder + "tex02.svg")
    private val warning = steady(folder + "tex03.svg")
    private val stop = steady(folder + "tex04.svg")
    private val greenFlash = blink(folder + "tex05.svg", folder + "tex06.svg", everyMs = 500)
    private val yellowFlash = blink(folder + "tex07.svg", folder + "tex08.svg", everyMs = 500)
    private val redFlash = blink(folder + "tex09.svg", folder + "tex10.svg", everyMs = 500)

    fun forAspect(aspect: BalAspect): SignalAnimation = when (aspect) {
        BalAspect.Unknown -> unknown
        BalAspect.VL -> clear
        BalAspect.A -> warning
        BalAspect.S -> stop
        BalAspect.GreenFlash -> greenFlash
        BalAspect.YellowFlash -> yellowFlash
        BalAspect.RedFlash -> redFlash
    }
}
