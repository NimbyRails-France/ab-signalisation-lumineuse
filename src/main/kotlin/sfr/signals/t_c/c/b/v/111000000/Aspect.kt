package sfr.signals.t_c.c.b.v.`111000000`

import nimby.*
import sfr.signals.common.bal.*

internal enum class Aspect(val label: String, val bal: BalAspect?) {
    Unknown("indéterminé", BalAspect.Unknown), VL("VL", BalAspect.VL), A("A", BalAspect.A),
    S("S", BalAspect.S), GreenFlash("VL cli", BalAspect.GreenFlash),
    YellowFlash("A cli", BalAspect.YellowFlash), RedFlash("S cli", BalAspect.RedFlash), Closed("C", null)
}
