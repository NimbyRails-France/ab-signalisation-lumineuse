package sfr.signals.t_c.c.b.v.`111000000`

import nimby.*

internal object Textures {
    private const val folder = "imgs/t_c/c/b/v/111000000/"
    val catalogue = (0..10).map { folder + "tex${it.toString().padStart(2, '0')}.svg" } + (folder + "xx.svg")
    private val frames = mapOf(
        Aspect.Unknown to steady(folder + "xx.svg"),
        Aspect.Closed to steady(folder + "tex01.svg"),
        Aspect.VL to steady(folder + "tex02.svg"),
        Aspect.A to steady(folder + "tex03.svg"),
        Aspect.S to steady(folder + "tex04.svg"),
        Aspect.GreenFlash to blink(folder + "tex05.svg", folder + "tex06.svg", everyMs = 500),
        Aspect.YellowFlash to blink(folder + "tex07.svg", folder + "tex08.svg", everyMs = 500),
        Aspect.RedFlash to blink(folder + "tex09.svg", folder + "tex10.svg", everyMs = 500)
    )
    fun forAspect(aspect: Aspect): SignalAnimation = frames.getValue(aspect)
}
