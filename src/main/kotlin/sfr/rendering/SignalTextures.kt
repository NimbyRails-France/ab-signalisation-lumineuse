package sfr.rendering

import sfr.signalling.Aspect

internal object SignalTextures {
    fun path(aspect: Aspect, simulationMs: Long, halfPeriodMs: Long = 500): String {
        val folder = "imgs/ca/sem_bal/"
        if (simulationMs < 0 || halfPeriodMs !in 100..10000) return folder + "xx.svg"
        val lit = (simulationMs / halfPeriodMs) % 2 == 0L
        val file = when (aspect) {
            Aspect.Unknown -> "xx"
            Aspect.Inactive -> "tex00"
            Aspect.VL -> "tex02"
            Aspect.A -> "tex03"
            Aspect.S -> "tex04"
            Aspect.GreenFlash -> if (lit) "tex05" else "tex06"
            Aspect.YellowFlash -> if (lit) "tex07" else "tex08"
            Aspect.RedFlash -> if (lit) "tex09" else "tex10"
        }
        return "$folder$file.svg"
    }
}
