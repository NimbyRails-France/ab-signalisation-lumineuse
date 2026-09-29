package sfr.signals.carrebal

import nimby.*

internal object CarreBalTextures {
    private const val folder = "imgs/cc/cc_sma/"
    val catalogue = (0..10).map { folder + "tex${it.toString().padStart(2, '0')}.svg" } + (folder + "xx.svg")
    private val frames = mapOf(
        CarreBalAspect.Unknown to steady(folder + "xx.svg"),
        CarreBalAspect.Closed to steady(folder + "tex01.svg"),
        CarreBalAspect.VL to steady(folder + "tex02.svg"),
        CarreBalAspect.A to steady(folder + "tex03.svg"),
        CarreBalAspect.S to steady(folder + "tex04.svg"),
        CarreBalAspect.GreenFlash to blink(folder + "tex05.svg", folder + "tex06.svg", everyMs = 500),
        CarreBalAspect.YellowFlash to blink(folder + "tex07.svg", folder + "tex08.svg", everyMs = 500),
        CarreBalAspect.RedFlash to blink(folder + "tex09.svg", folder + "tex10.svg", everyMs = 500)
    )
    fun forAspect(aspect: CarreBalAspect): SignalAnimation = frames.getValue(aspect)
}
